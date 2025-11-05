package com.hospital.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "room")
public class Room extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    UUID id;

    // Số hiệu phòng (VD: P101, A3-02)
    @NotNull
    @Column(name = "room_no", unique = true)
    String roomNo;

    // Loại phòng: EXAMINATION, LAB, ICU, etc.
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "room_type")
    RoomType roomType;

    // Phòng thuộc khoa nào
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    Specialty specialty;

    // Tầng của phòng
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id")
    @JsonIgnoreProperties({"rooms", "area", "hibernateLazyInitializer", "handler"})
    Floor floor;

    // Sức chứa tối đa (cho phòng bệnh)
    @Column(name = "capacity")
    Integer capacity;

    // Trạng thái phòng: AVAILABLE, OCCUPIED, MAINTENANCE
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    RoomStatus status;

    // Ghi chú thêm
    @Column(name = "description")
    String description;

    // Đánh dấu phòng còn hoạt động
    @Column(name = "is_active")
    boolean isActive = true;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Appointment> appointments = new ArrayList<>();

    // Enum định nghĩa loại phòng
    public enum RoomType {
        EXAMINATION,    // Phòng khám
        LAB,            // Phòng xét nghiệm
        SURGERY,        // Phòng mổ
        ICU,            // Hồi sức tích cực
        PHARMACY,       // Quầy thuốc
        WARD,           // Phòng nội trú
        EMERGENCY,      // Cấp cứu
        WAITING_ROOM    // Phòng chờ
    }

    // Enum định nghĩa trạng thái phòng
    public enum RoomStatus {
        AVAILABLE,      // Sẵn sàng sử dụng
        OCCUPIED,       // Đang có người sử dụng
        MAINTENANCE,    // Đang bảo trì
        CLOSED          // Đóng vĩnh viễn
    }
}
