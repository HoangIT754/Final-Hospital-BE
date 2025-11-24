package com.hospital.backend.repository;

import com.hospital.backend.entity.Appointment;
import com.hospital.backend.entity.PatientProfile;
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
        SELECT a 
        FROM Appointment a
        WHERE a.isDeleted = false
        AND (:patientId IS NULL OR a.patient.id = :patientId)
        AND (:doctorId IS NULL OR a.staff.id = :doctorId)
        AND (:status IS NULL OR a.status = :status)
        AND (:startDate IS NULL OR :startDate = '' OR a.appointmentStartTime >= CAST(:startDate AS timestamp))
        AND (:endDate IS NULL OR :endDate = '' OR a.appointmentEndTime >= CAST(:endDate AS timestamp))
    """)
    List<Appointment> searchAppointments(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("patientId") UUID patientId,
            @Param("doctorId") UUID doctorId,
            @Param("status") Appointment.AppointmentStatus status
    );

    @Query("""
                SELECT DISTINCT a.patient, a.id as appointmentId
                FROM Appointment a
                WHERE a.staff.user.id = :doctorUserId
            """)
    List<Object[]> findPatientsWithAppointmentByDoctorUserId(@Param("doctorUserId") UUID doctorUserId);

}
