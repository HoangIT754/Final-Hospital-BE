package com.hospital.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "service")
public class Service extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    UUID id;

    @NotBlank
    @Column(name = "name", nullable = false, unique = true)
    String name; // Tên dịch vụ (VD: Khám tổng quát, Khám nội, Khám sản,...)

    @Column(name = "code", unique = true, length = 50)
    String code;

    @Column(name = "description")
    String description; // Mô tả chi tiết dịch vụ

    @NotNull
    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    BigDecimal price; // Giá tiền cho dịch vụ

    @Column(name = "duration_minutes")
    Integer durationMinutes; // Thời gian thực hiện dịch vụ (phút)

    @Column(name = "is_active")
    Boolean isActive = true; // Trạng thái hoạt động của dịch vụ

    // Dịch vụ có thể liên kết với nhiều xét nghiệm (nếu có)
    @ManyToMany
    @JoinTable(
            name = "service_lab_tests",
            joinColumns = @JoinColumn(name = "service_id"),
            inverseJoinColumns = @JoinColumn(name = "lab_test_id")
    )
    @JsonIgnore
    Set<LabTest> labTests = new HashSet<>();
}
