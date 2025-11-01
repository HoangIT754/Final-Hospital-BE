package com.hospital.backend.repository;

import com.hospital.backend.entity.Area;
import com.hospital.backend.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {
    @Query("""
        SELECT r FROM Room r
        JOIN r.floor f
        JOIN f.area a
        WHERE r.isDeleted = false
        AND (:roomNo IS NULL OR LOWER(r.roomNo) LIKE LOWER(CONCAT('%', :roomNo, '%')))
        AND (:roomType IS NULL OR r.roomType IN (:roomType))
        AND (:areaIds IS NULL OR a.id IN (:areaIds))
        AND (:status IS NULL OR r.status IN (:status))
        AND (:specialtyIds IS NULL OR r.specialty.id IN (:specialtyIds))
        AND (:floorIds IS NULL OR f.id IN (:floorIds))
        AND (:isActive IS NULL OR r.isActive = :isActive)
        ORDER BY a.name ASC, f.name ASC, r.roomNo ASC
    """)
    List<Room> searchRooms(
            @Param("roomNo") String roomNo,
            @Param("roomType") List<Room.RoomType> roomType,
            @Param("areaIds") List<UUID> areaIds,
            @Param("status") List<Room.RoomStatus> status,
            @Param("floorIds") List<UUID> floorIds,
            @Param("specialtyIds") List<UUID> specialtyIds,
            @Param("isActive") Boolean isActive
    );
}
