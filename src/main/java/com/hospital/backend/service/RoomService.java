package com.hospital.backend.service;

import com.hospital.backend.dto.request.room.RoomRequest;
import com.hospital.backend.entity.Department;
import com.hospital.backend.entity.Room;
import com.hospital.backend.repository.DepartmentRepository;
import com.hospital.backend.repository.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final DepartmentRepository departmentRepository;

    public RoomService(RoomRepository roomRepository, DepartmentRepository departmentRepository) {
        this.roomRepository = roomRepository;
        this.departmentRepository = departmentRepository;
    }

    public Room createRoom(RoomRequest request) {
        Room room = mapToEntity(request, new Room());
        return roomRepository.save(room);
    }

    public Room updateRoom(UUID id, RoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Room not found"));
        mapToEntity(request, room);
        return roomRepository.save(room);
    }

    public void deleteRoom(UUID id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Room not found"));
        room.setIsDeleted(false); // Soft delete
        roomRepository.save(room);
    }

    public Optional<Room> getRoomById(UUID id) {
        return roomRepository.findById(id).filter(Room::isActive);
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll()
                .stream()
                .filter(Room::isActive)
                .toList();
    }

    private Room mapToEntity(RoomRequest request, Room room) {
        room.setRoomNo(request.getRoomNo());
        room.setRoomType(request.getRoomType());
        room.setFloor(request.getFloor());
        room.setCapacity(request.getCapacity());
        room.setStatus(request.getStatus());
        room.setDescription(request.getDescription());
        room.setIsDeleted(request.isActive());

        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new EntityNotFoundException("Department not found"));
            room.setDepartment(department);
        } else {
            room.setDepartment(null);
        }

        return room;
    }
}
