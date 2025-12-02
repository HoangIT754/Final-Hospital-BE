package com.hospital.backend.dto.request.Jasper;

import com.hospital.backend.entity.PatientProfile;
import lombok.Data;

@Data
public class DataRequest {
    private String typeData;
    private String exportType; // pdf, xlsx, docx

    // PatientProfile
    private PatientProfile patientProfile;
}
