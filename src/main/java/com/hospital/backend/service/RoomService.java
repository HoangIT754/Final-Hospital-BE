package com.hospital.backend.service;

import com.hospital.backend.dto.request.room.RoomRequest;
import com.hospital.backend.dto.request.room.SearchRoomRequest;
import com.hospital.backend.dto.response.BaseResponse;
import com.hospital.backend.dto.response.BaseResponseList;
import com.hospital.backend.dto.response.room.RoomResponse;
import com.hospital.backend.dto.response.specialty.SpecialtyResponse;
import com.hospital.backend.entity.Area;
import com.hospital.backend.entity.Room;
import com.hospital.backend.entity.Specialty;
import com.hospital.backend.exception.BadRequestException;
import com.hospital.backend.exception.NotFoundException;
import com.hospital.backend.repository.AreaRepository;
import com.hospital.backend.repository.FloorRepository;
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
    private final FloorRepository floorRepository;
    private final AreaRepository areaRepository;

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

            mapToEntity(request, room);

            Room updatedRoom = roomRepository.save(room);
            RoomResponse dto = mapToRoomResponse(updatedRoom);

            log.info("End update Room in {} ms", System.currentTimeMillis() - beginTime);

            return ResponseUtils.buildSuccessRes(dto, "Updated Room Successfully");
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
     * Search Room
     */
    @Transactional
    public BaseResponse searchRooms(SearchRoomRequest request) {
        log.info("Start searching rooms with filters: {}", request);
        long beginTime = System.currentTimeMillis();
        try {
            String roomNo = normalizeString(request.getRoomNo());
            List<Room.RoomType> roomTypes = normalizeList(request.getRoomType());
            List<UUID> areaIds = normalizeList(request.getArea());
            List<Room.RoomStatus> statuses = normalizeList(request.getStatus());
            List<UUID> floorIds = normalizeList(request.getFloor());
            List<UUID> specialtyIds = normalizeList(request.getSpecialtyId());
            Boolean isActive = request.getIsActive();

            List<Room> rooms = roomRepository.searchRooms(
                    roomNo,
                    roomTypes,
                    areaIds,
                    statuses,
                    floorIds,
                    specialtyIds,
                    isActive
            );

            List<RoomResponse> responses = rooms.stream()
                    .map(this::mapToRoomResponse)
                    .toList();

            log.info("Found {} rooms in {} ms", rooms.size(), System.currentTimeMillis() - beginTime);
            return ResponseUtils.buildSuccessRes(
                    new BaseResponseList(responses, responses.size()),
                    "Search Rooms Successfully"
            );
        } catch (Exception e) {
            log.error("System error while searching rooms", e);
            return new BaseResponse(
                    500, null, SYSTEM_ERROR, FAILED, 1,
                    OPERATION_FAILED, DateUtils.formatDate(new Date(), DateUtils.CUSTOM_FORMAT), null
            );
        }
    }

    private String normalizeString(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        return value.trim();
    }

    private <T> List<T> normalizeList(List<T> list) {
        return (list == null || list.isEmpty()) ? null : list;
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
        List<Room> rooms = roomRepository.findAllWithFloorAndArea();

        List<RoomResponse> responses = rooms.stream()
                .map(this::mapToRoomResponse)
                .toList();

        return ResponseUtils.buildSuccessRes(
                new BaseResponseList(responses, responses.size()),
                "Fetched all rooms successfully"
        );
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
     * Mapping data từ request sang entity Room
     * Kiểm tra Specialty và Floor trước khi set vào.
     */
    private void mapToEntity(RoomRequest request, Room room) {
        room.setRoomNo(request.getRoomNo());
        room.setRoomType(request.getRoomType());
        room.setCapacity(request.getCapacity());
        room.setStatus(request.getStatus());
        room.setDescription(request.getDescription());
        room.setIsDeleted(false);
        room.setActive(request.getIsActive() != null ? request.getIsActive() : true);

        if (request.getSpecialtyId() != null) {
            Specialty specialty = specialtyRepository.findById(request.getSpecialtyId())
                    .orElseThrow(() -> new NotFoundException("Specialty not found"));
            room.setSpecialty(specialty);
        } else {
            room.setSpecialty(null);
        }

        if (request.getFloor() != null) {
            boolean exists = floorRepository.existsById(request.getFloor());
            if (!exists) {
                throw new NotFoundException("Floor not found");
            }
            room.setFloor(floorRepository.getReferenceById(request.getFloor()));
        } else {
            room.setFloor(null);
        }

        if (request.getAreaId() != null) {
            Area area = areaRepository.findById(request.getAreaId())
                    .orElseThrow(() -> new NotFoundException("Area not found"));
            room.setRoomArea(area);
        } else {
            throw new BadRequestException("Area is required");
        }
    }


    private RoomResponse mapToRoomResponse(Room r) {
        RoomResponse dto = new RoomResponse();
        dto.setId(r.getId());
        dto.setRoomNo(r.getRoomNo());
        dto.setRoomType(r.getRoomType());
        dto.setCapacity(r.getCapacity());
        dto.setStatus(r.getStatus());
        dto.setDescription(r.getDescription());
        dto.setActive(r.isActive());

        if (r.getSpecialty() != null) {
            SpecialtyResponse s = new SpecialtyResponse();
            s.setId(r.getSpecialty().getId());
            s.setName(r.getSpecialty().getName());
            s.setDescription(r.getSpecialty().getDescription());
            dto.setSpecialty(s);
        }

        if (r.getFloor() != null) {
            dto.setFloorId(r.getFloor().getId());
            dto.setFloorName(r.getFloor().getName());

            if (r.getFloor().getArea() != null) {
                dto.setAreaId(r.getFloor().getArea().getId());
                dto.setAreaName(r.getFloor().getArea().getName());
            }
        }

        return dto;
    }
}
