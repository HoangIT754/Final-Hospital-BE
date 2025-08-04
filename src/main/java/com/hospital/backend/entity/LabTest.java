package com.hospital.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "lab_test")
public class LabTest extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    UUID id;

    @NotBlank
    @Column(name = "name", nullable = false, unique = true)
    String name; // Tên xét nghiệm (VD: Huyết học tổng quát, Glucose máu, Xét nghiệm nước tiểu,...)

    @Column(name = "code", unique = true)
    String code; // Mã xét nghiệm nội bộ (VD: LAB001)

    @Column(name = "description")
    String description; // Mô tả chi tiết xét nghiệm

    @Column(name = "unit")
    String unit; // Đơn vị đo (VD: g/L, mmol/L,...)

    @Column(name = "reference_range")
    String referenceRange; // Khoảng giá trị bình thường (VD: 4.0 - 5.9)

    @NotNull
    @Column(name = "price", nullable = false)
    Double price; // Giá tiền của xét nghiệm

    @Column(name = "is_active")
    Boolean isActive = true; // Xét nghiệm còn hoạt động hay đã ngừng

    @ManyToMany(mappedBy = "labTests")
    Set<Service> services = new HashSet<>();
}
