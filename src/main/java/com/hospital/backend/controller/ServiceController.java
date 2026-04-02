package com.hospital.backend.controller;

import com.hospital.backend.constant.APIConstants;
import com.hospital.backend.dto.request.labTest.LabTestRequest;
import com.hospital.backend.dto.request.service.ServiceRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.entity.Service;
import com.hospital.backend.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @PostMapping(value = APIConstants.API_CREATE_SERVICE)
    public ResponseEntity<BaseResponse> createService(@RequestBody ServiceRequest request) {
        long beginTime = System.currentTimeMillis();
        BaseResponse response = serviceService.createService(request);
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping(value = APIConstants.API_GET_ALL_SERVICES)
    public ResponseEntity<BaseResponse> getAllService() {
        long beginTime = System.currentTimeMillis();
        BaseResponse response = serviceService.getAllServices();
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}

