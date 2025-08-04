package com.hospital.backend.service;

import com.hospital.backend.dto.request.patient.PatientRequest;
import com.hospital.backend.entity.PatientProfile;
import com.hospital.backend.entity.User;
import com.hospital.backend.repository.PatientProfileRepository;
import com.hospital.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientProfileRepository patientProfileRepository;
    private final UserRepository userRepository;

    @Transactional
    public PatientProfile createPatient(PatientRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        PatientProfile patient = new PatientProfile();
        patient.setUser(user);
        patient.setIdentityNumber(request.getIdentityNumber());
        patient.setHealthInsuranceNumber(request.getHealthInsuranceNumber());
        patient.setEmergencyContact(request.getEmergencyContact());
        patient.setMedicalHistory(request.getMedicalHistory());
        patient.setAllergies(request.getAllergies());
        patientProfileRepository.save(patient);

        return patient;
    }

    public List<PatientProfile> getAllPatients() {
        return patientProfileRepository.findAll();
    }
}
