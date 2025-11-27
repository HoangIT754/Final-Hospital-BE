package com.hospital.backend.repository;

import com.hospital.backend.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MedicineRepository extends JpaRepository<Medicine, UUID> {

    Optional<Medicine> findByCodeIgnoreCase(String code);

    @Query("SELECT m FROM Medicine m " +
            "WHERE (:code IS NULL OR LOWER(m.code) LIKE :code) " +
            "AND (:name IS NULL OR LOWER(m.name) LIKE :name) " +
            "AND (:manufacturer IS NULL OR LOWER(m.manufacturer) LIKE :manufacturer) " +
            "AND (:isActive IS NULL OR m.isActive = :isActive)")
    List<Medicine> searchMedicines(String code,
                                   String name,
                                   String manufacturer,
                                   Boolean isActive);
}
