package com.hospital.backend.dto.response.room;

import com.hospital.backend.dto.response.specialty.SpecialtyResponse;
import com.hospital.backend.entity.Room;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomResponse {
    UUID id;
    String roomNo;
    Room.RoomType roomType;
    Integer capacity;
    Room.RoomStatus status;
    String description;
    boolean isActive;

    // specialty là object đơn giản
    SpecialtyResponse specialty;

    UUID floorId;
    String floorName;
    UUID areaId;
    String areaName;
}
