package com.enigma.proplybackend.service.impl;

import com.enigma.proplybackend.model.entity.Division;
import com.enigma.proplybackend.model.entity.Role;
import com.enigma.proplybackend.model.entity.User;
import com.enigma.proplybackend.model.entity.UserCredential;
import com.enigma.proplybackend.model.exception.ApplicationException;
import com.enigma.proplybackend.model.request.UserProfileRequest;
import com.enigma.proplybackend.model.request.UserRequest;
import com.enigma.proplybackend.model.response.DivisionResponse;
import com.enigma.proplybackend.model.response.UserCredentialResponse;
import com.enigma.proplybackend.model.response.UserProfileResponse;
import com.enigma.proplybackend.model.response.UserResponse;
import com.enigma.proplybackend.repository.UserRepository;
import com.enigma.proplybackend.service.DivisionService;
import com.enigma.proplybackend.service.UserCredentialService;
import com.enigma.proplybackend.service.UserProfileService;
import com.enigma.proplybackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final DivisionService divisionService;
    private final UserCredentialService userCredentialService;
    private final UserProfileService userProfileService;

    @Override
    public User addUser(UserRequest userRequest) {
        DivisionResponse divisionResponse = divisionService.getDivisionById(userRequest.getDivisionId());

        if (!divisionResponse.getIsActive())
            throw new ApplicationException("Division is not active", divisionResponse.getName() + " is not active", HttpStatus.BAD_REQUEST);

        User user = toUser(userRequest, divisionResponse);
        userRepository.save(user);

        return user;
    }


    @Override
    public UserResponse updateUser(UserRequest userRequest) {
        User user = userRepository.findById(userRequest.getId()).orElseThrow(() -> new ApplicationException("User not found", "User with id=" + userRequest.getId() + " not found", HttpStatus.BAD_REQUEST));

        DivisionResponse divisionResponse = divisionService.getDivisionById(userRequest.getDivisionId());
        UserCredential userCredential = userCredentialService.getByEmail(userRequest.getEmail());
        UserProfileResponse userProfileResponse = userProfileService.getByUserId(user.getId());

        if (userRequest.getProfileImage() != null) {
            userProfileResponse = userProfileService.uploadImage(UserProfileRequest.builder()
                    .userId(user.getId())
                    .image(userRequest.getProfileImage())
                    .build());
        }

        User foundUser = toUser(userRequest, divisionResponse);
        foundUser.setId(userRequest.getId());
        userRepository.save(foundUser);

        return toUserResponse(user, userCredential, divisionResponse, userProfileResponse);
    }

    @Override
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApplicationException("User not found", "User with id=" + userId + " not found", HttpStatus.BAD_REQUEST));

        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    public UserResponse getUserById(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApplicationException("User not found", "User with id=" + userId + " not found", HttpStatus.BAD_REQUEST));
        UserCredentialResponse userCredentialResponse = userCredentialService.getByUserId(userId);
        UserProfileResponse userProfileResponse = userProfileService.getByUserId(userId);

        UserCredential userCredential = UserCredential.builder()
                .id(userCredentialResponse.getUserCredentialId())
                .email(userCredentialResponse.getEmail())
                .role(Role.builder()
                        .name(userCredentialResponse.getRole())
                        .build())
                .build();

        System.out.println(userCredential.getId());

        DivisionResponse divisionResponse = divisionService.getDivisionById(user.getDivision().getId());

        return toUserResponse(user, userCredential, divisionResponse, userProfileResponse);
    }

    @Override
    public UserResponse getUserByEmail(String userEmail) {
        UserCredential userCredential = userCredentialService.getByEmail(userEmail);
        User user = userRepository.findById(userCredential.getUser().getId()).orElseThrow(() -> new ApplicationException("User not found", "User with email=" + userEmail + " not found", HttpStatus.NOT_FOUND));
        DivisionResponse divisionResponse = divisionService.getDivisionById(user.getDivision().getId());
        UserProfileResponse userProfileResponse = userProfileService.getByUserId(user.getId());

        return toUserResponse(user, userCredential, divisionResponse, userProfileResponse);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> userList = userRepository.findAll();
        List<UserResponse> userResponseList = new ArrayList<>();

        if (userList.isEmpty()) throw new ApplicationException("No users were found", null, HttpStatus.NOT_FOUND);

        for (User user : userList) {
            UserCredentialResponse userCredentialResponse = userCredentialService.getByUserId(user.getId());
            UserCredential userCredential = UserCredential.builder()
                    .id(userCredentialResponse.getUserCredentialId())
                    .email(userCredentialResponse.getEmail())
                    .role(Role.builder()
                            .name(userCredentialResponse.getRole())
                            .build())
                    .build();
            DivisionResponse divisionResponse = divisionService.getDivisionById(user.getDivision().getId());
            UserProfileResponse userProfileResponse = userProfileService.getByUserId(user.getId());

            userResponseList.add(toUserResponse(user, userCredential, divisionResponse, userProfileResponse));
        }

        return userResponseList;
    }

    @Override
    public List<UserResponse> getAllUserWhereActive() {
        List<User> userList = userRepository.findAllByIsActiveIsTrue();
        List<UserResponse> userResponseList = new ArrayList<>();

        if (userList.isEmpty()) throw new ApplicationException("No users were found", null, HttpStatus.NOT_FOUND);

        for (User user : userList) {
            UserCredentialResponse userCredentialResponse = userCredentialService.getByUserId(user.getId());
            UserCredential userCredential = UserCredential.builder()
                    .id(userCredentialResponse.getUserCredentialId())
                    .email(userCredentialResponse.getEmail())
                    .role(Role.builder()
                            .name(userCredentialResponse.getRole())
                            .build())
                    .build();
            DivisionResponse divisionResponse = divisionService.getDivisionById(user.getDivision().getId());
            UserProfileResponse userProfileResponse = userProfileService.getByUserId(user.getId());

            userResponseList.add(toUserResponse(user, userCredential, divisionResponse, userProfileResponse));
        }

        return userResponseList;
    }

    private static UserResponse toUserResponse(User user, UserCredential userCredential, DivisionResponse divisionResponse, UserProfileResponse userProfileResponse) {
        return UserResponse.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .birthDate(user.getBirthDate())
                .gender(user.getGender())
                .maritalStatus(user.getMaritalStatus())
                .profileImageUrl(userProfileResponse != null ? userProfileResponse.getImageUrl() : null)
                .userCredentialResponse(UserCredentialResponse.builder()
                        .userCredentialId(userCredential.getId())
                        .email(userCredential.getEmail())
                        .role(userCredential.getRole().getName())
                        .build())
                .divisionResponse(divisionResponse)
                .isActive(user.getIsActive())
                .build();
    }

    private static User toUser(UserRequest userRequest, DivisionResponse divisionResponse) {
        return User.builder()
                .fullName(userRequest.getFullName())
                .gender(userRequest.getGender())
                .birthDate(userRequest.getBirthDate())
                .maritalStatus(userRequest.getMaritalStatus())
                .division(Division.builder()
                        .id(divisionResponse.getDivisionId())
                        .name(divisionResponse.getName())
                        .isActive(divisionResponse.getIsActive())
                        .build())
                .isActive(true)
                .build();
    }
}
