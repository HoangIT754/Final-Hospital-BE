package com.hospital.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Master data cho các tầng lầu.
 * Ví dụ: G, B1, 1, 2, 3...
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "cfg_floor")
public class Floor extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @NotNull
    UUID id;

    @NotNull
    @Column(name = "code", unique = true, length = 50)
    String code;

    @NotNull
    @Column(name = "name", length = 100)
    String name;

    @Column(name = "description")
    String description;

    // Một tầng thuộc 1 khu vực
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id")
    @JsonIgnoreProperties({"floors", "hibernateLazyInitializer", "handler"})
    Area area;

    // Một tầng có nhiều phòng
    @OneToMany(mappedBy = "floor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonManagedReference
    @JsonIgnore
    List<Room> rooms = new ArrayList<>();
}
