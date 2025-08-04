package com.hospital.backend.dto.request.doctor;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DoctorProfileRequest {
    @NotNull
    UUID userId; // Id của user liên kết với doctor

    String specialty; // Chuyên khoa

    String description; // Mô tả chi tiết về bác sĩ

    String workSchedule; // Lịch làm việc
}
