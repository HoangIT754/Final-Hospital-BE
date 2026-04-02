package com.hospital.backend.dto.request.room;

import com.hospital.backend.entity.Room;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class RoomRequest {
    private UUID id;

    @NotNull
    private String roomNo;

    @NotNull
    private Room.RoomType roomType;

    private UUID specialtyId;

    private UUID floor;

    private Integer capacity;

    private Room.RoomStatus status;

    private String description;

    private Boolean isActive = true;

//    private UUID areaId;
}
