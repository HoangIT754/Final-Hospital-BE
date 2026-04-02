package com.hospital.backend.dto.request.labTestOrder;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LabTestOrderCreateRequest {
    UUID medicalRecordId;
    String testType;
    List<UUID> serviceIds;
    List<UUID> labTestIds;
}
