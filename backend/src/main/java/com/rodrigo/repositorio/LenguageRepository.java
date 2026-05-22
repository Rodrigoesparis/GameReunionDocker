package com.rodrigo.repositorio;

import com.rodrigo.modelo.Lenguage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LenguageRepository extends JpaRepository<Lenguage, Integer> {
    List<Lenguage> findByUserIdUser(Integer userId);
    void deleteByUserIdUser(Integer userId);
}