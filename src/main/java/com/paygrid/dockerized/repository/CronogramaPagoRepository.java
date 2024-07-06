package com.paygrid.dockerized.repository;

import com.paygrid.dockerized.model.entity.CronogramaPago;
import com.paygrid.dockerized.model.enums.Estado;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CronogramaPagoRepository extends JpaRepository<CronogramaPago, Long> {
    List<CronogramaPago> findByPrestamoId(Long prestamoId);
    List<CronogramaPago> findAllByEstado(Estado estado);
}


