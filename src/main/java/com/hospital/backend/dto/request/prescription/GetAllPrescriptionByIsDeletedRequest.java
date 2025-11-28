package com.hospital.backend.dto.request.prescription;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetAllPrescriptionByIsDeletedRequest {
    Boolean isDeleted;
}
