package com.hospital.backend.repository;

import com.hospital.backend.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {
    List<Prescription> findAllByIsDeleted(Boolean isDeleted);

    @Query("""
                SELECT p 
                FROM Prescription p
                WHERE (:status IS NULL OR p.status = :status)
                  AND (:medicalRecordId IS NULL OR p.medicalRecord.id = :medicalRecordId)
                  AND (:isDeleted IS NULL OR p.isDeleted = :isDeleted)
                  AND p.createDate >= :fromDate
                  AND p.createDate < :toDate
            """)
    List<Prescription> searchPrescriptions(
            @Param("status") Prescription.PrescriptionStatus status,
            @Param("medicalRecordId") UUID medicalRecordId,
            @Param("isDeleted") Boolean isDeleted,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    Optional<Prescription> findByMedicalRecordIdAndIsDeletedFalse(UUID medicalRecordId);
}
