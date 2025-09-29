package com.hospital.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Master data cho role.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "cfg_role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    UUID id; // Khóa chính

    @NotNull
    @Column(name = "name", length = 100)
    String name; // Tên hiển thị

    @Column(name = "description", length = 255)
    String description; // Mô tả chi tiết

    @Column(name = "is_active")
    Boolean isActive = true; // Có được sử dụng hay không

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    @JsonIgnore
    Set<User> users = new HashSet<>(); // Danh sách user có role này
}
