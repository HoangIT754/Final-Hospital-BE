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
@Table(name = "lab_test_order_detail")
public class LabTestOrderDetail extends AuditModel{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    LabTestOrder order;

    @ManyToOne
    @JoinColumn(name = "lab_test_id")
    LabTest labTest;

    @Column(name = "result")
    String result; // Kết quả riêng cho từng LabTest

    @Column(name = "status")
    String status; // PENDING, COMPLETED, etc
}

