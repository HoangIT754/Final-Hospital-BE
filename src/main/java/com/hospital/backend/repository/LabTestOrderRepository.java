package com.hospital.backend.repository;

import com.hospital.backend.entity.LabTestOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LabTestOrderRepository extends JpaRepository<LabTestOrder, UUID> {
    @Query(
            value = "SELECT COALESCE(MAX(CAST(SUBSTRING(order_code, 5) AS INTEGER)), 0) FROM lab_test_order",
            nativeQuery = true
    )
    Long findMaxOrderCodeIndex();

    List<LabTestOrder> findByMedicalRecordIdAndIsDeletedFalse(UUID medicalRecordId);

    @Query("""
           SELECT o
           FROM LabTestOrder o
           WHERE o.medicalRecord.id = :medicalRecordId
             AND (o.isDeleted IS NULL OR o.isDeleted = false)
           """)
    List<LabTestOrder> findActiveByMedicalRecordId(@Param("medicalRecordId") UUID medicalRecordId);
}
