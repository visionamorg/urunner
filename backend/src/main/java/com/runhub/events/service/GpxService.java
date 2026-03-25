package com.runhub.events.service;

import com.runhub.config.BadRequestException;
import com.runhub.config.ResourceNotFoundException;
import com.runhub.events.dto.EventDto;
import com.runhub.events.model.Event;
import com.runhub.events.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GpxService {

    private static final Path UPLOAD_DIR = Paths.get("uploads/gpx");
    private final EventRepository eventRepository;

    @Transactional
    public EventDto uploadGpx(Long eventId, MultipartFile file) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + eventId));

        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.toLowerCase().endsWith(".gpx")) {
            throw new BadRequestException("Only .gpx files are allowed");
        }

        try {
            Files.createDirectories(UPLOAD_DIR);

            String filename = eventId + "_" + UUID.randomUUID().toString().substring(0, 8) + ".gpx";
            Path target = UPLOAD_DIR.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            // Parse GPX for elevation gain and distance
            GpxStats stats = parseGpx(file.getInputStream());

            event.setRouteGpxUrl("/api/events/" + eventId + "/gpx/download");
            event.setElevationGainMeters(stats.elevationGain);
            if (stats.distanceKm > 0 && (event.getDistanceKm() == null || event.getDistanceKm() == 0)) {
                event.setDistanceKm(Math.round(stats.distanceKm * 100.0) / 100.0);
            }

            eventRepository.save(event);

            EventDto dto = new EventDto();
            dto.setId(event.getId());
            dto.setRouteGpxUrl(event.getRouteGpxUrl());
            dto.setElevationGainMeters(event.getElevationGainMeters());
            dto.setDistanceKm(event.getDistanceKm());
            return dto;
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to process GPX file for event {}", eventId, e);
            throw new BadRequestException("Failed to process GPX file");
        }
    }

    public Path getGpxFile(Long eventId) {
        try {
            return Files.list(UPLOAD_DIR)
                    .filter(p -> p.getFileName().toString().startsWith(eventId + "_"))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("GPX file not found for event: " + eventId));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new ResourceNotFoundException("GPX file not found for event: " + eventId);
        }
    }

    @Transactional
    public void deleteGpx(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + eventId));
        try {
            Files.list(UPLOAD_DIR)
                    .filter(p -> p.getFileName().toString().startsWith(eventId + "_"))
                    .forEach(p -> { try { Files.delete(p); } catch (Exception ignored) {} });
        } catch (Exception ignored) {}
        event.setRouteGpxUrl(null);
        event.setElevationGainMeters(null);
        eventRepository.save(event);
    }

    private GpxStats parseGpx(InputStream is) {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
            NodeList trkpts = doc.getElementsByTagName("trkpt");
            if (trkpts.getLength() == 0) {
                trkpts = doc.getElementsByTagName("rtept");
            }

            List<double[]> points = new ArrayList<>(); // [lat, lon, ele]
            for (int i = 0; i < trkpts.getLength(); i++) {
                Element pt = (Element) trkpts.item(i);
                double lat = Double.parseDouble(pt.getAttribute("lat"));
                double lon = Double.parseDouble(pt.getAttribute("lon"));
                double ele = 0;
                NodeList eleNodes = pt.getElementsByTagName("ele");
                if (eleNodes.getLength() > 0) {
                    ele = Double.parseDouble(eleNodes.item(0).getTextContent().trim());
                }
                points.add(new double[]{lat, lon, ele});
            }

            double elevationGain = 0;
            double totalDistance = 0;
            for (int i = 1; i < points.size(); i++) {
                double eleDiff = points.get(i)[2] - points.get(i - 1)[2];
                if (eleDiff > 0) elevationGain += eleDiff;
                totalDistance += haversine(
                        points.get(i - 1)[0], points.get(i - 1)[1],
                        points.get(i)[0], points.get(i)[1]
                );
            }

            return new GpxStats(Math.round(elevationGain * 10.0) / 10.0, totalDistance);
        } catch (Exception e) {
            log.warn("Could not parse GPX elevation data", e);
            return new GpxStats(0, 0);
        }
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371.0; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private record GpxStats(double elevationGain, double distanceKm) {}
}
