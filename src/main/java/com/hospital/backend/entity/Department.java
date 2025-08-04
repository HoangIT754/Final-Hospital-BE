package com.hospital.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "department")
public class Department extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    UUID id;

    // Tên khoa (VD: Nội tổng hợp, Ngoại thần kinh...)
    @NotNull
    @Column(name = "name", unique = true)
    String name;

    // Mã khoa (ví dụ: "INT", "SUR", "PED")
    @Column(name = "code", unique = true)
    String code;

    // Mô tả khoa (nội dung hoạt động, chuyên môn)
    @Column(name = "description")
    String description;

    // Khoa còn hoạt động hay không
    @Column(name = "is_active")
    Boolean isActive = true;

    // Danh sách các phòng thuộc khoa này (OneToMany)
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Room> rooms;
}
