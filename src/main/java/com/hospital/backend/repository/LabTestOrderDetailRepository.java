package com.hospital.backend.repository;

import com.hospital.backend.entity.LabTestOrder;
import com.hospital.backend.entity.LabTestOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LabTestOrderDetailRepository extends JpaRepository<LabTestOrderDetail, UUID> {
    List<LabTestOrderDetail> findByOrder(LabTestOrder order);
}
