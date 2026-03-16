package com.runhub.communities.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.runhub.config.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleDriveService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${GOOGLE_DRIVE_API_KEY:}")
    private String apiKey;

    private static final String DRIVE_API_BASE = "https://www.googleapis.com/drive/v3/files";

    /**
     * Fetches all image files from a public Google Drive folder.
     *
     * @param folderId The Google Drive folder ID
     * @return List of direct image URLs
     */
    public List<String> getImagesFromFolder(String folderId) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new BadRequestException(
                "Google Drive API key is not configured. Please set the GOOGLE_DRIVE_API_KEY environment variable.");
        }

        if (folderId == null || folderId.isBlank()) {
            throw new BadRequestException(
                "Google Drive folder ID is not set for this community. Please configure it in the community settings.");
        }

        String query = String.format("'%s' in parents and mimeType contains 'image/'", folderId);

        String url = UriComponentsBuilder.fromHttpUrl(DRIVE_API_BASE)
                .queryParam("q", query)
                .queryParam("fields", "files(id,name,mimeType,thumbnailLink)")
                .queryParam("key", apiKey)
                .queryParam("pageSize", 100)
                .build()
                .toUriString();

        log.info("Fetching Drive images from folder: {}", folderId);

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode files = root.path("files");

            log.info("Drive API raw response: {}", response);

            List<String> imageUrls = new ArrayList<>();
            if (files.isArray()) {
                for (JsonNode file : files) {
                    String fileId = file.path("id").asText();
                    String thumbnailLink = file.path("thumbnailLink").asText("");

                    if (!fileId.isBlank()) {
                        // Use thumbnailLink if available (reliable CDN URL), upgrade to larger size
                        if (!thumbnailLink.isBlank()) {
                            // Replace thumbnail size (s220) with larger size for better quality
                            String largeUrl = thumbnailLink.replaceAll("=s\\d+$", "=s1600")
                                    .replaceAll("=s\\d+&", "=s1600&");
                            imageUrls.add(largeUrl);
                        } else {
                            // Fallback to Drive thumbnail endpoint
                            imageUrls.add("https://drive.google.com/thumbnail?id=" + fileId + "&sz=w1600");
                        }
                    }
                }
            }

            log.info("Found {} images in Drive folder {}", imageUrls.size(), folderId);
            return imageUrls;

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to fetch images from Google Drive folder: {}", folderId, e);
            throw new BadRequestException("Failed to fetch images from Google Drive: " + e.getMessage());
        }
    }
}
