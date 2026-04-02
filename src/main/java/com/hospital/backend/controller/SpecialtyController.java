package com.hospital.backend.controller;

import com.hospital.backend.constant.APIConstants;
import com.hospital.backend.dto.request.patient.PatientRequest;
import com.hospital.backend.dto.request.specialty.SpecialtyRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.service.SpecialtyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpecialtyController {
    private SpecialtyService specialtyService;

    public SpecialtyController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    @PostMapping(value = APIConstants.API_CREATE_SPECIALTY)
    public ResponseEntity<BaseResponse> createSpecialty(@RequestBody SpecialtyRequest request) {
        long beginTime = System.currentTimeMillis();
        BaseResponse response = specialtyService.createSpecialty(request);
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping(value = APIConstants.API_GET_ALL_SPECIALTITIES)
    public ResponseEntity<BaseResponse> getAllSpecialties() {
        long beginTime = System.currentTimeMillis();
        BaseResponse response = specialtyService.getAllSpecialties();
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
