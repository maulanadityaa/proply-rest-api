package com.enigma.proplybackend.controller;

import com.enigma.proplybackend.constant.AppPath;
import com.enigma.proplybackend.model.request.UserRequest;
import com.enigma.proplybackend.model.response.CommonResponse;
import com.enigma.proplybackend.model.response.UserResponse;
import com.enigma.proplybackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(AppPath.USERS)
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<UserResponse> userResponses = userService.getAllUsers();

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<List<UserResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Users retrieved successfully")
                        .data(userResponses)
                        .build());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(AppPath.ACTIVE_STATUS)
    public ResponseEntity<?> getAllActiveUsers() {
        List<UserResponse> userResponses = userService.getAllUserWhereActive();

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<List<UserResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Active users retrieved successfully")
                        .data(userResponses)
                        .build());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @GetMapping(AppPath.GET_BY_ID)
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        UserResponse userResponse = userService.getUserById(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<UserResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("User retrieved successfully")
                        .data(userResponse)
                        .build()
                );
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @GetMapping(AppPath.GET_BY_EMAIL)
    public ResponseEntity<?> getUserByEmail(@RequestBody UserRequest userRequest) {
        UserResponse userResponse = userService.getUserByEmail(userRequest.getEmail());

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<UserResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("User retrieved successfully")
                        .data(userResponse)
                        .build()
                );
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @PutMapping
    public ResponseEntity<?> updateUser(@ModelAttribute UserRequest userRequest) {
        UserResponse userResponse = userService.updateUser(userRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<UserResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("User updated successfully")
                        .data(userResponse)
                        .build()
                );
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(AppPath.DELETE_BY_ID)
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<UserResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("User deleted successfully")
                        .build()
                );
    }
}
