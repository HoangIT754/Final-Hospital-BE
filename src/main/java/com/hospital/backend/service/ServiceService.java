package com.hospital.backend.service;

import com.hospital.backend.dto.request.service.ServiceRequest;
import com.hospital.backend.entity.LabTest;
import com.hospital.backend.entity.Service;
import com.hospital.backend.repository.LabTestRepository;
import com.hospital.backend.repository.ServiceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final LabTestRepository labTestRepository;

    public Service create(ServiceRequest request) {
        Service service = new Service();
        mapRequestToEntity(request, service);
        return serviceRepository.save(service);
    }

    public Service update(ServiceRequest request) {
        Service service = serviceRepository.findById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException("Service not found"));
        mapRequestToEntity(request, service);
        return serviceRepository.save(service);
    }

    public void delete(UUID id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service not found"));
        serviceRepository.delete(service);
    }

    public Service getById(UUID id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service not found"));
    }

    public List<Service> getAll() {
        return serviceRepository.findAll();
    }

    private void mapRequestToEntity(ServiceRequest request, Service service) {
        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setPrice(request.getPrice());
        service.setDurationMinutes(request.getDurationMinutes());
        service.setIsActive(request.getIsActive());

        if (request.getLabTestIds() != null) {
            Set<LabTest> labTests = request.getLabTestIds().stream()
                    .map(id -> labTestRepository.findById(id)
                            .orElseThrow(() -> new EntityNotFoundException("LabTest not found: " + id)))
                    .collect(Collectors.toSet());
            service.setLabTests(labTests);
        }
    }
}
