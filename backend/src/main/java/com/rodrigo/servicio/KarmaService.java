package com.rodrigo.servicio;

import com.rodrigo.modelo.*;
import com.rodrigo.repositorio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class KarmaService {

    @Autowired private UserRepository userRepository;
    @Autowired private KarmaVoteRepository karmaVoteRepository;

    public void vote(Integer voterId, Integer targetId, String voteType) {
        if (voterId.equals(targetId)) {
            throw new IllegalArgumentException("No puedes votarte a ti mismo.");
        }

        if (karmaVoteRepository.existsByVoterIdUserAndTargetIdUser(voterId, targetId)) {
            throw new IllegalStateException("Ya has votado a este usuario.");
        }

        User voter = userRepository.findById(voterId)
            .orElseThrow(() -> new IllegalArgumentException("Votante no encontrado."));
        User target = userRepository.findById(targetId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario objetivo no encontrado."));

        // Aplicar karma
        if (voteType.equalsIgnoreCase("UP")) {
            target.setKarma(target.getKarma() + 2);
        } else if (voteType.equalsIgnoreCase("DOWN")) {
            target.setKarma(target.getKarma() - 1);
        } else {
            throw new IllegalArgumentException("Tipo de voto inválido. Usa UP o DOWN.");
        }

        userRepository.save(target);

        // Registrar voto
        KarmaVote vote = new KarmaVote();
        vote.setVoter(voter);
        vote.setTarget(target);
        vote.setVoteType(KarmaVote.VoteType.valueOf(voteType.toUpperCase()));
        karmaVoteRepository.save(vote);
    }

    public List<Map<String, Object>> getRanking(String order) {
    return userRepository.findAll()
        .stream()
        .sorted((a, b) -> order.equals("asc")
            ? a.getKarma() - b.getKarma()   // menor a mayor
            : b.getKarma() - a.getKarma())   // mayor a menor (default)
        .map(u -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("idUser", u.getIdUser());
            m.put("username", u.getUsername());
            m.put("karma", u.getKarma());
            return m;
        })
        .toList();
}
}