package com.hospital.backend.controller;

import com.hospital.backend.constant.APIConstants;
import com.hospital.backend.dto.request.patient.PatientRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping(value = APIConstants.API_CREATE_PATIENT)
    public ResponseEntity<BaseResponse> createPatient(@RequestBody PatientRequest request) {
        long beginTime = System.currentTimeMillis();
        BaseResponse response = patientService.createPatient(request);
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping(value = APIConstants.API_GET_ALL_PATIENT)
    public ResponseEntity<BaseResponse> getAllPatients() {
        long beginTime = System.currentTimeMillis();
        BaseResponse response = patientService.getAllPatients();
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
