package com.hospital.backend.repository;

import com.hospital.backend.entity.Floor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FloorRepository extends JpaRepository<Floor, UUID> {
    @Query("SELECT f FROM Floor f WHERE f.area.id = :areaId")
    List<Floor> findFloorsByAreaId(@Param("areaId") UUID areaId);
}
