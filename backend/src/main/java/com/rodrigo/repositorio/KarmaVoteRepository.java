package com.rodrigo.repositorio;

import com.rodrigo.modelo.KarmaVote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KarmaVoteRepository extends JpaRepository<KarmaVote, Integer> {
    boolean existsByVoterIdUserAndTargetIdUser(Integer voterId, Integer targetId);
}