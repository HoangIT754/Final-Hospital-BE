package com.hospital.backend.controller;

import com.hospital.backend.constant.APIConstants;
import com.hospital.backend.dto.request.invoice.InvoiceCreateFromMedicalRecordRequest;
import com.hospital.backend.dto.request.payment.CashPaymentRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.service.InvoiceService;
import com.hospital.backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping(value = APIConstants.API_PAY_BY_CASH)
    public ResponseEntity<BaseResponse> payByCash(
            @RequestBody CashPaymentRequest request) {
        long begin = System.currentTimeMillis();
        BaseResponse res = paymentService.payByCash(request);
        res.setTook(System.currentTimeMillis() - begin);
        return ResponseEntity.ok(res);
    }
}
