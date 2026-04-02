package com.hospital.backend.repository;

import com.hospital.backend.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, UUID> {
    boolean existsByNameIgnoreCase(String name);

    Optional<Specialty> findByName(String name);
}
