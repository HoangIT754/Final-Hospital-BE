package com.hospital.backend.service;

import com.hospital.backend.dto.request.labTest.LabTestRequest;
import com.hospital.backend.entity.LabTest;
import com.hospital.backend.repository.LabTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LabTestService {

    private final LabTestRepository labTestRepository;

    public LabTest create(LabTestRequest request) {
        LabTest labTest = new LabTest();
        labTest.setName(request.getName());
        labTest.setCode(request.getCode());
        labTest.setDescription(request.getDescription());
        labTest.setUnit(request.getUnit());
        labTest.setReferenceRange(request.getReferenceRange());
        labTest.setPrice(request.getPrice());
        labTest.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        return labTestRepository.save(labTest);
    }

    public LabTest update(LabTestRequest request) {
        LabTest labTest = labTestRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("LabTest not found"));
        labTest.setName(request.getName());
        labTest.setCode(request.getCode());
        labTest.setDescription(request.getDescription());
        labTest.setUnit(request.getUnit());
        labTest.setReferenceRange(request.getReferenceRange());
        labTest.setPrice(request.getPrice());
        labTest.setIsActive(request.getIsActive());
        return labTestRepository.save(labTest);
    }

    public void delete(UUID id) {
        LabTest labTest = labTestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("LabTest not found"));
        labTest.setIsDeleted(true); // dùng trường kế thừa từ AuditModel
        labTestRepository.save(labTest);
    }

    public LabTest getById(UUID id) {
        return labTestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("LabTest not found"));
    }

    public List<LabTest> getAll() {
        return labTestRepository.findAll();
    }
}
