package com.hospital.backend.service;

import com.hospital.backend.dto.request.room.RoomRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.dto.response.BaseResponseList;
import com.hospital.backend.entity.Room;
import com.hospital.backend.entity.Specialty;
import com.hospital.backend.exception.BadRequestException;
import com.hospital.backend.exception.NotFoundException;
import com.hospital.backend.repository.RoomRepository;
import com.hospital.backend.repository.SpecialtyRepository;
import com.hospital.backend.utils.DateUtils;
import com.hospital.backend.utils.ResponseUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private static final String FAILED = "failed";
    private static final String SUCCESS = "Success";
    private static final String SYSTEM_ERROR = "Error systems";
    private static final String OPERATION_FAILED = "Operation failed";

    private final RoomRepository roomRepository;
    private final SpecialtyRepository specialtyRepository;

    /**
     * Create Room
     */
    @Transactional
    public BaseResponse createRoom(RoomRequest request) {
        long beginTime = System.currentTimeMillis();
        try {
            Room room = new Room();

            // Map thông tin từ request
            mapToEntity(request, room);

            // Lưu vào DB
            Room savedRoom = roomRepository.save(room);
            log.info("End create Room in {} ms", System.currentTimeMillis() - beginTime);

            return ResponseUtils.buildSuccessRes(savedRoom, "Created Room Successfully");
        } catch (BadRequestException | NotFoundException e) {
            log.error("Validation error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("System error while creating room", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1,
                    OPERATION_FAILED, DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }

    /**
     * Update Room
     */
    @Transactional
    public BaseResponse updateRoom(RoomRequest request) {
        long beginTime = System.currentTimeMillis();
        try {
            Room room = roomRepository.findById(request.getId())
                    .orElseThrow(() -> new NotFoundException("Room not found"));

            // Map lại dữ liệu
            mapToEntity(request, room);

            Room updatedRoom = roomRepository.save(room);
            log.info("End update Room in {} ms", System.currentTimeMillis() - beginTime);

            return ResponseUtils.buildSuccessRes(updatedRoom, "Updated Room Successfully");
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("System error while updating room", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1,
                    OPERATION_FAILED, DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }

    /**
     * Delete Room (soft delete)
     */
    @Transactional
    public BaseResponse deleteRoom(RoomRequest request) {
        long beginTime = System.currentTimeMillis();
        try {
            Room room = roomRepository.findById(request.getId())
                    .orElseThrow(() -> new NotFoundException("Room not found"));

            room.setIsDeleted(true);
            roomRepository.save(room);

            log.info("End delete Room in {} ms", System.currentTimeMillis() - beginTime);
            return ResponseUtils.buildSuccessRes(null, "Deleted Room Successfully");
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("System error while deleting room", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1,
                    OPERATION_FAILED, DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }

    /**
     * Get All Active Rooms
     */
    public BaseResponse getAllRooms() {
        log.info("Started fetching all rooms");
        long beginTime = System.currentTimeMillis();
        try {
            List<Room> rooms = roomRepository.findAll()
                    .stream()
                    .filter(room -> !Boolean.TRUE.equals(room.getIsDeleted()))
                    .toList();

            log.info("End fetching rooms in {} ms", System.currentTimeMillis() - beginTime);
            return ResponseUtils.buildSuccessRes(
                    new BaseResponseList(rooms, rooms.size()),
                    "Fetched Rooms Successfully"
            );
        } catch (Exception e) {
            log.error("System error while fetching rooms", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1,
                    OPERATION_FAILED, DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }

    /**
     * Get Room By ID
     */
    public BaseResponse getRoomById(RoomRequest request) {
        try {
            Room room = roomRepository.findById(request.getId())
                    .orElseThrow(() -> new NotFoundException("Room not found"));
            return ResponseUtils.buildSuccessRes(room, "Fetched Room Successfully");
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("System error while fetching room by id", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1,
                    OPERATION_FAILED, DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }

    /**
     * Mapping data từ request sang entity
     */
    private void mapToEntity(RoomRequest request, Room room) {
        room.setRoomNo(request.getRoomNo());
        room.setRoomType(request.getRoomType());
        room.setFloor(request.getFloor());
        room.setCapacity(request.getCapacity());
        room.setStatus(request.getStatus());
        room.setDescription(request.getDescription());
        room.setIsDeleted(false); // Luôn mặc định false khi tạo mới hoặc cập nhật

        if (request.getSpecialtyId() != null) {
            Specialty specialty = specialtyRepository.findById(request.getSpecialtyId())
                    .orElseThrow(() -> new NotFoundException("Specialty not found"));
            room.setSpecialty(specialty);
        } else {
            room.setSpecialty(null);
        }
    }
}
