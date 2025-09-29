package com.hospital.backend.dto.request.specialty;

import com.hospital.backend.entity.DoctorProfile;
import lombok.Data;

import java.util.List;

@Data
public class SpecialtyRequest {
    private String name;
    private String description;
}
