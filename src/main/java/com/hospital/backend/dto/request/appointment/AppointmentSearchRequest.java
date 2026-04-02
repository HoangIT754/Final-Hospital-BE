package com.hospital.backend.dto.request.appointment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppointmentSearchRequest {
    String startDate;
    String endDate;
    String patientId;
    String doctorId;
    String status;
}
