package com.hospital.backend.repository;

import com.hospital.backend.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AreaRepository extends JpaRepository<Area, UUID> {
    List<Area> findByIsDeletedFalse();
}
