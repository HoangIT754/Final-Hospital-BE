package com.hospital.backend.controller;

import com.hospital.backend.constant.APIConstants;
import com.hospital.backend.dto.request.UserRequest;
import com.hospital.backend.dto.request.appointment.AppointmentRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.entity.Appointment;
import com.hospital.backend.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping(value = APIConstants.API_APPOINTMENT_CREATE)
    public ResponseEntity<BaseResponse> createAppointment(@RequestBody AppointmentRequest request){
        long beginTime = System.currentTimeMillis();
        BaseResponse response = appointmentService.createAppointment(request);
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping(value = APIConstants.API_APPOINTMENT_GET_ALL)
    public ResponseEntity<BaseResponse> getAllAppointment(){
        long beginTime = System.currentTimeMillis();
        BaseResponse response = appointmentService.getAllAppointments();
        response.setTook(System.currentTimeMillis() - beginTime);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

//    // POST: Get all appointments
//    @PostMapping(value = APIConstants.API_APPOINTMENT_GET_ALL)
//    public ResponseEntity<BaseResponse> getAllAppointments() {
//        long beginTime = System.currentTimeMillis();
//        BaseResponse response = appointmentService.getAllAppointments();
//        response.setTook(System.currentTimeMillis() - beginTime);
//        return ResponseEntity.status(response.getStatusCode()).body(response);
//    }
//
//
//    // POST: Get appointment by id
//    @PostMapping(value = APIConstants.API_APPOINTMENT_GET_DETAILS)
//    public ResponseEntity<Appointment> getById(@RequestParam UUID id) {
//        return appointmentService.getById(id)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    // POST: Update appointment by id
//    @PostMapping(value = APIConstants.API_APPOINTMENT_UPDATE)
//    public ResponseEntity<Appointment> update(@RequestParam UUID id, @RequestBody Appointment appointment) {
//        return ResponseEntity.ok(appointmentService.update(id, appointment));
//    }
//
//    // POST: Delete appointment (soft delete)
//    @PostMapping(value = APIConstants.API_APPOINTMENT_DELETE)
//    public ResponseEntity<String> delete(@RequestParam UUID id) {
//        appointmentService.delete(id);
//        return ResponseEntity.ok("Deleted successfully");
//    }
}
