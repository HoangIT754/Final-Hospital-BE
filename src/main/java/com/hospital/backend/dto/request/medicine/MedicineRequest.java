package com.hospital.backend.dto.request.medicine;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MedicineRequest {
    String code;          // Mã thuốc
    String name;          // Tên thuốc (bắt buộc)
    String description;   // Mô tả
    String form;          // Dạng bào chế
    String strength;      // Hàm lượng
    String unit;          // Đơn vị
    Integer stock;        // Tồn kho
    BigDecimal price;     // Giá (bắt buộc)
    String currency;      // Đơn vị tiền (bắt buộc)
    String manufacturer;  // Hãng SX
    Boolean isActive;
    String imageUrl;
}
