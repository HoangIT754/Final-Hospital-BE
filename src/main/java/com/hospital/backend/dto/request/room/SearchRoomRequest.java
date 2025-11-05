package com.hospital.backend.dto.request.room;

import com.hospital.backend.entity.Area;
import com.hospital.backend.entity.Room;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class SearchRoomRequest {
    private String roomNo;
    private List<Room.RoomType> roomType;
    private List<UUID> area;
    private List<Room.RoomStatus> status;
    private List<UUID> floor;
    private List<UUID> specialtyId;
    private Boolean isActive;
}
