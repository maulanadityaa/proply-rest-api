package com.enigma.proplybackend.service.impl;

import com.enigma.proplybackend.constant.EGender;
import com.enigma.proplybackend.constant.ERole;
import com.enigma.proplybackend.model.entity.AppUser;
import com.enigma.proplybackend.model.entity.Division;
import com.enigma.proplybackend.model.entity.Role;
import com.enigma.proplybackend.model.entity.User;
import com.enigma.proplybackend.model.entity.UserCredential;
import com.enigma.proplybackend.model.request.AuthRequest;
import com.enigma.proplybackend.model.request.UserRequest;
import com.enigma.proplybackend.model.response.LoginResponse;
import com.enigma.proplybackend.model.response.RegisterResponse;
import com.enigma.proplybackend.model.response.UserResponse;
import com.enigma.proplybackend.repository.UserCredentialRepository;
import com.enigma.proplybackend.security.JwtUtil;
import com.enigma.proplybackend.service.AuthService;
import com.enigma.proplybackend.service.RoleService;
import com.enigma.proplybackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserCredentialRepository userCredentialRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public RegisterResponse registerAdmin(AuthRequest authRequest) {
        try {
            Role role = roleService.getOrSave(ERole.ROLE_ADMIN);

            UserRequest userRequest = UserRequest.builder()
                    .fullName(authRequest.getFullName())
                    .birthDate(authRequest.getBirthDate())
                    .email(authRequest.getEmail())
                    .gender(authRequest.getGender())
                    .maritalStatus(authRequest.getMaritalStatus())
                    .divisionId(authRequest.getDivisionId())
                    .build();
            UserResponse userResponse = userService.addUser(userRequest);

            UserCredential userCredential = UserCredential.builder()
                    .email(authRequest.getEmail())
                    .password(passwordEncoder.encode(authRequest.getPassword()))
                    .role(role)
                    .user(User.builder()
                            .id(userResponse.getUserId())
                            .fullName(userResponse.getFullName())
                            .birthDate(userResponse.getBirthDate())
                            .gender(userResponse.getGender())
                            .maritalStatus(userResponse.getMaritalStatus())
                            .division(Division.builder()
                                    .id(userResponse.getDivisionResponse().getDivisionId())
                                    .name(userResponse.getDivisionResponse().getName())
                                    .build())
                            .isActive(true)
                            .build())
                    .build();
            userCredentialRepository.save(userCredential);


            return RegisterResponse.builder()
                    .email(userCredential.getEmail())
                    .role(role.getName().name())
                    .build();
        } catch (DataIntegrityViolationException e) {
//            throw new DataIntegrityViolationException("Admin already exist!");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public RegisterResponse registerEmployee(AuthRequest authRequest) {
        try {
            Role role = roleService.getOrSave(ERole.ROLE_EMPLOYEE);

            UserRequest userRequest = UserRequest.builder()
                    .fullName(authRequest.getFullName())
                    .birthDate(authRequest.getBirthDate())
                    .email(authRequest.getEmail())
                    .gender(authRequest.getGender())
                    .maritalStatus(authRequest.getMaritalStatus())
                    .divisionId(authRequest.getDivisionId())
                    .build();
            UserResponse userResponse = userService.addUser(userRequest);

            UserCredential userCredential = UserCredential.builder()
                    .email(authRequest.getEmail())
                    .password(passwordEncoder.encode(authRequest.getPassword()))
                    .role(role)
                    .user(User.builder()
                            .id(userResponse.getUserId())
                            .fullName(userResponse.getFullName())
                            .birthDate(userResponse.getBirthDate())
                            .gender(userResponse.getGender())
                            .maritalStatus(userResponse.getMaritalStatus())
                            .division(Division.builder()
                                    .id(userResponse.getDivisionResponse().getDivisionId())
                                    .name(userResponse.getDivisionResponse().getName())
                                    .build())
                            .isActive(true)
                            .build())
                    .build();
            userCredentialRepository.save(userCredential);


            return RegisterResponse.builder()
                    .email(userCredential.getEmail())
                    .role(role.getName().name())
                    .build();
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Employee already exist!");
        }
    }

    @Override
    public LoginResponse login(AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authRequest.getEmail(),
                authRequest.getPassword()
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        AppUser appUser = (AppUser) authentication.getPrincipal();
        String token = jwtUtil.generateToken(appUser);

        return LoginResponse.builder()
                .token(token)
                .build();
    }
}
