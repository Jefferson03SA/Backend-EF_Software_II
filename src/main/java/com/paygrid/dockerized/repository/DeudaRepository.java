package com.paygrid.dockerized.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.paygrid.dockerized.model.entity.Deuda;
import com.paygrid.dockerized.model.enums.Estado;

import java.time.LocalDate;
import java.util.List;

public interface DeudaRepository extends JpaRepository<Deuda, Long> {

    List<Deuda> findByUsuarioId(Long usuarioId);

    List<Deuda> findByUsuarioIdAndFechaVencimientoBetween(Long usuarioId, LocalDate start, LocalDate end);

    List<Deuda> findByUsuarioIdAndEstado(Long usuarioId, Estado estado);

    List<Deuda> findByUsuarioIdAndEstadoAndFechaVencimientoBetween(Long usuarioId, Estado estado, LocalDate start, LocalDate end);

    List<Deuda> findByUsuarioIdAndEstadoAndFechaVencimientoBefore(Long usuarioId, Estado estado, LocalDate date);

    boolean existsByNumeroDocumento(String numeroDocumento);

    List<Deuda> findByFechaVencimientoAndEstado(LocalDate hoy, Estado pendiente);

    List<Deuda> findByFechaVencimiento(LocalDate hoy);
}
