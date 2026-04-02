package com.hospital.backend.dto.request.medicine;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MedicineSearchRequest {
    String code;          // tìm theo mã (like)
    String name;          // tìm theo tên (like)
    String manufacturer;  // tìm theo hãng (like)
    Boolean isActive;     // filter theo trạng thái, có thể null
}
