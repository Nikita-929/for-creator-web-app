package com.forcreators.api.web;

import com.forcreators.api.service.StorageService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class FileController {

    private final StorageService storage;

    public FileController(StorageService storage) {
        this.storage = storage;
    }

    @PostMapping("/admin/uploads")
    public Map<String, String> adminUpload(@RequestParam("file") MultipartFile file) {
        return Map.of("url", storage.store(file));
    }

    @PostMapping("/uploads")
    public Map<String, String> upload(@RequestParam("file") MultipartFile file) {
        return Map.of("url", storage.store(file));
    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> file(@PathVariable String filename) throws Exception {
        Path path = storage.resolve(filename);
        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }
        String contentType = Files.probeContentType(path);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType == null ? "application/octet-stream" : contentType))
                .body(new FileSystemResource(path));
    }
}
