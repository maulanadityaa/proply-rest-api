package com.enigma.proplybackend.repository;

import com.enigma.proplybackend.model.entity.ProcurementCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcurementCategoryRepository extends JpaRepository<ProcurementCategory, String> {
}
