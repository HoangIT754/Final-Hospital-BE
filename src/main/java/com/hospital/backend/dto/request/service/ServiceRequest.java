package com.hospital.backend.dto.request.service;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceRequest {

    UUID id;

    String name;

    String code;

    String description;

    BigDecimal price;

    Integer durationMinutes;

    Boolean isActive;

    Set<UUID> labTestIds;
}

