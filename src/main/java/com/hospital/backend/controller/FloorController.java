package com.hospital.backend.controller;

import com.hospital.backend.constant.APIConstants;
import com.hospital.backend.dto.request.Floor.FloorRequest;
import com.hospital.backend.dto.request.Floor.GetFloorByAreaRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.service.FloorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FloorController {
    private final FloorService floorService;

    public FloorController(FloorService floorService) {
        this.floorService = floorService;
    }

    @PostMapping(APIConstants.API_GET_FLOORS_BY_AREA)
    public ResponseEntity<BaseResponse> getFloorsByArea(@RequestBody GetFloorByAreaRequest request){
        long beginTime = System.currentTimeMillis();
        BaseResponse response = floorService.getFloorByArea(request);
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

}
