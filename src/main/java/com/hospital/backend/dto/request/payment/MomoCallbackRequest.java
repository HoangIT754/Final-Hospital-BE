package com.hospital.backend.dto.request.payment;

import lombok.Data;

@Data
public class MomoCallbackRequest {

    private String partnerCode;
    private String orderId;
    private String requestId;
    private long amount;
    private String orderInfo;
    private String orderType;
    private String transId;
    private int resultCode;
    private String message;
    private String payType;
    private long responseTime;
    private String extraData;
    private String signature;
}
