package com.enigma.proplybackend.repository;

import com.enigma.proplybackend.model.entity.ProcurementDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcurementDetailRepository extends JpaRepository<ProcurementDetail, String> {
}
