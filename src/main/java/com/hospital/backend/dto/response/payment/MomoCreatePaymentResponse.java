package com.hospital.backend.dto.response.payment;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Builder
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MomoCreatePaymentResponse {
    String payUrl;     // url MoMo trả về (momo deeplink/web)
    String qrCodeUrl;  // nếu MoMo trả, hoặc bạn generate từ payUrl
    String orderId;    // orderId gửi sang MoMo
    String requestId;  // requestId
}
