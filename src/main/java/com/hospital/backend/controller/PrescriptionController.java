package com.hospital.backend.controller;

import com.hospital.backend.constant.APIConstants;
import com.hospital.backend.dto.request.patient.PatientRequest;
import com.hospital.backend.dto.request.prescription.GetAllPrescriptionByIsDeletedRequest;
import com.hospital.backend.dto.request.prescription.PrescriptionCreateRequest;
import com.hospital.backend.dto.request.prescription.PrescriptionSearchRequest;
import com.hospital.backend.dto.request.prescription.PrescriptionUpdateRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.service.PatientService;
import com.hospital.backend.service.PrescriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PrescriptionController{

    PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @PostMapping(value = APIConstants.API_CREATE_PRESCRIPTION)
    public ResponseEntity<BaseResponse> createPrescription(@RequestBody PrescriptionCreateRequest request) {
        long beginTime = System.currentTimeMillis();
        BaseResponse response = prescriptionService.createPrescription(request);
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping(value = APIConstants.API_GET_BY_IS_DELETED)
    public ResponseEntity<BaseResponse> getAllByIsDeleted(@RequestBody GetAllPrescriptionByIsDeletedRequest request) {
        long beginTime = System.currentTimeMillis();
        BaseResponse response = prescriptionService.getAllByIsDeleted(request);
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping(value = APIConstants.API_UPDATE_PRESCRIPTION)
    public ResponseEntity<BaseResponse> updatePrescription(@RequestBody PrescriptionUpdateRequest request) {
        long beginTime = System.currentTimeMillis();
        BaseResponse response = prescriptionService.updatePrescription(request);
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping(value = APIConstants.API_SEARCH_PRESCRIPTION)
    public ResponseEntity<BaseResponse> searchPrescription(@RequestBody PrescriptionSearchRequest request) {
        long beginTime = System.currentTimeMillis();
        BaseResponse response = prescriptionService.searchPrescriptions(request);
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
