package com.paygrid.dockerized.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.paygrid.dockerized.model.entity.Deuda;

import java.util.List;

public interface DeudaRepository extends JpaRepository<Deuda, Long> {
    List<Deuda> findByUsuarioId(Long usuarioId);
}
