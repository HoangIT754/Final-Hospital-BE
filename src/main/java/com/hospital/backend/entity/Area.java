package com.hospital.backend.entity;

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
 * Master data cho các khu vực, tòa nhà trong bệnh viện.
 * Ví dụ: Main Building, 1, 2, 3...
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "cfg_area")
public class Area extends AuditModel {
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

    @OneToMany(mappedBy = "area", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "area-floor")
    List<Floor> floors = new ArrayList<>();
}
