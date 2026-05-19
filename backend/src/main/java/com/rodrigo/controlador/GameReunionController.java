package com.rodrigo.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.rodrigo.modelo.*;
import com.rodrigo.servicio.GameReunionService;
import com.rodrigo.repositorio.ParticipantRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/groups")
public class GameReunionController {

    private static final Logger log = LoggerFactory.getLogger(GameReunionController.class);

    @Autowired
    private GameReunionService GameReunionService;

    @Autowired
    private ParticipantRepository participantRepository;

    //Crear grupo

@PostMapping
public ResponseEntity<?> createGroup(@RequestBody CreateGroupRequest req) {
    try {
        GameReunion group = GameReunionService.crearGrupo(
            req.creatorId, req.name, req.game,
            req.mode, req.privacy, req.password, req.maxPlayers
        );
        log.info("[GRUPO CREADO] idGroup={} nombre='{}' juego='{}' modo='{}' privacidad={} maxJugadores={} creadoPor=userId={}",
            group.getIdGroup(), group.getName(), group.getGame(),
            group.getMode(), group.getPrivacy(), group.getMaxPlayers(), req.creatorId);
        return ResponseEntity.ok(group);
    } catch (IllegalArgumentException | IllegalStateException e) {
        log.error("[ERROR CREAR GRUPO] creadoPor=userId={} motivo='{}'", req.creatorId, e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

    // Listar miembros del grupo

   @GetMapping
public List<GameReunionService.GameReunionDTO> getAllGroups(
        @RequestParam(required = false) Integer userId) {
    if (userId != null) {
        return GameReunionService.listarGruposSinElUsuario(userId);
    }
    return GameReunionService.listarGruposConInfo();
}

    // Detalles del grupo

    @GetMapping("/{groupId}")
    public ResponseEntity<?> getGroup(@PathVariable Integer groupId) {
        try {
            return ResponseEntity.ok(GameReunionService.buscarPorId(groupId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Modificar grupo

 @PutMapping("/{groupId}")
public ResponseEntity<?> updateGroup(@PathVariable Integer groupId,
                                      @RequestParam Integer requesterId,
                                      @RequestBody UpdateGroupRequest req) {
    try {
        verificarLiderOAdmin(requesterId, groupId);
        GameReunion updated = GameReunionService.actualizarGrupo(
            groupId, req.name, req.game, req.mode,
            req.privacy, req.password, req.maxPlayers
        );
        log.info("[GRUPO MODIFICADO] idGroup={} nombre='{}' juego='{}' modo='{}' privacidad={} maxJugadores={} modificadoPor=userId={}",
            updated.getIdGroup(), updated.getName(), updated.getGame(),
            updated.getMode(), updated.getPrivacy(), updated.getMaxPlayers(), requesterId);
        return ResponseEntity.ok(updated);
    } catch (IllegalArgumentException | IllegalStateException e) {
        log.error("[ERROR MODIFICAR GRUPO] idGroup={} modificadoPor=userId={} motivo='{}'",
            groupId, requesterId, e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

    //Cambiar liderazgo

    @PostMapping("/{groupId}/transfer-leader")
    public ResponseEntity<?> transferLeader(@PathVariable Integer groupId,
@RequestParam Integer liderActualId,@RequestParam Integer nuevoLiderId) {
        try {
            GameReunionService.transferirLiderazgo(groupId, liderActualId, nuevoLiderId);
            return ResponseEntity.ok("Liderazgo transferido correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Ascender miembro a admin (solo líder) 

    @PostMapping("/{groupId}/promote")
    public ResponseEntity<?> promoteToAdmin(@PathVariable Integer groupId,
                                             @RequestParam Integer liderActualId,
                                             @RequestParam Integer targetUserId) {
        try {
            verificarLider(liderActualId, groupId);
            GameReunionService.cambiarRol(groupId, targetUserId, Role.ADMIN);
            return ResponseEntity.ok("Usuario ascendido a admin.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Degradar admin a miembro (solo líder) 

    @PostMapping("/{groupId}/demote")
    public ResponseEntity<?> demoteToMember(@PathVariable Integer groupId,
                                             @RequestParam Integer liderActualId,
                                             @RequestParam Integer targetUserId) {
        try {
            verificarLider(liderActualId, groupId);
            GameReunionService.cambiarRol(groupId, targetUserId, Role.MIEMBRO);
            return ResponseEntity.ok("Admin degradado a miembro.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Eliminar grupo (solo líder) 

@DeleteMapping("/{groupId}")
public ResponseEntity<?> deleteGroup(@PathVariable Integer groupId,
                                      @RequestParam Integer requesterId) {
    try {
        GameReunion group = GameReunionService.buscarPorId(groupId);
        String nombreGrupo = group.getName();
        GameReunionService.eliminarGrupo(groupId, requesterId);
        log.info("[GRUPO ELIMINADO] idGroup={} nombre='{}' eliminadoPor=userId={}",
            groupId, nombreGrupo, requesterId);
        return ResponseEntity.ok("Grupo eliminado.");
    } catch (IllegalArgumentException | IllegalStateException e) {
        log.error("[ERROR ELIMINAR GRUPO] idGroup={} eliminadoPor=userId={} motivo='{}'",
            groupId, requesterId, e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

    //  Helpers de permisos 

    private void verificarLiderOAdmin(Integer userId, Integer groupId) { // Verifica que el usuario sea líder o admin
        Participant p = participantRepository.findByUserIdUserAndGroupIdGroup(userId, groupId);
        if (p == null || p.getRole() == Role.MIEMBRO) {
            throw new IllegalStateException("Solo el líder o un admin pueden hacer esto.");
        }
    }

    private void verificarLider(Integer userId, Integer groupId) { // Verifica que el usuario sea líder
        Participant p = participantRepository.findByUserIdUserAndGroupIdGroup(userId, groupId);
        if (p == null || p.getRole() != Role.LIDER) {
            throw new IllegalStateException("Solo el líder puede hacer esto.");
        }
    }

    //  DTOs 

    static class CreateGroupRequest {
        public Integer creatorId;
        public String name;
        public String game;       // "Cluedo", "Uno", lo que sea
        public String mode;       // COMPETITIVO, CASUAL, PERSONALIZADO
        public Privacy privacy;    // PUBLICO, PRIVADO, SOLICITUD
        public String password;
        public Integer maxPlayers;
    }

    static class UpdateGroupRequest {
        public String name;
        public String game;
        public String mode;
        public Privacy privacy;
        public String password;
        public Integer maxPlayers;
    }
}