package com.hospital.backend.repository;

import com.hospital.backend.entity.StaffStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StaffStatusRepository extends JpaRepository<StaffStatus, UUID> {
    List<StaffStatus> findByIsDeletedFalse();
}
