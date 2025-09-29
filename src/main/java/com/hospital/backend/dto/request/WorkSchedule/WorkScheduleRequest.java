package com.hospital.backend.dto.request.WorkSchedule;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.DayOfWeek;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WorkScheduleRequest {
    UUID doctorId;        // Id của bác sĩ
    DayOfWeek dayOfWeek;  // Thứ trong tuần
    String startTime;     // VD: "08:00"
    String endTime;       // VD: "17:00"
}
