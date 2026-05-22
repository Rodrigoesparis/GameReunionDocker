package com.rodrigo.repositorio;

import com.rodrigo.modelo.Games;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GamesRepository extends JpaRepository<Games, Integer> {
    List<Games> findByUserIdUserOrderByDisplayOrder(Integer userId);
    void deleteByUserIdUser(Integer userId);
}
