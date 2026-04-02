package com.hospital.backend.repository;

import com.hospital.backend.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    Optional<Invoice> findFirstByMedicalRecordIdAndIsDeletedFalseOrderByCreateDateDesc(UUID medicalRecordId);

    long countByCreateDateBetween(LocalDateTime start, LocalDateTime end);
}
