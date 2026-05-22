package com.rodrigo.controlador;

import com.rodrigo.servicio.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private static final Logger log = LoggerFactory.getLogger(ProfileController.class);

    @Autowired
    private ProfileService profileService;

    // GET /profile/{userId}
    @GetMapping("/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable Integer userId) {
        try {
            Map<String, Object> profile = profileService.getProfile(userId);
            return ResponseEntity.ok(profile);
        } catch (IllegalArgumentException e) {
            log.error("[ERROR GET PROFILE] userId={} motivo='{}'", userId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT /profile/{userId}
    // Body JSON: { "username": "...", "bio": "...", "games": [...], "languages": [...], "platforms": [...] }
    // Todos los campos son opcionales, solo se actualizan los que vengan
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateProfile(@PathVariable Integer userId,
                                           @RequestBody Map<String, Object> body) {
        try {
            Map<String, Object> updated = profileService.updateProfile(userId, body);
            log.info("[PROFILE ACTUALIZADO] userId={}", userId);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.error("[ERROR UPDATE PROFILE] userId={} motivo='{}'", userId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
