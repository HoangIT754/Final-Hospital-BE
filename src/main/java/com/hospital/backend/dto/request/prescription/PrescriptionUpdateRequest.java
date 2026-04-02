package com.hospital.backend.dto.request.prescription;

import com.hospital.backend.entity.Prescription;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrescriptionUpdateRequest {
    UUID id;
    String notes;
    Prescription.PrescriptionStatus status;
    List<PrescriptionItemRequest> items;
}
