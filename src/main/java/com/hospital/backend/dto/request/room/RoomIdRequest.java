package com.hospital.backend.dto.request.room;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class RoomIdRequest {
    private UUID id;
}
