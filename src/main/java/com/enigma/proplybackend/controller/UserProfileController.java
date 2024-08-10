package com.enigma.proplybackend.controller;

import com.enigma.proplybackend.constant.AppPath;
import com.enigma.proplybackend.model.request.UserProfileRequest;
import com.enigma.proplybackend.model.response.CommonResponse;
import com.enigma.proplybackend.model.response.UserProfileResponse;
import com.enigma.proplybackend.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AppPath.USER_PROFILES)
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
public class UserProfileController {
    private final UserProfileService userProfileService;

    @PostMapping
    public ResponseEntity<?> updateProfileImage(@Valid @ModelAttribute UserProfileRequest userProfileRequest) {
        UserProfileResponse userProfileResponse = userProfileService.uploadImage(userProfileRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<UserProfileResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Profile image updated successfully")
                        .data(userProfileResponse)
                        .build());
    }
}
