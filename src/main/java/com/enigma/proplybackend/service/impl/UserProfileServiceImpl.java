package com.enigma.proplybackend.service.impl;

import com.enigma.proplybackend.model.entity.Division;
import com.enigma.proplybackend.model.entity.User;
import com.enigma.proplybackend.model.entity.UserProfile;
import com.enigma.proplybackend.model.exception.ApplicationException;
import com.enigma.proplybackend.model.request.UserProfileRequest;
import com.enigma.proplybackend.model.response.UserProfileResponse;
import com.enigma.proplybackend.model.response.UserResponse;
import com.enigma.proplybackend.repository.UserProfileRepository;
import com.enigma.proplybackend.service.CloudinaryService;
import com.enigma.proplybackend.service.UserProfileService;
import com.enigma.proplybackend.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
//@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {
    private final CloudinaryService cloudinaryService;
    private final UserProfileRepository userProfileRepository;
    private final UserService userService;

    public UserProfileServiceImpl(CloudinaryService cloudinaryService, UserProfileRepository userProfileRepository, @Lazy UserService userService) {
        this.cloudinaryService = cloudinaryService;
        this.userProfileRepository = userProfileRepository;
        this.userService = userService;
    }

    @Override
    public UserProfileResponse uploadImage(UserProfileRequest userProfileRequest) {
        UserResponse userResponse = userService.getUserById(userProfileRequest.getUserId());

        try {
            if (userProfileRequest.getImage().isEmpty() || userProfileRequest.getImage() == null)
                throw new ApplicationException("Profile image is empty", "Profile image must specified", HttpStatus.BAD_REQUEST);

            String imageName = userProfileRequest.getImage().getName() + "-" + Instant.now();

            UserProfile founduserProfile = userProfileRepository.findByUser_Id(userProfileRequest.getUserId()).orElse(null);
            UserProfile userProfile = new UserProfile();
            if (founduserProfile != null) {
                userProfile = UserProfile.builder()
                        .id(founduserProfile.getId())
                        .imageName(imageName)
                        .imageUrl(cloudinaryService.uploadImage(userProfileRequest.getImage(), "profile-images"))
                        .user(User.builder()
                                .id(userResponse.getUserId())
                                .fullName(userResponse.getFullName())
                                .gender(userResponse.getGender())
                                .birthDate(userResponse.getBirthDate())
                                .maritalStatus(userResponse.getMaritalStatus())
                                .division(Division.builder()
                                        .id(userResponse.getDivisionResponse().getDivisionId())
                                        .name(userResponse.getDivisionResponse().getName())
                                        .isActive(userResponse.getDivisionResponse().getIsActive())
                                        .build())
                                .isActive(true)
                                .build())
                        .build();
            } else {
                userProfile = UserProfile.builder()
                        .imageName(imageName)
                        .imageUrl(cloudinaryService.uploadImage(userProfileRequest.getImage(), "profile-images"))
                        .user(User.builder()
                                .id(userResponse.getUserId())
                                .fullName(userResponse.getFullName())
                                .gender(userResponse.getGender())
                                .birthDate(userResponse.getBirthDate())
                                .maritalStatus(userResponse.getMaritalStatus())
                                .division(Division.builder()
                                        .id(userResponse.getDivisionResponse().getDivisionId())
                                        .name(userResponse.getDivisionResponse().getName())
                                        .isActive(userResponse.getDivisionResponse().getIsActive())
                                        .build())
                                .isActive(true)
                                .build())
                        .build();
            }

            if (userProfile.getImageUrl().isEmpty())
                throw new ApplicationException("Upload Failed", "Failed to upload profile image", HttpStatus.CONFLICT);

            userProfileRepository.save(userProfile);

            return UserProfileResponse.builder()
                    .imageUrl(userProfile.getImageUrl())
//                    .userResponse(userResponse)
                    .build();
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public UserProfileResponse getByUserId(String userId) {
        UserProfile userProfile = userProfileRepository.findByUser_Id(userId).orElse(null);

        if (userProfile != null) {
            return UserProfileResponse.builder()
                    .imageUrl(userProfile.getImageUrl())
                    .build();
        }
        return null;
    }
}
