package com.hospital.backend.repository;

import com.hospital.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsernameAndIdNot(String username, UUID id);
    Boolean existsByEmailAndIdNot(String email, UUID id);
    @Query("""
        SELECT r.name AS roleName, COUNT(u.id) AS totalUsers
        FROM User u
        JOIN u.roles r
        GROUP BY r.name
       """)
    List<Object[]> countUsersGroupByRole();

}
