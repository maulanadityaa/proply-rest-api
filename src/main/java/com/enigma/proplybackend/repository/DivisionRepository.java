package com.enigma.proplybackend.repository;

import com.enigma.proplybackend.model.entity.Division;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DivisionRepository extends JpaRepository<Division, String> {
    List<Division> findAllByIsActiveIsTrue();
}
