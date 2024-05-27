package com.enigma.proplybackend.service.impl;

import com.enigma.proplybackend.constant.ERole;
import com.enigma.proplybackend.model.entity.AppUser;
import com.enigma.proplybackend.model.entity.Role;
import com.enigma.proplybackend.model.entity.User;
import com.enigma.proplybackend.model.entity.UserCredential;
import com.enigma.proplybackend.model.exception.ApplicationException;
import com.enigma.proplybackend.model.request.AuthRequest;
import com.enigma.proplybackend.model.request.MailRequest;
import com.enigma.proplybackend.model.request.UserRequest;
import com.enigma.proplybackend.model.response.DivisionResponse;
import com.enigma.proplybackend.model.response.LoginResponse;
import com.enigma.proplybackend.model.response.MailResponse;
import com.enigma.proplybackend.model.response.RegisterResponse;
import com.enigma.proplybackend.model.response.UserResponse;
import com.enigma.proplybackend.repository.UserCredentialRepository;
import com.enigma.proplybackend.security.JwtUtil;
import com.enigma.proplybackend.service.AuthService;
import com.enigma.proplybackend.service.DivisionService;
import com.enigma.proplybackend.service.MailSenderService;
import com.enigma.proplybackend.service.RoleService;
import com.enigma.proplybackend.service.UserCredentialService;
import com.enigma.proplybackend.service.UserService;
import com.enigma.proplybackend.util.RandomStringGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserCredentialRepository userCredentialRepository;
    private final UserCredentialService userCredentialService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final DivisionService divisionService;
    private final JwtUtil jwtUtil;
    private final MailSenderService mailSenderService;

    @Transactional(rollbackOn = Exception.class)
    @Override
    public RegisterResponse registerAdmin(AuthRequest authRequest) {
        try {
            Role role = roleService.getOrSave(ERole.ROLE_ADMIN);

            return getRegisterResponse(authRequest, role);
        } catch (DataIntegrityViolationException e) {
            throw new ApplicationException("Data request conflict", "Admin already exists", HttpStatus.CONFLICT);
        }
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public RegisterResponse registerEmployee(AuthRequest authRequest, String authorization) {
        try {
            Role role = roleService.getOrSave(ERole.ROLE_EMPLOYEE);

            Map<String, String> userInfo = jwtUtil.getUserInfoByToken(authorization.substring(7));

            String userId = userInfo.get("userId");
            UserCredential userCredential = userCredentialRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
            UserResponse userResponse = userService.getUserById(userCredential.getUser().getId());
            DivisionResponse divisionResponse = divisionService.getDivisionById(authRequest.getDivisionId());

            if (authRequest.getDivisionId().equals(userResponse.getDivisionResponse().getDivisionId())) {
                return getRegisterResponse(authRequest, role);
            }

            throw new ApplicationException("Cannot register employee", "Only admin or manager with same division=" + divisionResponse.getName() + " can add this employee to the correspondent division", HttpStatus.BAD_REQUEST);
        } catch (DataIntegrityViolationException e) {
            throw new ApplicationException("Data request conflict", "Employee already exists", HttpStatus.CONFLICT);
        }
    }

    @Transactional(rollbackOn = Exception.class)
    @Override
    public RegisterResponse registerManager(AuthRequest authRequest) {
        try {
            Role role = roleService.getOrSave(ERole.ROLE_MANAGER);
//
            return getRegisterResponse(authRequest, role);
        } catch (DataIntegrityViolationException e) {
            throw new ApplicationException("Data request conflict", "Manager already exists", HttpStatus.CONFLICT);
        }
    }

    @Override
    public LoginResponse login(AuthRequest authRequest) {
        try {
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
        } catch (BadCredentialsException e) {
            throw new ApplicationException("Invalid credentials", "Email or password incorrect", HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public MailResponse resetPassword(String email) {
        Context context = new Context();

        UserCredential userCredential = userCredentialService.getByEmail(email);

        if (userCredential != null) {
            String randomPassword = RandomStringGenerator.generateRandomString();
            context.setVariable("newpassword", randomPassword);
            Boolean isSend = mailSenderService.sendEmailWithTemplate(MailRequest.builder()
                    .to(email)
                    .subject("Reset Password")
                    .body("Your password has been reset.\nUse password below to login into your account\n" + randomPassword)
                    .build(), context);
//            if (isSend) {
//                userCredential.setPassword(passwordEncoder.encode(randomPassword));
//                userCredentialRepository.save(userCredential);
//
//                return MailResponse.builder()
//                        .email(email)
//                        .build();
//            }
            return MailResponse.builder()
                    .email(email)
                    .build();
        }

        throw new ApplicationException("Email not sent", "Email not sent", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private RegisterResponse getRegisterResponse(AuthRequest authRequest, Role role) {
//        UserCredential userCredential = userCredentialService.getByEmail(authRequest.getEmail());
//        if (userCredential != null) {
//            throw new ApplicationException("Data request conflict", "User already exists", HttpStatus.CONFLICT);
//        }

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
        userCredentialRepository.saveAndFlush(userCredential);


        return RegisterResponse.builder()
                .email(userCredential.getEmail())
                .role(role.getName().name())
                .build();
    }
}
