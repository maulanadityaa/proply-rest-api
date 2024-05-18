package com.enigma.proplybackend.service.impl;

import com.enigma.proplybackend.model.entity.AppUser;
import com.enigma.proplybackend.model.entity.UserCredential;
import com.enigma.proplybackend.model.response.UserCredentialResponse;
import com.enigma.proplybackend.model.response.UserResponse;
import com.enigma.proplybackend.repository.UserCredentialRepository;
import com.enigma.proplybackend.service.UserCredentialService;
import com.enigma.proplybackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
//@RequiredArgsConstructor
public class UserCredentialServiceImpl implements UserCredentialService {
    private final UserCredentialRepository userCredentialRepository;
    private final UserService userService;

    public UserCredentialServiceImpl(UserCredentialRepository userCredentialRepository, @Lazy UserService userService) {
        this.userCredentialRepository = userCredentialRepository;
        this.userService = userService;
    }

    @Override
    public AppUser loadUserByUserId(String userId) {
        UserCredential userCredential = userCredentialRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
        UserResponse userResponse = userService.getUserById(userCredential.getUser().getId());

        return AppUser.builder()
                .id(userCredential.getId())
                .email(userCredential.getEmail())
                .password(userCredential.getPassword())
                .fullName(userResponse.getFullName())
                .role(userCredential.getRole().getName())
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserCredential userCredential = userCredentialRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
        UserResponse userResponse = userService.getUserById(userCredential.getUser().getId());

        return AppUser.builder()
                .id(userCredential.getId())
                .email(userCredential.getEmail())
                .password(userCredential.getPassword())
                .fullName(userResponse.getFullName())
                .role(userCredential.getRole().getName())
                .build();
    }

    @Override
    public UserCredentialResponse getByEmail(String email) {
        UserCredential userCredential = userCredentialRepository.findByEmail(email).orElse(null);

        if (userCredential != null) {
            return UserCredentialResponse.builder()
                    .email(userCredential.getEmail())
                    .role(userCredential.getRole().getName())
                    .build();
        }
        return null;
    }

    @Override
    public UserCredentialResponse getByUserId(String userId) {
        UserCredential userCredential = userCredentialRepository.findByUser_Id(userId).orElse(null);

        if (userCredential != null) {
            return UserCredentialResponse.builder()
                    .email(userCredential.getEmail())
                    .role(userCredential.getRole().getName())
                    .build();
        }
        return null;
    }
}
