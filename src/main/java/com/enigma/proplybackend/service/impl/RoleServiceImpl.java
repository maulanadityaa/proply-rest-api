package com.enigma.proplybackend.service.impl;

import com.enigma.proplybackend.constant.ERole;
import com.enigma.proplybackend.model.entity.Role;
import com.enigma.proplybackend.repository.RoleRepository;
import com.enigma.proplybackend.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public Role getOrSave(ERole role) {
        Optional<Role> optionalRole = roleRepository.findByName(role);

        if (optionalRole.isPresent()) {
            return optionalRole.get();
        }

        Role currentRole = Role.builder()
                .name(role)
                .build();

        return roleRepository.save(currentRole);
    }
}
