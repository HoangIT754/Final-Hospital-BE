package com.hospital.backend.service;

import com.hospital.backend.dto.request.appointment.AppointmentRequest;
import com.hospital.backend.dto.request.appointment.AppointmentSearchRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.dto.response.BaseResponseList;
import com.hospital.backend.dto.response.appointment.AppointmentResponse;
import com.hospital.backend.entity.Appointment;
import com.hospital.backend.entity.PatientProfile;
import com.hospital.backend.entity.Room;
import com.hospital.backend.entity.StaffProfile;
import com.hospital.backend.exception.BadRequestException;
import com.hospital.backend.exception.NotFoundException;
import com.hospital.backend.repository.AppointmentRepository;
import com.hospital.backend.repository.PatientProfileRepository;
import com.hospital.backend.repository.RoomRepository;
import com.hospital.backend.repository.StaffProfileRepository;
import com.hospital.backend.utils.DateUtils;
import com.hospital.backend.utils.ResponseUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

    private static final String FAILED = "failed";
    private static final String SYSTEM_ERROR = "Error systems";
    private static final String OPERATION_FAILED = "Operation failed";

    private final AppointmentRepository appointmentRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final StaffProfileRepository staffProfileRepository;
    private final RoomRepository roomRepository;

    /**
     * Create Appointment
     */
    @Transactional
    public BaseResponse createAppointment(AppointmentRequest request) {
        long beginTime = System.currentTimeMillis();
        try {
            Appointment appointment = new Appointment();
            mapToEntity(request, appointment);

            Appointment saved = appointmentRepository.save(appointment);
            log.info("End create Appointment in {} ms", System.currentTimeMillis() - beginTime);

            AppointmentResponse response = mapToResponse(saved);
            return ResponseUtils.buildSuccessRes(response, "Created Appointment Successfully");

        } catch (BadRequestException | NotFoundException e) {
            log.error("Validation error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("System error while creating appointment", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1,
                    OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }

    /**
     * Update Appointment
     */
    @Transactional
    public BaseResponse updateAppointment(AppointmentRequest request) {
        long beginTime = System.currentTimeMillis();
        try {
            Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                    .orElseThrow(() -> new NotFoundException("Appointment not found"));

            mapToEntity(request, appointment);
            Appointment updated = appointmentRepository.save(appointment);

            log.info("End update Appointment in {} ms", System.currentTimeMillis() - beginTime);

            AppointmentResponse response = mapToResponse(updated);
            return ResponseUtils.buildSuccessRes(response, "Updated Appointment Successfully");

        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("System error while updating appointment", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1,
                    OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }

    /**
     * Soft Delete Appointment
     */
    @Transactional
    public BaseResponse deleteAppointment(UUID id) {
        long beginTime = System.currentTimeMillis();
        try {
            Appointment appointment = appointmentRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Appointment not found"));

            appointment.setIsDeleted(true);
            appointmentRepository.save(appointment);

            log.info("End delete Appointment in {} ms", System.currentTimeMillis() - beginTime);
            return ResponseUtils.buildSuccessRes(null, "Deleted Appointment Successfully");

        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("System error while deleting appointment", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1,
                    OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }

    /**
     * Get All Appointments (return DTO)
     */
    public BaseResponse getAllAppointments() {
        log.info("Start fetching all appointments");
        long beginTime = System.currentTimeMillis();

        try {
            List<Appointment> appointments = appointmentRepository.findAll();

            List<AppointmentResponse> responses = appointments.stream()
                    .map(this::mapToResponse)
                    .toList();

            log.info("Fetched {} appointments in {} ms",
                    responses.size(), System.currentTimeMillis() - beginTime);

            return ResponseUtils.buildSuccessRes(
                    new BaseResponseList(responses, responses.size()),
                    "Fetched Appointments Successfully"
            );

        } catch (Exception e) {
            log.error("System error while fetching appointments", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1,
                    OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }

    public BaseResponse searchAppointments(AppointmentSearchRequest request) {
        try {
            if (request.getStartDate() == null &&
                    request.getEndDate() == null &&
                    request.getPatientId() == null &&
                    request.getDoctorId() == null &&
                    request.getStatus() == null) {

                return getAllAppointments();
            }

            LocalDateTime startDate = request.getStartDate() != null
                    ? DateUtils.parseLocalDateTime(request.getStartDate())
                    : null;

            LocalDateTime endDate = request.getEndDate() != null
                    ? DateUtils.parseLocalDateTime(request.getEndDate())
                    : null;

            UUID patientId = request.getPatientId() != null
                    ? UUID.fromString(request.getPatientId())
                    : null;

            UUID doctorId = request.getDoctorId() != null
                    ? UUID.fromString(request.getDoctorId())
                    : null;

            Appointment.AppointmentStatus status = request.getStatus() != null
                    ? Appointment.AppointmentStatus.valueOf(request.getStatus())
                    : null;

            List<Appointment> results = appointmentRepository.searchAppointments(
                    startDate, endDate, patientId, doctorId, status
            );

            List<AppointmentResponse> dtoList = results.stream()
                    .map(this::mapToResponse)
                    .toList();

            return ResponseUtils.buildSuccessRes(
                    new BaseResponseList(dtoList, dtoList.size()),
                    "Search Appointments Successfully"
            );

        } catch (Exception e) {
            log.error("Error searching appointments", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1,
                    OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT),
                    null
            );
        }
    }

    /**
     * Get Appointment By ID
     */
    public BaseResponse getAppointmentById(AppointmentRequest request) {
        long beginTime = System.currentTimeMillis();
        log.info("Start fetching appointment with id: {}", request.getAppointmentId());

        try {
            if (request.getAppointmentId() == null) {
                throw new BadRequestException("Appointment ID is required");
            }

            Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                    .orElseThrow(() -> new NotFoundException("Appointment not found"));

            AppointmentResponse response = mapToResponse(appointment);

            log.info("Fetched appointment {} in {} ms",
                    request.getAppointmentId(), System.currentTimeMillis() - beginTime);

            return ResponseUtils.buildSuccessRes(response, "Fetched Appointment Successfully");

        } catch (NotFoundException | BadRequestException e) {
            log.error("Validation error: {}", e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("System error while fetching appointment by id", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1,
                    OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT),
                    null
            );
        }
    }

    /**
     * Update Appointment By ID
     */
    @Transactional
    public BaseResponse updateAppointmentById(AppointmentRequest request) {
        long beginTime = System.currentTimeMillis();
        log.info("Start updating appointment with id: {}", request.getAppointmentId());

        try {
            if (request.getAppointmentId() == null) {
                throw new BadRequestException("Appointment ID is required");
            }

            Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                    .orElseThrow(() -> new NotFoundException("Appointment not found"));

            mapToEntity(request, appointment);

            Appointment updated = appointmentRepository.save(appointment);

            log.info("End update Appointment {} in {} ms",
                    request.getAppointmentId(), System.currentTimeMillis() - beginTime);

            AppointmentResponse response = mapToResponse(updated);
            return ResponseUtils.buildSuccessRes(response, "Updated Appointment Successfully");

        } catch (NotFoundException | BadRequestException e) {
            log.error("Validation error: {}", e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("System error while updating appointment by id", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1,
                    OPERATION_FAILED,
                    DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT),
                    null
            );
        }
    }


    /**
     * Map AppointmentRequest → Appointment Entity
     */
    private void mapToEntity(AppointmentRequest request, Appointment appointment) {
        if (request.getPatientId() == null)
            throw new BadRequestException("Patient ID is required");
        if (request.getStaffId() == null)
            throw new BadRequestException("Staff ID is required");

        PatientProfile patient = patientProfileRepository.findById(request.getPatientId())
                .orElseThrow(() -> new NotFoundException("Patient not found"));
        StaffProfile staff = staffProfileRepository.findById(request.getStaffId())
                .orElseThrow(() -> new NotFoundException("Staff not found"));

        appointment.setPatient(patient);
        appointment.setStaff(staff);
        appointment.setAppointmentStartTime(request.getAppointmentStartTime());
        appointment.setAppointmentEndTime(request.getAppointmentEndTime());
        appointment.setReason(request.getReason());
        appointment.setIsDeleted(false);

        if (request.getRoom() != null) {
            Room room = roomRepository.findById(request.getRoom())
                    .orElseThrow(() -> new NotFoundException("Room not found"));
            appointment.setRoom(room);
        } else {
            appointment.setRoom(null);
        }

        appointment.setStatus(
                request.getStatus() != null
                        ? request.getStatus()
                        : Appointment.AppointmentStatus.PENDING
        );
    }

    /**
     * Map Appointment Entity → AppointmentResponse DTO
     */
    private AppointmentResponse mapToResponse(Appointment a) {
        AppointmentResponse dto = new AppointmentResponse();
        dto.setId(a.getId());
        dto.setReason(a.getReason());
        dto.setStatus(a.getStatus() != null ? a.getStatus().name() : null);
        dto.setAppointmentStartTime(a.getAppointmentStartTime());
        dto.setAppointmentEndTime(a.getAppointmentEndTime());

        if (a.getPatient() != null) {
            dto.setPatientId(a.getPatient().getId());
            dto.setPatientName(a.getPatient().getFirstName() + " " + a.getPatient().getLastName());
        }

        if (a.getStaff() != null) {
            dto.setStaffId(a.getStaff().getId());
            dto.setDoctorName(a.getStaff().getFirstName() + " " + a.getStaff().getLastName());
        }

        if (a.getRoom() != null) {
            dto.setRoomId(a.getRoom().getId());
            dto.setRoomNo(a.getRoom().getRoomNo());
        }

        return dto;
    }
}
