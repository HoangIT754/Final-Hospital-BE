package com.hospital.backend.service;

import com.hospital.backend.dto.request.appointment.AppointmentRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.dto.response.BaseResponseList;
import com.hospital.backend.entity.Appointment;
import com.hospital.backend.entity.StaffProfile;
import com.hospital.backend.entity.PatientProfile;
import com.hospital.backend.repository.AppointmentRepository;
import com.hospital.backend.repository.StaffProfileRepository;
import com.hospital.backend.repository.PatientProfileRepository;
import com.hospital.backend.utils.DateUtils;
import com.hospital.backend.utils.ResponseUtils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Service
@Transactional
@Slf4j
public class AppointmentService {

    private static final String FAILED = "failed";
    private static final String SUCCESS = "Success";
    private static final String SYSTEM_ERROR = "Error systems";
    private static final String OPERATION_FAILED = "Operation failed";

    private final AppointmentRepository appointmentRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final StaffProfileRepository staffProfileRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, PatientProfileRepository patientProfileRepository, StaffProfileRepository staffProfileRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientProfileRepository = patientProfileRepository;
        this.staffProfileRepository = staffProfileRepository;
    }

    public Appointment createAppointment(AppointmentRequest request) {

        // Lấy Patient, nếu không có thì ném lỗi
        PatientProfile patient = patientProfileRepository.findById(request.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with id: " + request.getPatientId()));

        // Lấy Doctor, nếu không có thì ném lỗi
        StaffProfile staff = staffProfileRepository.findById(request.getStaffId())
                .orElseThrow(() -> new IllegalArgumentException("Staff not found with id: " + request.getStaffId()));

        // Tạo appointment mới
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setStaff(staff);
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

    public BaseResponse getAllAppointments() {
        log.info("Started fetching all appointments");
        long beginTime = System.currentTimeMillis();

        try {
            List<Appointment> patients = appointmentRepository.findAll();
            log.info("End fetching appointments in {} ms", System.currentTimeMillis() - beginTime);

            return ResponseUtils.buildSuccessRes(
                    new BaseResponseList(patients, patients.size()) ,
                    "Fetched Appointments Successfully"
            );
        } catch (Exception e) {
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1, OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
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

        existing.setStaff(updatedAppointment.getStaff());
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
