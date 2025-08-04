package com.hospital.backend.service;

import com.hospital.backend.dto.request.appointment.AppointmentRequest;
import com.hospital.backend.entity.Appointment;
import com.hospital.backend.entity.DoctorProfile;
import com.hospital.backend.entity.PatientProfile;
import com.hospital.backend.repository.AppointmentRepository;
import com.hospital.backend.repository.DoctorProfileRepository;
import com.hospital.backend.repository.PatientProfileRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final DoctorProfileRepository doctorProfileRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, PatientProfileRepository patientProfileRepository, DoctorProfileRepository doctorProfileRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientProfileRepository = patientProfileRepository;
        this.doctorProfileRepository = doctorProfileRepository;
    }

    public Appointment createAppointment(AppointmentRequest request) {

        // Lấy Patient, nếu không có thì ném lỗi
        PatientProfile patient = patientProfileRepository.findById(request.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with id: " + request.getPatientId()));

        // Lấy Doctor, nếu không có thì ném lỗi
        DoctorProfile doctor = doctorProfileRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with id: " + request.getDoctorId()));

        // Tạo appointment mới
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentStartTime(request.getAppointmentStartTime());
        appointment.setAppointmentEndTime(request.getAppointmentEndTime());
        appointment.setReason(request.getReason());
        appointment.setStatus(
                request.getStatus() != null
                        ? request.getStatus()
                        : Appointment.AppointmentStatus.PENDING
        );

        // Lưu DB
        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getAll() {
        List<Appointment> appointments = appointmentRepository.findAll();
        return appointments != null ? appointments : Collections.emptyList();
    }

    public Optional<Appointment> getById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return appointmentRepository.findById(id);
    }

    public Appointment update(UUID id, Appointment updatedAppointment) {
        Appointment existing = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        existing.setDoctor(updatedAppointment.getDoctor());
        existing.setPatient(updatedAppointment.getPatient());
        existing.setAppointmentStartTime(updatedAppointment.getAppointmentStartTime());
        existing.setAppointmentEndTime(updatedAppointment.getAppointmentEndTime());
        existing.setStatus(updatedAppointment.getStatus());
        existing.setReason(updatedAppointment.getReason());

        return appointmentRepository.save(existing);
    }

    public void delete(UUID id) {
        Appointment existing = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        existing.setIsDeleted(true); // nếu dùng soft delete
        appointmentRepository.save(existing);
    }
}
