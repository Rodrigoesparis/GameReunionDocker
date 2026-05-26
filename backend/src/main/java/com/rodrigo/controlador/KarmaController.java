package com.rodrigo.controlador;

import com.rodrigo.servicio.KarmaService;
import com.rodrigo.modelo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.Map;

@RestController
@RequestMapping("/karma")
public class KarmaController {

    @Autowired private KarmaService karmaService;

    // Votar a un usuario
    @PostMapping("/vote")
    public ResponseEntity<?> vote(
            @RequestParam Integer voterId,
            @RequestParam Integer targetId,
            @RequestParam String voteType) {
        try {
            karmaService.vote(voterId, targetId, voteType);
            return ResponseEntity.ok("Voto registrado correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Obtener ranking
    @GetMapping("/ranking")
    public ResponseEntity<List<Map<String, Object>>> getRanking() {
        return ResponseEntity.ok(karmaService.getRanking());
    }
}