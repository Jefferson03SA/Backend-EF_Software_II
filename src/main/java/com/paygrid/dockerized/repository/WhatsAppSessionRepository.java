package com.paygrid.dockerized.repository;

import com.paygrid.dockerized.model.entity.WhatsAppSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WhatsAppSessionRepository extends JpaRepository<WhatsAppSession, Long> {
    
    @Query("SELECT w FROM WhatsAppSession w WHERE w.phoneNumber = :phoneNumber ORDER BY w.updatedAt DESC LIMIT 1")
    Optional<WhatsAppSession> findLatestByPhoneNumber(@Param("phoneNumber") String phoneNumber);
    
    @Query("SELECT w FROM WhatsAppSession w WHERE w.phoneNumber = :phoneNumber AND w.active = true ORDER BY w.updatedAt DESC LIMIT 1")
    Optional<WhatsAppSession> findLatestActiveByPhoneNumber(@Param("phoneNumber") String phoneNumber);
}