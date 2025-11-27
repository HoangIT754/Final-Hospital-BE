package com.hospital.backend.controller;

import com.hospital.backend.constant.APIConstants;
import com.hospital.backend.dto.request.labTestOrder.LabTestOrderCreateRequest;
import com.hospital.backend.dto.request.medicine.MedicineRequest;
import com.hospital.backend.dto.request.medicine.MedicineSearchRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.service.LabTestOrderService;
import com.hospital.backend.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MedicineController {
    private final MedicineService medicineService;

    @PostMapping(
            value = APIConstants.API_CREATE_MEDICINE
//            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<BaseResponse> createMedicine(@ModelAttribute MedicineRequest request) {
        long beginTime = System.currentTimeMillis();
        BaseResponse response = medicineService.createMedicine(request);
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping(value = APIConstants.API_GET_ALL_MEDICINE)
    public ResponseEntity<BaseResponse> getAllMedicine() {
        long beginTime = System.currentTimeMillis();
        BaseResponse response = medicineService.getAllMedicines();
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping(value = APIConstants.API_SEARCH_MEDICINE)
    public ResponseEntity<BaseResponse> searhMedicine(@RequestBody MedicineSearchRequest request) {
        long beginTime = System.currentTimeMillis();
        BaseResponse response = medicineService.searchMedicine(request);
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
