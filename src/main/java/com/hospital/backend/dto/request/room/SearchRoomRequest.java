package com.hospital.backend.dto.request.room;

import com.hospital.backend.entity.Area;
import com.hospital.backend.entity.Room;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class SearchRoomRequest {
    private UUID id;

    @NotNull
    private String roomNo;

    @NotNull
    private List<Room.RoomType> roomType;

    @NotNull
    private List<UUID> area;

    private List<UUID> specialtyId;

    private List<UUID> floor;

    private Integer capacity;

    private List<Room.RoomStatus> status;

    private String description;

    private Boolean isActive = true;
}
