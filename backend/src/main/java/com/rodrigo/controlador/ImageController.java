package com.rodrigo.controlador;

import com.rodrigo.modelo.GameReunion;
import com.rodrigo.modelo.User;
import com.rodrigo.repositorio.GameReunionRepository;
import com.rodrigo.repositorio.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/images")
public class ImageController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    // cambia esto por tu IP local cuando pruebes en dispositivo físico
    private static final String BASE_URL = "http://10.0.2.2:8080/uploads/";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameReunionRepository gameReunionRepository;

    // POST /images/user/{userId}
    @PostMapping("/user/{userId}")
    public ResponseEntity<?> uploadUserPhoto(
            @PathVariable Integer userId,
            @RequestParam("file") MultipartFile file) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            String url = saveFile(file, "avatars");

            // borrar foto anterior si existe
            deleteOldFile(user.getPhotoUrl());

            user.setPhotoUrl(url);
            userRepository.save(user);

            return ResponseEntity.ok(Map.of("photoUrl", url));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // POST /images/group/{groupId}
    @PostMapping("/group/{groupId}")
    public ResponseEntity<?> uploadGroupPhoto(
            @PathVariable Integer groupId,
            @RequestParam("file") MultipartFile file) {
        try {
            GameReunion group = gameReunionRepository.findById(groupId)
                    .orElseThrow(() -> new IllegalArgumentException("Grupo no encontrado"));

            String url = saveFile(file, "groups");

            deleteOldFile(group.getPhotoUrl());

            group.setPhotoUrl(url);
            gameReunionRepository.save(group);

            return ResponseEntity.ok(Map.of("photoUrl", url));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String saveFile(MultipartFile file, String folder) throws IOException {
        String ext = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + ext;

        Path dir = Paths.get(uploadDir, folder);
        Files.createDirectories(dir);

        Path dest = dir.resolve(filename);
        Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);

        return BASE_URL + folder + "/" + filename;
    }

    private void deleteOldFile(String oldUrl) {
    if (oldUrl == null || oldUrl.isBlank()) return;
    try {
        String relative = oldUrl.replace(BASE_URL, "");
        Path old = Paths.get(uploadDir, relative);
        System.out.println(">>> BORRANDO: " + old.toAbsolutePath());
        Files.deleteIfExists(old);
    } catch (IOException ignored) {}
}

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return ".jpg";
        return filename.substring(filename.lastIndexOf("."));
    }

    @GetMapping("/uploads/{folder}/{filename}")
public ResponseEntity<byte[]> serveFile(
        @PathVariable String folder,
        @PathVariable String filename) {
    try {
        Path filePath = Paths.get(uploadDir, folder, filename);
        byte[] data = Files.readAllBytes(filePath);
        
        String contentType = filename.endsWith(".png") ? "image/png" : "image/jpeg";
        
        return ResponseEntity.ok()
                .header("Content-Type", contentType)
                .body(data);
    } catch (IOException e) {
        return ResponseEntity.notFound().build();
    }
}
}