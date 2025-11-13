package com.hospital.backend.repository;

import com.hospital.backend.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    @Query("""
                SELECT a FROM Appointment a
                WHERE a.isDeleted = false
                AND (:patientId IS NULL OR a.patient.id = :patientId)
                AND (:doctorId IS NULL OR a.staff.id = :doctorId)
                AND (:status IS NULL OR a.status = :status)
            """)
    List<Appointment> searchAppointments(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("patientId") UUID patientId,
            @Param("doctorId") UUID doctorId,
            @Param("status") Appointment.AppointmentStatus status
    );
}
