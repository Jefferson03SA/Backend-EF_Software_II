package com.paygrid.dockerized.repository;

import com.paygrid.dockerized.model.entity.CronogramaPago;
import com.paygrid.dockerized.model.enums.Estado;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CronogramaPagoRepository extends JpaRepository<CronogramaPago, Long> {
    List<CronogramaPago> findByPrestamoId(Long prestamoId);
    List<CronogramaPago> findAllByEstado(Estado estado);
    Optional<CronogramaPago>findByPrestamoIdAndNumero(Long prestamoId, int numero); 
    List<CronogramaPago> findByPrestamoIdOrderByNumeroAsc(Long prestamoId);
    List<CronogramaPago> findAllByFechaVencimientoAndEstado(LocalDate hoy, Estado pendiente);
    List<CronogramaPago> findByPrestamoUsuarioIdAndFechaVencimientoBetween(Long id, LocalDate hoy, LocalDate hoy2);
}


