package com.hospital.backend.dto.response.medicine;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MedicineResponse {

    UUID id;              // Id của thuốc
    String code;          // Mã thuốc
    String name;          // Tên thuốc
    String description;   // Mô tả ngắn
    String form;          // Dạng bào chế
    String strength;      // Hàm lượng
    String unit;          // Đơn vị tính
    Integer stock;        // Tồn kho
    BigDecimal price;     // Giá
    String currency;      // Tiền tệ
    String manufacturer;  // Nhà sản xuất
    Boolean isActive;     // Trạng thái hoạt động
    String imageUrl;      // Ảnh hiển thị
}
