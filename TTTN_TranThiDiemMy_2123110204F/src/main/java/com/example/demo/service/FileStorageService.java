package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    private final Path uploadRoot;

    public FileStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) throws IOException {
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadRoot);
    }

    public String store(MultipartFile file, String folder) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Chỉ chấp nhận file ảnh: JPG, PNG, GIF, WEBP");
        }

        String originalName = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "image");
        String extension = "";
        int dot = originalName.lastIndexOf('.');
        if (dot > 0) {
            extension = originalName.substring(dot).toLowerCase();
        }

        String fileName = UUID.randomUUID() + extension;
        Path targetDir = uploadRoot.resolve(folder);
        Files.createDirectories(targetDir);
        Path targetPath = targetDir.resolve(fileName);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/" + folder + "/" + fileName;
    }

    public void deleteIfLocal(String imageUrl) {
        if (imageUrl == null || !imageUrl.startsWith("/uploads/")) {
            return;
        }
        try {
            Path filePath = uploadRoot.resolve(imageUrl.substring("/uploads/".length()));
            Files.deleteIfExists(filePath);
        } catch (IOException ignored) {
        }
    }
}
