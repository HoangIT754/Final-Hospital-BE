package com.hospital.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
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

    @Column(name = "code", unique = true)
    String code; // Mã thuốc nội bộ / theo danh mục BYT

    @Column(name = "name", nullable = false)
    String name; // Tên thuốc

    @Column(name = "description")
    String description; // mô tả ngắn, công dụng

    @Column(name = "form")
    String form; // dạng bào chế: tablet, capsule, syrup...

    @Column(name = "strength")
    String strength; // độ mạnh: 500mg, 5mg/ml...

    @Column(name = "unit")
    String unit; // Đơn vị tính (viên, lọ...)

    @Column(name = "stock")
    Integer stock; // Tồn kho hiện tại

    @NotNull
    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    BigDecimal price;

    @NotNull
    @Column(name = "currency", nullable = false)
    String currency;

    @Column(name = "manufacturer")
    String manufacturer; // hãng/nhà sản xuất

    @Column(name = "is_active", nullable = false)
    Boolean isActive = true; // ẩn/hiện thuốc trong hệ thống

    @Column(name = "image_url")
    String imageUrl;

    @Column(name = "image_public_id")
    String imagePublicId;

    @OneToMany(mappedBy = "medicine")
    List<PrescriptionItem> prescriptionItems;
}
