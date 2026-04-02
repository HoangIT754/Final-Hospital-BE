package com.hospital.backend.controller;

import com.hospital.backend.constant.APIConstants;
import com.hospital.backend.dto.request.room.SearchRoomRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.service.AreaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AreaController{

    private final AreaService areaService;

    public AreaController(AreaService areaService) {
        this.areaService = areaService;
    }

    @PostMapping(APIConstants.API_GET_ALL_AREA)
    public ResponseEntity<BaseResponse> getAllArea() {
        long beginTime = System.currentTimeMillis();
        BaseResponse response = areaService.getAllArea();
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
