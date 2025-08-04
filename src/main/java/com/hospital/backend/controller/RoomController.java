package com.hospital.backend.controller;

import com.hospital.backend.constant.APIConstants;
import com.hospital.backend.dto.request.room.RoomRequest;
import com.hospital.backend.dto.request.room.RoomIdRequest;
import com.hospital.backend.entity.Room;
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

    @PostMapping(APIConstants.API_CREATE_ROOM)
    public ResponseEntity<Room> create(@RequestBody RoomRequest request) {
        return ResponseEntity.ok(roomService.createRoom(request));
    }

    @PostMapping(APIConstants.API_UPDATE_ROOM)
    public ResponseEntity<Room> update(@RequestBody RoomIdAndRequest payload) {
        return ResponseEntity.ok(roomService.updateRoom(payload.getId(), payload.getRequest()));
    }

    @PostMapping(APIConstants.API_DELETE_ROOM)
    public ResponseEntity<String> delete(@RequestBody RoomIdRequest request) {
        roomService.deleteRoom(request.getId());
        return ResponseEntity.ok("Room deleted (soft)");
    }

    @PostMapping(APIConstants.API_GET_ROOM_BY_ID)
    public ResponseEntity<Room> getById(@RequestBody RoomIdRequest request) {
        return roomService.getRoomById(request.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(APIConstants.API_GET_ALL_ROOMS)
    public ResponseEntity<List<Room>> getAll() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    // Inner class cho update (gồm id + dữ liệu room)
    public static class RoomIdAndRequest {
        private UUID id;
        private RoomRequest request;

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public RoomRequest getRequest() {
            return request;
        }

        public void setRequest(RoomRequest request) {
            this.request = request;
        }
    }
}
