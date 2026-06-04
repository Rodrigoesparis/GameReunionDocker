package com.rodrigo.controlador;

import com.rodrigo.modelo.GameReunion;
import com.rodrigo.modelo.User;
import com.rodrigo.repositorio.GameReunionRepository;
import com.rodrigo.repositorio.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/images")
public class ImageController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameReunionRepository gameReunionRepository;

    // POST /images/user/{userId}  → sube/reemplaza foto de usuario
    @PostMapping("/user/{userId}")
    public ResponseEntity<?> uploadUserPhoto(
            @PathVariable Integer userId,
            @RequestParam("file") MultipartFile file) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            user.setPhoto(file.getBytes());
            userRepository.save(user);

            return ResponseEntity.ok(Map.of("message", "Foto de usuario guardada correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET /images/user/{userId}  → devuelve foto de usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<byte[]> getUserPhoto(@PathVariable Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (user.getPhoto() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                .body(user.getPhoto());
    }

    // POST /images/group/{groupId}  → sube/reemplaza foto de grupo
    @PostMapping("/group/{groupId}")
    public ResponseEntity<?> uploadGroupPhoto(
            @PathVariable Integer groupId,
            @RequestParam("file") MultipartFile file) {
        try {
            GameReunion group = gameReunionRepository.findById(groupId)
                    .orElseThrow(() -> new IllegalArgumentException("Grupo no encontrado"));

            group.setPhoto(file.getBytes());
            gameReunionRepository.save(group);

            return ResponseEntity.ok(Map.of("message", "Foto de grupo guardada correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET /images/group/{groupId}  → devuelve foto de grupo
    @GetMapping("/group/{groupId}")
    public ResponseEntity<byte[]> getGroupPhoto(@PathVariable Integer groupId) {
        GameReunion group = gameReunionRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Grupo no encontrado"));

        if (group.getPhoto() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                .body(group.getPhoto());
    }
}