package com.enigma.proplybackend.repository;

import com.enigma.proplybackend.model.entity.Approval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, String> {
}
