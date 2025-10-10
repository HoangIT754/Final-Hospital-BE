package com.hospital.backend.repository;

import com.hospital.backend.entity.StaffProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StaffProfileRepository extends JpaRepository<StaffProfile, UUID> {
}
