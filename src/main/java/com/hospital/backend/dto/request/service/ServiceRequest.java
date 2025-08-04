package com.hospital.backend.dto.request.service;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceRequest {

    UUID id; // Dùng cho update/delete/get-by-id

    String name;

    String description;

    Double price;

    Integer durationMinutes;

    Boolean isActive;

    Set<UUID> labTestIds; // Danh sách ID các LabTest liên kết
}

