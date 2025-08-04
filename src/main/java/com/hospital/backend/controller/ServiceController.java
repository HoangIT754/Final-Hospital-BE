package com.hospital.backend.controller;

import com.hospital.backend.constant.APIConstants;
import com.hospital.backend.dto.request.service.ServiceRequest;
import com.hospital.backend.entity.Service;
import com.hospital.backend.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @PostMapping(APIConstants.API_CREATE_SERVICE)
    public Service create(@RequestBody ServiceRequest request) {
        return serviceService.create(request);
    }

    @PostMapping(APIConstants.API_UPDATE_SERVICE)
    public Service update(@RequestBody ServiceRequest request) {
        return serviceService.update(request);
    }

    @PostMapping(APIConstants.API_DELETE_SERVICE)
    public void delete(@RequestBody ServiceRequest request) {
        serviceService.delete(request.getId());
    }

    @PostMapping(APIConstants.API_GET_SERVICE_BY_ID)
    public Service getById(@RequestBody ServiceRequest request) {
        return serviceService.getById(request.getId());
    }

    @PostMapping(APIConstants.API_GET_ALL_SERVICES)
    public List<Service> getAll() {
        return serviceService.getAll();
    }
}

