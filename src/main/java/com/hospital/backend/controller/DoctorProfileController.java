package com.hospital.backend.controller;

import com.hospital.backend.constant.APIConstants;
import com.hospital.backend.dto.request.doctor.DoctorProfileRequest;
import com.hospital.backend.dto.request.patient.PatientRequest;
import com.hospital.backend.dto.response.BaseResponse;
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
    public ResponseEntity<BaseResponse> createPatient(@RequestBody DoctorProfileRequest request) {
        long beginTime = System.currentTimeMillis();
        BaseResponse response = doctorProfileService.createDoctor(request);
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping(value = APIConstants.API_GET_ALL_DOCTOR)
    public ResponseEntity<List<DoctorProfile>> getAllDoctors() {
        List<DoctorProfile> doctors = doctorProfileService.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }
}
