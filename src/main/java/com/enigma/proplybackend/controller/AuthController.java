package com.enigma.proplybackend.controller;

import com.enigma.proplybackend.constant.AppPath;
import com.enigma.proplybackend.model.request.AuthRequest;
import com.enigma.proplybackend.model.request.MailRequest;
import com.enigma.proplybackend.model.response.CommonResponse;
import com.enigma.proplybackend.model.response.LoginResponse;
import com.enigma.proplybackend.model.response.MailResponse;
import com.enigma.proplybackend.model.response.RegisterResponse;
import com.enigma.proplybackend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AppPath.AUTH)
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping(AppPath.REGISTER_ADMIN)
    public ResponseEntity<?> registerAdmin(@RequestBody AuthRequest authRequest) {
        RegisterResponse registerResponse = authService.registerAdmin(authRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.<RegisterResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Admin registered successfully")
                        .data(registerResponse)
                        .build()
                );
    }

    @PostMapping(AppPath.REGISTER_MANAGER)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> registerManager(@RequestBody AuthRequest authRequest) {
        RegisterResponse registerResponse = authService.registerManager(authRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.<RegisterResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Manager registered successfully")
                        .data(registerResponse)
                        .build()
                );
    }

    @PostMapping(AppPath.REGISTER_EMPLOYEE)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<?> registerEmployee(@RequestBody AuthRequest authRequest, @RequestHeader("Authorization") String authorization) {
        RegisterResponse registerResponse = authService.registerEmployee(authRequest, authorization);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.<RegisterResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Employee registered successfully")
                        .data(registerResponse)
                        .build()
                );
    }

    @PostMapping(AppPath.LOGIN)
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        LoginResponse loginResponse = authService.login(authRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<LoginResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Login successfully")
                        .data(loginResponse)
                        .build()
                );
    }

    @PostMapping(AppPath.RESET_PASSWORD)
    public ResponseEntity<?> resetPassword(@RequestBody MailRequest mailRequest) {
        MailResponse mailResponse = authService.resetPassword(mailRequest.getTo());

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<MailResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Account password has been reset successfully")
                        .data(mailResponse)
                        .build()
                );
    }
}
