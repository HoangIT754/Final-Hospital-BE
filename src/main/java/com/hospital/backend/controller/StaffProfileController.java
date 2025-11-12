package com.hospital.backend.controller;

import com.hospital.backend.constant.APIConstants;
import com.hospital.backend.dto.request.staff.StaffProfileRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.entity.StaffProfile;
import com.hospital.backend.service.StaffProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StaffProfileController {

    private final StaffProfileService staffProfileService;

    public StaffProfileController(StaffProfileService staffProfileService) {
        this.staffProfileService = staffProfileService;
    }

    @PostMapping(value = APIConstants.API_CREATE_STAFF)
    public ResponseEntity<BaseResponse> createStaff(@RequestBody StaffProfileRequest request) {
        long beginTime = System.currentTimeMillis();
        BaseResponse response = staffProfileService.createStaff(request);
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping(value = APIConstants.API_GET_ALL_STAFF)
    public ResponseEntity<List<StaffProfile>> getAllStaffs() {
        List<StaffProfile> staffs = staffProfileService.getAllStaffs();
        return ResponseEntity.ok(staffs);
    }

    @PostMapping(value = APIConstants.API_GET_ALL_DOCTOR)
    public ResponseEntity<List<StaffProfile>> getAllDoctors() {
        List<StaffProfile> doctors = staffProfileService.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }
}
