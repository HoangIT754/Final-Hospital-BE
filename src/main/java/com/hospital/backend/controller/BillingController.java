package com.hospital.backend.controller;

import com.hospital.backend.constant.APIConstants;
import com.hospital.backend.dto.request.billing.BillingSummaryRequest;
import com.hospital.backend.dto.request.patient.PatientRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.service.BillingService;
import com.hospital.backend.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BillingController {
    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @PostMapping(value = APIConstants.API_GET_BILLING_SUMMARY)
    public ResponseEntity<BaseResponse> getBillingSummary(@RequestBody BillingSummaryRequest request) {
        long beginTime = System.currentTimeMillis();
        BaseResponse response = billingService.getBillingSummary(request);
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
