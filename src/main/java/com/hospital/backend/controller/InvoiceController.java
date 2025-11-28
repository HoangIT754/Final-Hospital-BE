package com.hospital.backend.controller;

import com.hospital.backend.constant.APIConstants;
import com.hospital.backend.dto.request.invoice.InvoiceCreateFromMedicalRecordRequest;
import com.hospital.backend.dto.request.invoice.InvoiceGetByMedicalRecordRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InvoiceController {
    private final InvoiceService invoiceService;

    @PostMapping(value = APIConstants.API_CREATE_FROM_MEDICAL_RECORD)
    public ResponseEntity<BaseResponse> createFromMedicalRecord(
            @RequestBody InvoiceCreateFromMedicalRecordRequest request) {
        long begin = System.currentTimeMillis();
        BaseResponse res = invoiceService.createFromMedicalRecord(request);
        res.setTook(System.currentTimeMillis() - begin);
        return ResponseEntity.ok(res);
    }

    @PostMapping(value = APIConstants.API_GET_BY_MEDICAL_RECORD)
    public ResponseEntity<BaseResponse> getByMedicalRecord(
            @RequestBody InvoiceGetByMedicalRecordRequest request) {
        long begin = System.currentTimeMillis();
        BaseResponse res = invoiceService.getByMedicalRecord(request);
        res.setTook(System.currentTimeMillis() - begin);
        return ResponseEntity.ok(res);
    }
}
