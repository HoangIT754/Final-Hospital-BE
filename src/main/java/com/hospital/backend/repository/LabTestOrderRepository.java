package com.hospital.backend.repository;

import com.hospital.backend.entity.LabTestOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    @Query("""
            SELECT o
            FROM LabTestOrder o
            JOIN o.medicalRecord mr
            LEFT JOIN mr.appointment a
            LEFT JOIN a.patient p
            LEFT JOIN a.staff s
            WHERE o.createDate >= COALESCE(:startDate, o.createDate)
              AND o.createDate <= COALESCE(:endDate, o.createDate)
              AND p.id = COALESCE(:patientId, p.id)
              AND s.id = COALESCE(:doctorId, s.id)
              AND o.status = COALESCE(:status, o.status)
              AND o.testType = COALESCE(:testType, o.testType)
              AND LOWER(o.orderCode) LIKE COALESCE(
                    LOWER(CONCAT('%', :orderCode, '%')),
                    LOWER(o.orderCode)
              )
              AND (o.isDeleted IS NULL OR o.isDeleted = false)
            ORDER BY o.createDate DESC
            """)
    List<LabTestOrder> searchLabTestOrders(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("patientId") UUID patientId,
            @Param("doctorId") UUID doctorId,
            @Param("status") String status,
            @Param("testType") String testType,
            @Param("orderCode") String orderCode
    );
}
