package com.hospital.backend.controller;

import com.hospital.backend.constant.APIConstants;
import com.hospital.backend.dto.request.room.RoomRequest;
import com.hospital.backend.dto.request.room.SearchRoomRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping(value = APIConstants.API_CREATE_ROOM)
    public ResponseEntity<BaseResponse> createRoom(@RequestBody RoomRequest request) {
        long beginTime = System.currentTimeMillis();
        BaseResponse response = roomService.createRoom(request);
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping(APIConstants.API_UPDATE_ROOM)
    public ResponseEntity<BaseResponse> updateRoom(@RequestBody RoomRequest request) {
        long beginTime = System.currentTimeMillis();
        BaseResponse response = roomService.updateRoom(request);
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping(APIConstants.API_DELETE_ROOM)
    public ResponseEntity<BaseResponse> delete(@RequestBody RoomRequest request) {
        long beginTime = System.currentTimeMillis();
        BaseResponse response = roomService.deleteRoom(request);
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping(APIConstants.API_GET_ROOM_BY_ID)
    public ResponseEntity<BaseResponse> getById(@RequestBody RoomRequest request) {
        long beginTime = System.currentTimeMillis();
        BaseResponse response = roomService.getRoomById(request);
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping(APIConstants.API_SEARCH_ROOMS)
    public ResponseEntity<BaseResponse> searchRoom(@RequestBody SearchRoomRequest request) {
        long beginTime = System.currentTimeMillis();
        BaseResponse response = roomService.searchRooms(request);
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping(APIConstants.API_GET_ALL_ROOMS)
    public ResponseEntity<BaseResponse> getAll() {
        long beginTime = System.currentTimeMillis();
        BaseResponse response = roomService.getAllRooms();
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

}
