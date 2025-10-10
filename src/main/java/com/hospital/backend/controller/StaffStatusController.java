package com.hospital.backend.controller;

import com.hospital.backend.constant.APIConstants;
import com.hospital.backend.dto.request.role.RoleRequest;
import com.hospital.backend.dto.request.staffStatus.StaffStatusRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.service.StaffStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StaffStatusController {

    StaffStatusService staffStatusService;

    public StaffStatusController(StaffStatusService staffStatusService) {
        this.staffStatusService = staffStatusService;
    }

    @PostMapping(value = APIConstants.API_CREATE_STAFF_STATUS)
    public ResponseEntity<BaseResponse> createRole(@RequestBody StaffStatusRequest request) {
        long beginTime = System.currentTimeMillis();
        BaseResponse response = staffStatusService.createStaffStatus(request);
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
