package com.hospital.backend.controller;

import com.hospital.backend.constant.APIConstants;
import com.hospital.backend.dto.request.labTest.LabTestRequest;
import com.hospital.backend.dto.request.labTestOrder.LabTestOrderCreateRequest;
import com.hospital.backend.dto.request.labTestOrder.LabTestOrderRequest;
import com.hospital.backend.dto.request.labTestOrder.UpdateLabTestOrderDetailWithFileRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.service.LabTestOrderService;
import com.hospital.backend.service.LabTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class LabTestOrderController {
    private final LabTestOrderService labTestOrderService;

    @PostMapping(value = APIConstants.API_CREATE_LAB_TEST_ORDER)
    public ResponseEntity<BaseResponse> createLabTestOrder(@RequestBody LabTestOrderCreateRequest request) {
        long beginTime = System.currentTimeMillis();
        BaseResponse response = labTestOrderService.createLabTestOrder(request);
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping(value = APIConstants.API_GET_ALL_LAB_TESTS_ORDER)
    public ResponseEntity<BaseResponse> getAllLabTestOrders() {
        long beginTime = System.currentTimeMillis();
        BaseResponse response = labTestOrderService.getAllLabTestOrders();
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping(value = APIConstants.API_GET_LAB_TESTS_ORDER_BY_ID)
    public ResponseEntity<BaseResponse> getLabTestOrdersById(@RequestBody LabTestOrderRequest request) {
        long beginTime = System.currentTimeMillis();
        BaseResponse response = labTestOrderService.getLabTestOrderById(request);
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping(value = APIConstants.API_UPDATE_LAB_TEST_ORDER_DETAIL_WITH_FILE)
    public ResponseEntity<BaseResponse> updateLabTestOrderDetailWithFile(@ModelAttribute UpdateLabTestOrderDetailWithFileRequest request) {
        long beginTime = System.currentTimeMillis();
        BaseResponse response = labTestOrderService.updateLabTestOrderDetailWithFile(request);
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping(value = APIConstants.API_MARK_COMPLETE)
    public ResponseEntity<BaseResponse> markLabTetOrderComplete(@RequestBody LabTestOrderRequest request) {
        long beginTime = System.currentTimeMillis();
        BaseResponse response = labTestOrderService.completeLabTestOrder(request);
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
