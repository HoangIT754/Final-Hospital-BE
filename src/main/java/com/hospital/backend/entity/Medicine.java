package com.hospital.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "medicine")
public class Medicine extends AuditModel{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @NotNull
    UUID id; // Id của thuốc

    @Column(name = "name")
    String name; // Tên thuốc

    @Column(name = "unit")
    String unit; // Đơn vị tính (viên, lọ...)

    @Column(name = "stock")
    int stock; // Tồn kho hiện tại
}
