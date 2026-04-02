package com.hospital.backend.dto.request.prescription;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrescriptionCreateRequest {
    UUID medicalRecordId;
    String notes;
    List<PrescriptionItemRequest> items;
}
