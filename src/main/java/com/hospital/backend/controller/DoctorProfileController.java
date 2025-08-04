package com.hospital.backend.controller;

import com.hospital.backend.constant.APIConstants;
import com.hospital.backend.dto.request.doctor.DoctorProfileRequest;
import com.hospital.backend.entity.DoctorProfile;
import com.hospital.backend.service.DoctorProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DoctorProfileController {

    private final DoctorProfileService doctorProfileService;

    public DoctorProfileController(DoctorProfileService doctorProfileService) {
        this.doctorProfileService = doctorProfileService;
    }

    @PostMapping(value = APIConstants.API_CREATE_DOCTOR)
    public ResponseEntity<DoctorProfile> createDoctorProfile(@RequestBody DoctorProfileRequest request) {
        DoctorProfile doctorProfile = doctorProfileService.createDoctorProfile(request);
        return ResponseEntity.ok(doctorProfile);
    }

    @PostMapping(value = APIConstants.API_GET_ALL_DOCTOR)
    public ResponseEntity<List<DoctorProfile>> getAllDoctors() {
        List<DoctorProfile> doctors = doctorProfileService.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }
}
