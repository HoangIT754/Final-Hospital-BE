package com.hospital.backend.repository;

import com.hospital.backend.entity.Appointment;
import com.hospital.backend.entity.PatientProfile;
import com.hospital.backend.entity.PatientStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientProfileRepository extends JpaRepository<PatientProfile, UUID> {

    long countByStatus(PatientStatus status);

    @Query("SELECT p.status.id, COUNT(p) FROM PatientProfile p GROUP BY p.status.id")
    List<Object[]> countPatientsGroupByStatus();

    @Query("""
        SELECT p FROM PatientProfile p
        WHERE (:firstName IS NULL OR LOWER(p.firstName) LIKE :firstName)
          AND (:lastName IS NULL OR LOWER(p.lastName) LIKE :lastName)
          AND (:identityNumber IS NULL OR p.identityNumber = :identityNumber)
          AND (:phoneNumber IS NULL OR p.phoneNumber = :phoneNumber)
          AND (:gender IS NULL OR p.gender = :gender)
          AND (:status IS NULL OR p.status = :status)
    """)
    List<PatientProfile> searchPatients(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("identityNumber") String identityNumber,
            @Param("phoneNumber") String phoneNumber,
            @Param("gender") String gender,
            @Param("status") String status
    );

    Optional<PatientProfile> findByUserId(UUID userId);
}
