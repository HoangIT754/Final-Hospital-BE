package com.hospital.backend.controller;

import com.hospital.backend.constant.APIConstants;
import com.hospital.backend.dto.request.patient.PatientRequest;
import com.hospital.backend.entity.PatientProfile;
import com.hospital.backend.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    // POST: Create new appointment
    @PostMapping(value = APIConstants.API_CREATE_PATIENT)
    public ResponseEntity<PatientProfile> createAppointment(@RequestBody PatientRequest request) {
        return ResponseEntity.ok(patientService.createPatient(request));
    }

    @PostMapping(value = APIConstants.API_GET_ALL_PATIENT)
    public ResponseEntity<List<PatientProfile>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }
}
