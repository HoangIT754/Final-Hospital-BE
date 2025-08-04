package com.hospital.backend.service;

import com.hospital.backend.dto.request.doctor.DoctorProfileRequest;
import com.hospital.backend.entity.DoctorProfile;
import com.hospital.backend.entity.User;
import com.hospital.backend.repository.DoctorProfileRepository;
import com.hospital.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorProfileService {
    private final DoctorProfileRepository doctorProfileRepository;
    private final UserRepository userRepository;

    DoctorProfileService(DoctorProfileRepository doctorProfileRepository, UserRepository userRepository) {
        this.doctorProfileRepository = doctorProfileRepository;
        this.userRepository = userRepository;
    }

    public DoctorProfile createDoctorProfile(DoctorProfileRequest request) {

        // Tìm user theo userId
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUserId()));

        // Tạo mới DoctorProfile
        DoctorProfile doctorProfile = new DoctorProfile();
        doctorProfile.setUser(user);
        doctorProfile.setSpecialty(request.getSpecialty());
        doctorProfile.setDescription(request.getDescription());
        doctorProfile.setWorkSchedule(request.getWorkSchedule());

        return doctorProfileRepository.save(doctorProfile);
    }

    public List<DoctorProfile> getAllDoctors() {
        return doctorProfileRepository.findAll();
    }
}
