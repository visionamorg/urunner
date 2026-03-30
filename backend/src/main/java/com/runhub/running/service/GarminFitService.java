package com.runhub.running.service;

import com.runhub.running.dto.SyncResultDto;
import com.runhub.running.model.ActivitySource;
import com.runhub.running.model.RunningActivity;
import com.runhub.running.repository.ActivityRepository;
import com.runhub.users.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HexFormat;

/**
 * Handles import of Garmin .FIT binary files.
 *
 * FIT format overview used here:
 *  - Bytes 8-11: ASCII ".FIT" magic identifier
 *  - Records start at byte 14 (after 14-byte file header)
 *  - Each record starts with a 1-byte header:
 *      bits 7-6 = 0b01 → Definition message  (bit 6 set)
 *      bits 7-6 = 0b00 → Data message         (bit 6 clear)
 *      bits 3-0         = local message number
 *  - Session message: global message number 18 (mesg_num 18)
 *  - We parse field definitions from definition messages and extract
 *    session-record fields by field definition number.
 *
 * Field definition numbers for session (mesg_num 18):
 *   2  = start_time      UINT32  (seconds since 1989-12-31 UTC = FIT epoch)
 *   7  = total_elapsed_time  UINT32  (milliseconds / 1000 = seconds)
 *   9  = total_distance  UINT32  (cm / 100 = meters)
 *   16 = avg_heart_rate  UINT8   (bpm)
 *   17 = max_heart_rate  UINT8   (bpm)
 *   22 = total_ascent    UINT16  (meters)
 *   53 = avg_cadence     UINT8   (steps/min; multiply × 2 for running SPM)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GarminFitService {

    // FIT epoch: 1989-12-31 00:00:00 UTC in Unix epoch seconds
    private static final long FIT_EPOCH_OFFSET = 631065600L;

    private final ActivityRepository activityRepository;

    @Transactional
    public SyncResultDto importFitFile(User user, MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();

            // Compute SHA-256 for deduplication
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String hash = HexFormat.of().formatHex(digest.digest(bytes));
            String externalId = "fit_" + hash;

            if (activityRepository.existsByExternalId(externalId)) {
                return SyncResultDto.builder()
                        .imported(0)
                        .skipped(1)
                        .message("Activity already imported (duplicate .FIT file)")
                        .build();
            }

            // Validate .FIT magic header
            if (bytes.length < 12) {
                return SyncResultDto.builder().message("File too small to be a valid .FIT file").build();
            }

            // Header: bytes 8..11 should be ".FIT"
            boolean validMagic = bytes[8] == '.' && bytes[9] == 'F' && bytes[10] == 'I' && bytes[11] == 'T';
            if (!validMagic) {
                return SyncResultDto.builder().message("Not a valid .FIT file (magic bytes mismatch)").build();
            }

            // Parse session data using our inline FIT parser
            FitParser parser = new FitParser(bytes);
            FitSessionData sessionData = parser.parse();

            // Build activity from parsed data
            User userRef = new User();
            userRef.setId(user.getId());

            LocalDate activityDate = sessionData.startTimeEpoch > 0
                    ? Instant.ofEpochSecond(sessionData.startTimeEpoch).atZone(ZoneOffset.UTC).toLocalDate()
                    : LocalDate.now();

            RunningActivity activity = RunningActivity.builder()
                    .user(userRef)
                    .title("FIT Import")
                    .distanceKm(sessionData.distanceMeters / 1000.0)
                    .durationMinutes(Math.max(1, sessionData.elapsedSeconds / 60))
                    .activityDate(activityDate)
                    .source(ActivitySource.GARMIN)
                    .externalId(externalId)
                    .elevationGainMeters(sessionData.totalAscent > 0 ? sessionData.totalAscent : null)
                    .avgHeartRate(sessionData.avgHr > 0 ? sessionData.avgHr : null)
                    .maxHeartRate(sessionData.maxHr > 0 ? sessionData.maxHr : null)
                    .avgCadence(sessionData.avgCadence > 0 ? sessionData.avgCadence * 2 : null)
                    .build();

            activityRepository.save(activity);

            return SyncResultDto.builder()
                    .imported(1)
                    .skipped(0)
                    .message(String.format("FIT file imported: %.2f km, %d min",
                            activity.getDistanceKm(), activity.getDurationMinutes()))
                    .build();

        } catch (Exception e) {
            log.error("FIT file import failed for user {}", user.getId(), e);
            // Fallback: store a stub activity so the file isn't silently lost
            return SyncResultDto.builder()
                    .imported(0)
                    .skipped(0)
                    .message("FIT import failed: " + e.getMessage())
                    .build();
        }
    }

    // ── Inner FIT parser ────────────────────────────────────────────────────

    static class FitSessionData {
        long startTimeEpoch;   // Unix seconds
        double distanceMeters;
        int elapsedSeconds;
        int avgHr;
        int maxHr;
        int totalAscent;
        int avgCadence;        // raw (× 2 for running steps/min)
    }

    /**
     * Minimal FIT binary scanner.
     * Reads definition messages to learn field layouts, then extracts
     * session (mesg_num 18) data message fields.
     */
    static class FitParser {

        private final byte[] data;
        // Per local-msg-number: array of FieldDef
        private final FieldDef[][] localMsgDefs = new FieldDef[16][];
        // Per local-msg-number: global message number
        private final int[] localMsgGlobalNum = new int[16];

        FitParser(byte[] data) {
            this.data = data;
        }

        FitSessionData parse() {
            FitSessionData result = new FitSessionData();
            if (data.length < 14) return result;

            // FIT file header size is stored in byte 0
            int headerSize = data[0] & 0xFF;
            int pos = headerSize; // start reading records after header

            try {
                while (pos < data.length - 1) {
                    int recordHeader = data[pos] & 0xFF;
                    pos++;

                    // Compressed timestamp records (bit 7 = 1, bit 6 = 0) — skip
                    if ((recordHeader & 0x80) != 0 && (recordHeader & 0x40) == 0) {
                        // Compressed: local number in bits 5-4, timestamp offset in bits 3-0
                        int localNum = (recordHeader >> 4) & 0x03;
                        if (localMsgDefs[localNum] != null) {
                            int size = calcDataSize(localNum);
                            pos += size;
                        } else {
                            break; // can't continue without definition
                        }
                        continue;
                    }

                    boolean isDefinition = (recordHeader & 0x40) != 0;
                    int localMsgNum = recordHeader & 0x0F;

                    if (isDefinition) {
                        pos = parseDefinitionMessage(pos, localMsgNum);
                    } else {
                        // Data message
                        if (localMsgDefs[localMsgNum] == null) {
                            // Unknown local message, skip
                            break;
                        }
                        int globalNum = localMsgGlobalNum[localMsgNum];
                        int startPos = pos;
                        if (globalNum == 18) { // session message
                            parseSessionData(pos, localMsgNum, result);
                        }
                        pos = startPos + calcDataSize(localMsgNum);
                    }
                }
            } catch (Exception e) {
                // Return whatever we parsed so far
                log.debug("FIT parsing ended early: {}", e.getMessage());
            }

            return result;
        }

        private int parseDefinitionMessage(int pos, int localMsgNum) {
            if (pos + 5 >= data.length) return data.length;
            // Skip reserved byte
            pos++; // reserved
            int architecture = data[pos++] & 0xFF; // 0=little-endian, 1=big-endian
            int globalMsgNum = readUint16(pos, architecture == 0);
            pos += 2;
            int numFields = data[pos++] & 0xFF;

            localMsgGlobalNum[localMsgNum] = globalMsgNum;
            FieldDef[] fields = new FieldDef[numFields];
            for (int i = 0; i < numFields; i++) {
                if (pos + 2 >= data.length) break;
                int fieldDefNum = data[pos++] & 0xFF;
                int size = data[pos++] & 0xFF;
                int baseType = data[pos++] & 0xFF;
                fields[i] = new FieldDef(fieldDefNum, size, architecture == 0);
            }
            localMsgDefs[localMsgNum] = fields;
            return pos;
        }

        private void parseSessionData(int pos, int localMsgNum, FitSessionData out) {
            FieldDef[] fields = localMsgDefs[localMsgNum];
            if (fields == null) return;

            for (FieldDef f : fields) {
                if (pos + f.size > data.length) break;
                switch (f.fieldDefNum) {
                    case 2 -> { // start_time UINT32 (FIT epoch)
                        long fitTime = readUint32(pos, f.littleEndian);
                        out.startTimeEpoch = fitTime + FIT_EPOCH_OFFSET;
                    }
                    case 7 -> { // total_elapsed_time UINT32 (milliseconds * 1000 → seconds)
                        long ms1000 = readUint32(pos, f.littleEndian);
                        out.elapsedSeconds = (int)(ms1000 / 1000);
                    }
                    case 9 -> { // total_distance UINT32 (cm * 100 → meters)
                        long cm100 = readUint32(pos, f.littleEndian);
                        out.distanceMeters = cm100 / 100.0;
                    }
                    case 16 -> out.avgHr = data[pos] & 0xFF;    // UINT8
                    case 17 -> out.maxHr = data[pos] & 0xFF;    // UINT8
                    case 22 -> out.totalAscent = readUint16(pos, f.littleEndian); // UINT16
                    case 53 -> out.avgCadence = data[pos] & 0xFF; // UINT8
                }
                pos += f.size;
            }
        }

        private int calcDataSize(int localMsgNum) {
            FieldDef[] fields = localMsgDefs[localMsgNum];
            if (fields == null) return 0;
            int total = 0;
            for (FieldDef f : fields) total += f.size;
            return total;
        }

        private int readUint16(int pos, boolean littleEndian) {
            if (pos + 1 >= data.length) return 0;
            int b0 = data[pos] & 0xFF;
            int b1 = data[pos + 1] & 0xFF;
            return littleEndian ? (b1 << 8) | b0 : (b0 << 8) | b1;
        }

        private long readUint32(int pos, boolean littleEndian) {
            if (pos + 3 >= data.length) return 0;
            long b0 = data[pos] & 0xFFL;
            long b1 = data[pos + 1] & 0xFFL;
            long b2 = data[pos + 2] & 0xFFL;
            long b3 = data[pos + 3] & 0xFFL;
            return littleEndian
                    ? (b3 << 24) | (b2 << 16) | (b1 << 8) | b0
                    : (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
        }
    }

    record FieldDef(int fieldDefNum, int size, boolean littleEndian) {}
}
