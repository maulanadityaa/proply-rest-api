package com.enigma.proplybackend.service;

import com.enigma.proplybackend.model.entity.AppUser;
import com.enigma.proplybackend.model.response.UserCredentialResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserCredentialService extends UserDetailsService {
    AppUser loadUserByUserId(String userId);

    UserCredentialResponse getByUserId(String userId);

    UserCredentialResponse getByEmail(String email);
}
