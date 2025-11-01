package com.hospital.backend.dto.request.Floor;

import com.hospital.backend.entity.Area;
import com.hospital.backend.entity.Room;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FloorRequest {
    String code;
    String name;
    String description;
    Area area;
    List<Room> rooms = new ArrayList<>();
}
