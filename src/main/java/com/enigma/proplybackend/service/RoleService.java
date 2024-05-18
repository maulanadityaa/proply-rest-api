package com.enigma.proplybackend.service;

import com.enigma.proplybackend.constant.ERole;
import com.enigma.proplybackend.model.entity.Role;

public interface RoleService {
    Role getOrSave(ERole role);
}
