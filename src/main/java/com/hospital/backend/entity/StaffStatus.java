package com.hospital.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

/**
 * Master data cho trạng thái của nhân viên.
 * Ví dụ: ACTIVE, INACTIVE, ON_LEAVE, TERMINATED...
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "cfg_staff_status")
public class StaffStatus extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @NotNull
    UUID id; // ID duy nhất cho trạng thái

    @NotNull
    @Column(name = "code", unique = true, length = 50)
    String code; // Mã trạng thái (ví dụ: ACTIVE, INACTIVE)

    @NotNull
    @Column(name = "name", length = 100)
    String name; // Tên trạng thái (ví dụ: Đang hoạt động, Nghỉ việc)

    @Column(name = "description")
    String description; // Mô tả chi tiết trạng thái
}
