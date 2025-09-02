package com.hospital.backend.repository;

import com.hospital.backend.entity.PatientStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PatientStatusRepository extends JpaRepository<PatientStatus, UUID> {
}
