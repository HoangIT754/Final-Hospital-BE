package com.hospital.backend.controller;

import com.hospital.backend.constant.APIConstants;
import com.hospital.backend.dto.request.labTest.LabTestRequest;
import com.hospital.backend.entity.LabTest;
import com.hospital.backend.service.LabTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LabTestController {

    private final LabTestService labTestService;

    @PostMapping(APIConstants.API_CREATE_LAB_TEST)
    public LabTest create(@RequestBody LabTestRequest request) {
        return labTestService.create(request);
    }

    @PostMapping(APIConstants.API_UPDATE_LAB_TEST)
    public LabTest update(@RequestBody LabTestRequest request) {
        return labTestService.update(request);
    }

    @PostMapping(APIConstants.API_DELETE_LAB_TEST)
    public void delete(@RequestBody LabTestRequest request) {
        labTestService.delete(request.getId());
    }

    @PostMapping(APIConstants.API_GET_LAB_TEST_BY_ID)
    public LabTest getById(@RequestBody LabTestRequest request) {
        return labTestService.getById(request.getId());
    }

    @PostMapping(APIConstants.API_GET_ALL_LAB_TESTS)
    public List<LabTest> getAll() {
        return labTestService.getAll();
    }
}
