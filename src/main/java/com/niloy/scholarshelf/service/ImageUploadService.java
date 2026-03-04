package com.niloy.scholarshelf.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Service for uploading and deleting book cover images via Cloudinary.
 * Images are stored in the configured Cloudinary folder and the secure HTTPS
 * URL is persisted in the {@code imageUrl} field of the {@code Book} entity.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImageUploadService {

    private static final List<String> ALLOWED_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp");

    private final Cloudinary cloudinary;

    @Value("${cloudinary.folder:scholarshelf/books}")
    private String uploadFolder;

    /**
     * Uploads a book cover image to Cloudinary and returns the secure URL.
     *
     * @param file the uploaded file
     * @return Cloudinary secure URL, or {@code null} if the file is empty
     * @throws IOException              on upload failure
     * @throws IllegalArgumentException if the file type is not allowed
     */
    public String storeBookImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Only image files (JPEG, PNG, GIF, WebP) are allowed.");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", uploadFolder,
                        "resource_type", "image"
                )
        );

        String secureUrl = (String) uploadResult.get("secure_url");
        log.info("Uploaded book image to Cloudinary: {}", secureUrl);
        return secureUrl;
    }

    /**
     * Deletes a previously uploaded image from Cloudinary.
     *
     * @param imageUrl the Cloudinary secure URL stored in the {@code Book} entity
     */
    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }
        try {
            String publicId = extractPublicId(imageUrl);
            if (publicId == null) {
                log.warn("Could not derive public_id from URL: {}", imageUrl);
                return;
            }
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Deleted Cloudinary image with public_id: {}", publicId);
        } catch (Exception e) {
            log.warn("Could not delete Cloudinary image {}: {}", imageUrl, e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Extracts the Cloudinary {@code public_id} (without file extension) from a
     * secure URL such as:
     * {@code https://res.cloudinary.com/{cloud}/image/upload/v1234/{folder}/{name}.jpg}
     */
    private String extractPublicId(String imageUrl) {
        int uploadIndex = imageUrl.indexOf("/upload/");
        if (uploadIndex == -1) return null;

        String afterUpload = imageUrl.substring(uploadIndex + "/upload/".length());

        // Strip optional version segment  (e.g.  "v1712345678/")
        if (afterUpload.startsWith("v") && afterUpload.contains("/")) {
            String potentialVersion = afterUpload.substring(1, afterUpload.indexOf("/"));
            if (potentialVersion.matches("\\d+")) {
                afterUpload = afterUpload.substring(afterUpload.indexOf("/") + 1);
            }
        }

        // Strip file extension
        int dotIndex = afterUpload.lastIndexOf(".");
        if (dotIndex != -1) {
            afterUpload = afterUpload.substring(0, dotIndex);
        }

        return afterUpload; // e.g.  "scholarshelf/books/abcde12345"
    }
}
