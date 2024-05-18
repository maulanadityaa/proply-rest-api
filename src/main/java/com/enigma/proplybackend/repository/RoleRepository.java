package com.enigma.proplybackend.repository;

import com.enigma.proplybackend.constant.ERole;
import com.enigma.proplybackend.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByName(ERole name);
}
