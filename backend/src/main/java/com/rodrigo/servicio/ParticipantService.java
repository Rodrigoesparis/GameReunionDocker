package com.rodrigo.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rodrigo.controlador.GroupEventController;
import com.rodrigo.modelo.*;
import com.rodrigo.repositorio.*;

import java.util.LinkedHashMap;
import java.util.List;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ParticipantService {

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameReunionRepository groupRepository;

    @Autowired
    private GameReunionService GameReunionService;

    @Autowired
private GroupEventController groupEventController;

    //  Unirse a un grupo 

    public Participant unirseAlGrupo(Integer userId, Integer groupId, String passwordIntentada) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        GameReunion group = groupRepository.findById(groupId)
            .orElseThrow(() -> new IllegalArgumentException("Grupo no encontrado."));

        // Un usuario solo puede estar en un grupo a la vez
        if (participantRepository.existsByUserIdUser(userId)) {
            throw new IllegalStateException("Ya estás en un grupo. Sal primero antes de unirte a otro.");
        }

        // Verificar que hay sitio
        int actuales = participantRepository.countByGroupIdGroup(groupId);
        if (group.getMaxPlayers() != null && actuales >= group.getMaxPlayers()) {
            throw new IllegalStateException("El grupo está lleno.");
        }

        // Lógica según privacidad del grupo
        switch (group.getPrivacy()) {
            case ABIERTO:
                break;

            case PRIVADO_PASSWORD:
                if (passwordIntentada == null || !passwordIntentada.equals(group.getPassword())) {
                    throw new IllegalArgumentException("Contraseña incorrecta.");
                }
                break;

            case INVITACION:
                // Fase futura: comprobar si hay invitación pendiente
                throw new IllegalStateException("Este grupo solo acepta miembros por invitación.");

            case SOLICITUD:
                // Fase futura: crear Request automáticamente
                throw new IllegalStateException("Este grupo requiere solicitud. Usa el sistema de solicitudes.");
        }

        Participant participant = new Participant();
        participant.setUser(user);
        participant.setGroup(group);
        participant.setRole(Role.MIEMBRO);

        groupEventController.notifyGroupUpdate(groupId, "MEMBER_JOINED");

        return participantRepository.save(participant);
    }

    //  Salir de un grupo 

    public void salirDelGrupo(Integer userId, Integer groupId) {
    Participant participant = participantRepository.findByUserIdUserAndGroupIdGroup(userId, groupId);

    if (participant == null) {
        throw new IllegalArgumentException("No estás en ese grupo.");
    }

    boolean eraLider = participant.getRole() == Role.LIDER;

    participantRepository.delete(participant);

    groupEventController.notifyGroupUpdate(groupId, "MEMBER_LEFT");

    int restantes = participantRepository.countByGroupIdGroup(groupId);

    if (restantes == 0) {
        // Nadie queda → el grupo desaparece
        groupRepository.deleteById(groupId);
    } else if (eraLider) {
        // Quedan miembros pero se fue el líder → transferir liderazgo
        GameReunionService.transferirLiderazgoAlSiguiente(groupId, userId);
    }
}

    //  Expulsar a un usuario (la lógica de permisos está en el controlador) 

    public void expulsarUsuario(Integer targetId, Integer groupId) {
        Participant participant = participantRepository.findByUserIdUserAndGroupIdGroup(targetId, groupId);

        if (participant == null) {
            throw new IllegalArgumentException("El usuario no está en ese grupo.");
        }

        participantRepository.delete(participant);
    }

    //  Listar participantes de un grupo 

public List<Map<String, Object>> listarParticipantes(Integer groupId) {
    if (!groupRepository.existsById(groupId)) {
        throw new IllegalArgumentException("Grupo no encontrado.");
    }
    return participantRepository.findByGroupIdGroup(groupId)
        .stream()
        .map(p -> {
            User u = p.getUser();
            Map<String, Object> userMap = new LinkedHashMap<>();
            userMap.put("idUser", u.getIdUser());
            userMap.put("username", u.getUsername());
            userMap.put("photoUrl", u.getPhotoUrl());
            userMap.put("karma", u.getKarma());

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("user", userMap);
            result.put("role", p.getRole());
            return result;
        })
        .toList();
}

    //  Obtener el grupo actual de un usuario 

    public Map<String, Object> obtenerGrupoDeUsuario(Integer userId) {
    Participant p = participantRepository.findByUserIdUser(userId).orElse(null);
    if (p == null) return null;

    GameReunion g = p.getGroup();
    int currentPlayers = participantRepository.countByGroupIdGroup(g.getIdGroup());

    Map<String, Object> groupMap = new LinkedHashMap<>();
    groupMap.put("idGroup", g.getIdGroup());
    groupMap.put("name", g.getName());
    groupMap.put("game", g.getGame());
    groupMap.put("mode", g.getMode());
    groupMap.put("privacy", g.getPrivacy());
    groupMap.put("maxPlayers", g.getMaxPlayers());
    groupMap.put("currentPlayers", currentPlayers);
    groupMap.put("photoUrl", g.getPhotoUrl()); // ← clave

    Map<String, Object> result = new LinkedHashMap<>();
    result.put("group", groupMap);
    result.put("role", p.getRole());
    return result;
}
}