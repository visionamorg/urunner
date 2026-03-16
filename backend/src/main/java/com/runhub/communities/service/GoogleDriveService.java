package com.runhub.communities.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.runhub.communities.dto.DriveFolderDto;
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
     * Lists all subfolders inside a Drive folder (one level deep).
     * Used to browse event folders inside the root "urunner" folder.
     */
    public List<DriveFolderDto> getSubFolders(String rootFolderId) {
        requireApiKey();

        String query = String.format("'%s' in parents and mimeType = 'application/vnd.google-apps.folder' and trashed = false", rootFolderId);

        String url = UriComponentsBuilder.fromHttpUrl(DRIVE_API_BASE)
                .queryParam("q", query)
                .queryParam("fields", "files(id,name)")
                .queryParam("orderBy", "name")
                .queryParam("key", apiKey)
                .queryParam("pageSize", 100)
                .build()
                .toUriString();

        log.info("Listing subfolders of Drive folder: {}", rootFolderId);

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode files = root.path("files");

            List<DriveFolderDto> folders = new ArrayList<>();
            if (files.isArray()) {
                for (JsonNode file : files) {
                    String id = file.path("id").asText();
                    String name = file.path("name").asText();
                    if (!id.isBlank()) {
                        int count = countImagesInFolder(id);
                        folders.add(new DriveFolderDto(id, name, count));
                    }
                }
            }

            log.info("Found {} subfolders in root folder {}", folders.size(), rootFolderId);
            return folders;

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to list Drive subfolders: {}", rootFolderId, e);
            throw new BadRequestException("Failed to list Drive folders: " + e.getMessage());
        }
    }

    /**
     * Counts images in a folder (for display in the folder picker).
     */
    private int countImagesInFolder(String folderId) {
        try {
            String query = String.format("'%s' in parents and mimeType contains 'image/' and trashed = false", folderId);
            String url = UriComponentsBuilder.fromHttpUrl(DRIVE_API_BASE)
                    .queryParam("q", query)
                    .queryParam("fields", "files(id)")
                    .queryParam("key", apiKey)
                    .queryParam("pageSize", 200)
                    .build()
                    .toUriString();
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode files = root.path("files");
            return files.isArray() ? files.size() : 0;
        } catch (Exception e) {
            log.warn("Could not count images in folder {}: {}", folderId, e.getMessage());
            return 0;
        }
    }

    /**
     * Fetches all image files from a specific Drive folder (event folder).
     */
    public List<String> getImagesFromFolder(String folderId) {
        requireApiKey();

        String query = String.format("'%s' in parents and mimeType contains 'image/' and trashed = false", folderId);

        String url = UriComponentsBuilder.fromHttpUrl(DRIVE_API_BASE)
                .queryParam("q", query)
                .queryParam("fields", "files(id,name,mimeType,thumbnailLink)")
                .queryParam("key", apiKey)
                .queryParam("pageSize", 200)
                .build()
                .toUriString();

        log.info("Fetching images from Drive folder: {}", folderId);

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode files = root.path("files");

            log.info("Drive API response for folder {}: {}", folderId, response);

            List<String> imageUrls = new ArrayList<>();
            if (files.isArray()) {
                for (JsonNode file : files) {
                    String fileId = file.path("id").asText();
                    String thumbnailLink = file.path("thumbnailLink").asText("");

                    if (!fileId.isBlank()) {
                        if (!thumbnailLink.isBlank()) {
                            String largeUrl = thumbnailLink
                                    .replaceAll("=s\\d+$", "=s1600")
                                    .replaceAll("=s\\d+&", "=s1600&");
                            imageUrls.add(largeUrl);
                        } else {
                            imageUrls.add("https://drive.google.com/thumbnail?id=" + fileId + "&sz=w1600");
                        }
                    }
                }
            }

            log.info("Found {} images in folder {}", imageUrls.size(), folderId);
            return imageUrls;

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to fetch images from Drive folder: {}", folderId, e);
            throw new BadRequestException("Failed to fetch images from Google Drive: " + e.getMessage());
        }
    }

    private void requireApiKey() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new BadRequestException(
                "Google Drive API key is not configured. Set GOOGLE_DRIVE_API_KEY in .env");
        }
    }
}
