package com.hospital.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

/**
 * Master data cho trạng thái bệnh nhân.
 * Dùng để hiển thị và thống kê trên dashboard.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "cfg_patient_status")
public class PatientStatus extends AuditModel{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    UUID id; // Khóa chính

    @NotNull
    @Column(name = "code", unique = true, length = 50)
    String code; // Mã trạng thái (WAITING, IN_TREATMENT, ...)

    @NotNull
    @Column(name = "name", length = 100)
    String name; // Tên hiển thị (Waiting, In Treatment...)

    @Column(name = "description", length = 255)
    String description; // Mô tả chi tiết

    @Column(name = "is_active")
    Boolean isActive = true; // Có được sử dụng hay không
}
