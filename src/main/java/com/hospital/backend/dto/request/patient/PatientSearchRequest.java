package com.hospital.backend.dto.request.patient;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PatientSearchRequest {
    private String firstName;
    private String lastName;
    private String identityNumber;
    private String phoneNumber;
    private String gender;
    private String status;
    private List<String> ages;
}
