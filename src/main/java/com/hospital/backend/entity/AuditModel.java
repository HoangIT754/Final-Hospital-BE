package com.hospital.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditModel {

    @CreatedBy
    @Column(name = "created_by")
    String createBy;

    @CreatedDate
    @Column(name = "created_date")
    LocalDateTime createDate;

    @LastModifiedBy
    @Column(name = "modified_by")
    String modifyBy;

    @LastModifiedDate
    @Column(name = "modified_date")
    LocalDateTime modifyDate;

    @Column(name="is_deleted", nullable=false)
    private Boolean isDeleted = false;
}