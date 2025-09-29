package com.hospital.backend.controller;

import com.hospital.backend.constant.APIConstants;
import com.hospital.backend.dto.request.Role.RoleRequest;
import com.hospital.backend.dto.request.WorkSchedule.WorkScheduleRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.service.WorkScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WorkScheduleController {

    private final WorkScheduleService workScheduleService;

    @PostMapping(APIConstants.API_WORK_SCHEDULE_CREATE)
    public ResponseEntity<BaseResponse> createWorkSchedule(@RequestBody WorkScheduleRequest request) {
        long beginTime = System.currentTimeMillis();
        BaseResponse response = workScheduleService.createWorkSchedule(request);
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
