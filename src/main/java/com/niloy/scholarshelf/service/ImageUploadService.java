package com.niloy.scholarshelf.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Service for storing and deleting uploaded book cover images on the local file system.
 * Images are saved to  <app.upload.dir>/books/  and served at  /uploads/books/{filename}.
 */
@Service
@Slf4j
public class ImageUploadService {

    private static final List<String> ALLOWED_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp");

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    /**
     * Stores a book cover image and returns the public URL path.
     *
     * @param file the uploaded file
     * @return URL path like /uploads/books/uuid.jpg, or null if file is empty
     * @throws IOException              on I/O error
     * @throws IllegalArgumentException if file type is not allowed
     */
    public String storeBookImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Only image files (JPEG, PNG, GIF, WebP) are allowed.");
        }

        // Derive extension from original filename
        String originalFilename = file.getOriginalFilename();
        String extension = ".jpg"; // default
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }

        String filename = UUID.randomUUID().toString() + extension;

        Path bookUploads = Paths.get(uploadDir, "books");
        Files.createDirectories(bookUploads);
        Files.copy(file.getInputStream(), bookUploads.resolve(filename), StandardCopyOption.REPLACE_EXISTING);

        log.info("Stored book image: {}", filename);
        return "/uploads/books/" + filename;
    }

    /**
     * Deletes a previously uploaded image from the file system.
     *
     * @param imageUrl the public URL path returned by storeBookImage
     */
    public void deleteImage(String imageUrl) {
        if (imageUrl == null || !imageUrl.startsWith("/uploads/")) {
            return;
        }
        try {
            // imageUrl looks like /uploads/books/uuid.jpg
            String relativePath = imageUrl.substring(1); // remove leading /
            Path filePath = Paths.get(uploadDir, relativePath.substring("uploads/".length()));
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                log.info("Deleted image: {}", imageUrl);
            }
        } catch (IOException e) {
            log.warn("Could not delete image {}: {}", imageUrl, e.getMessage());
        }
    }
}
