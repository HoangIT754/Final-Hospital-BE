package com.hospital.backend.repository;

import com.hospital.backend.entity.StaffProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StaffProfileRepository extends JpaRepository<StaffProfile, UUID> {
    @Query("""
        SELECT sp
        FROM StaffProfile sp
        JOIN sp.user u
        JOIN u.roles r
        WHERE r.name = 'DOCTOR'
    """)
    List<StaffProfile> findAllDoctors();

    @Query("""
        SELECT sp 
        FROM StaffProfile sp
        JOIN sp.user u
        WHERE u.username = :username
    """)
    Optional<StaffProfile> findProfileByUsername(String username);

    Optional<StaffProfile> findByUserId(UUID userId);
}
