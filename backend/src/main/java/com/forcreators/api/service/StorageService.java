package com.forcreators.api.service;

import com.forcreators.api.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class StorageService {

    private static final Set<String> ALLOWED = Set.of("image/jpeg", "image/png", "image/webp", "image/avif", "image/gif");

    private final Path root;

    public StorageService(@Value("${app.storage.dir}") String dir) throws IOException {
        this.root = Path.of(dir).toAbsolutePath().normalize();
        Files.createDirectories(this.root);
    }

    public String store(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("Empty file");
        }
        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
        if (!ALLOWED.contains(contentType)) {
            throw new BusinessException("Only JPEG, PNG, WebP, AVIF, or GIF images are allowed");
        }
        String ext = switch (contentType) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/avif" -> ".avif";
            case "image/gif" -> ".gif";
            default -> ".jpg";
        };
        String name = UUID.randomUUID() + ext;
        try {
            file.transferTo(root.resolve(name));
        } catch (IOException e) {
            throw new BusinessException("Could not store file");
        }
        return "/api/files/" + name;
    }

    public Path resolve(String filename) {
        Path path = root.resolve(filename).normalize();
        if (!path.startsWith(root)) {
            throw new BusinessException("Invalid path");
        }
        return path;
    }
}
