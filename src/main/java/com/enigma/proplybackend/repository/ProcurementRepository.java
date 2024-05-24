package com.enigma.proplybackend.repository;

import com.enigma.proplybackend.model.entity.Procurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProcurementRepository extends JpaRepository<Procurement, String>, JpaSpecificationExecutor<Procurement> {
    Optional<List<Procurement>> findAllByUser_Id(String userId);
}
