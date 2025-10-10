package com.hospital.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "specialty")
public class Specialty extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    UUID id; // Id của chuyên khoa

    @Column(name = "name", nullable = false, unique = true, length = 100)
    String name; // Tên chuyên khoa (ví dụ: Nội tổng quát, Ngoại tổng quát)

    @Column(name = "description", columnDefinition = "TEXT")
    String description; // Mô tả chi tiết về chuyên khoa

    @OneToMany(mappedBy = "specialty", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    List<StaffProfile> staffs; // Danh sách bác sĩ thuộc chuyên khoa này

    // Danh sách các phòng thuộc khoa này (OneToMany)
    @OneToMany(mappedBy = "specialty", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Room> rooms;
}
