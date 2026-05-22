package com.rodrigo.repositorio;

import com.rodrigo.modelo.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlatformRepository extends JpaRepository<Platform, Integer> {
    List<Platform> findByUserIdUser(Integer userId);
    void deleteByUserIdUser(Integer userId);
}