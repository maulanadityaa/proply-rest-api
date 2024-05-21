package com.enigma.proplybackend.service.impl;

import com.enigma.proplybackend.constant.ERole;
import com.enigma.proplybackend.model.entity.AppUser;
import com.enigma.proplybackend.model.entity.Role;
import com.enigma.proplybackend.model.entity.User;
import com.enigma.proplybackend.model.entity.UserCredential;
import com.enigma.proplybackend.model.exception.ApplicationException;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

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

            return getRegisterResponse(authRequest, role);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Admin already exist!");
        }
    }

    @Override
    public RegisterResponse registerEmployee(AuthRequest authRequest, String authorization) {
        try {
            Role role = roleService.getOrSave(ERole.ROLE_EMPLOYEE);

            Map<String, String> userInfo = jwtUtil.getUserInfoByToken(authorization.substring(7));

            String userId = userInfo.get("userId");
            UserCredential userCredential = userCredentialRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
            UserResponse userResponse = userService.getUserById(userCredential.getUser().getId());

            if (authRequest.getDivisionId().equals(userResponse.getDivisionResponse().getDivisionId())) {
                return getRegisterResponse(authRequest, role);
            }

            throw new ApplicationException("Cannot register employee", "Only admin or manager with same division=" + userResponse.getDivisionResponse().getName() + " can add this employee to the correspondent division", HttpStatus.BAD_REQUEST);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Employee already exist!");
        }
    }

    @Override
    public RegisterResponse registerManager(AuthRequest authRequest) {
        try {
            Role role = roleService.getOrSave(ERole.ROLE_MANAGER);
//
            return getRegisterResponse(authRequest, role);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Manager already exist!");
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

    private RegisterResponse getRegisterResponse(AuthRequest authRequest, Role role) {
        UserRequest userRequest = UserRequest.builder()
                .fullName(authRequest.getFullName())
                .birthDate(authRequest.getBirthDate())
                .email(authRequest.getEmail())
                .gender(authRequest.getGender())
                .maritalStatus(authRequest.getMaritalStatus())
                .divisionId(authRequest.getDivisionId())
                .build();
        User user = userService.addUser(userRequest);

        UserCredential userCredential = UserCredential.builder()
                .email(authRequest.getEmail())
                .password(passwordEncoder.encode(authRequest.getPassword()))
                .role(role)
                .user(user)
                .build();
        userCredentialRepository.save(userCredential);


        return RegisterResponse.builder()
                .email(userCredential.getEmail())
                .role(role.getName().name())
                .build();
    }
}
