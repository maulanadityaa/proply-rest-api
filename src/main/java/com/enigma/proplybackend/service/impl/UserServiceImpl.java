package com.enigma.proplybackend.service.impl;

import com.enigma.proplybackend.model.entity.Division;
import com.enigma.proplybackend.model.entity.User;
import com.enigma.proplybackend.model.exception.ApplicationException;
import com.enigma.proplybackend.model.request.UserRequest;
import com.enigma.proplybackend.model.response.DivisionResponse;
import com.enigma.proplybackend.model.response.UserCredentialResponse;
import com.enigma.proplybackend.model.response.UserResponse;
import com.enigma.proplybackend.repository.UserRepository;
import com.enigma.proplybackend.service.DivisionService;
import com.enigma.proplybackend.service.UserCredentialService;
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

    @Override
    public UserResponse addUser(UserRequest userRequest) {
        DivisionResponse divisionResponse = divisionService.getDivisionById(userRequest.getDivisionId());
        UserCredentialResponse userCredentialResponse = userCredentialService.getByEmail(userRequest.getEmail());

        User user = toUser(userRequest, divisionResponse);
        userRepository.save(user);

        return toUserResponse(user, userCredentialResponse, divisionResponse);
    }


    @Override
    public UserResponse updateUser(UserRequest userRequest) {
        User user = userRepository.findById(userRequest.getId()).orElseThrow(() -> new ApplicationException("User not found", "User with id=" + userRequest.getId() + " not found", HttpStatus.BAD_REQUEST));

        DivisionResponse divisionResponse = divisionService.getDivisionById(userRequest.getDivisionId());
        UserCredentialResponse userCredentialResponse = userCredentialService.getByEmail(userRequest.getEmail());

        user.setFullName(userRequest.getFullName());
        user.setBirthDate(userRequest.getBirthDate());
        user.setGender(userRequest.getGender());
        user.setMaritalStatus(userRequest.getMaritalStatus());
        user.setDivision(Division.builder()
                .id(divisionResponse.getDivisionId())
                .name(divisionResponse.getName())
                .isActive(divisionResponse.getIsActive())
                .build());
        user.setIsActive(true);
        userRepository.save(user);

        return toUserResponse(user, userCredentialResponse, divisionResponse);
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

        DivisionResponse divisionResponse = divisionService.getDivisionById(user.getDivision().getId());

        return toUserResponse(user, userCredentialResponse, divisionResponse);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> userList = userRepository.findAll();
        List<UserResponse> userResponseList = new ArrayList<>();

        if (userList.isEmpty()) throw new ApplicationException("No users were found", null, HttpStatus.NOT_FOUND);

        for (User user : userList) {
            UserCredentialResponse userCredentialResponse = userCredentialService.getByUserId(user.getId());
            DivisionResponse divisionResponse = divisionService.getDivisionById(user.getDivision().getId());

            userResponseList.add(toUserResponse(user, userCredentialResponse, divisionResponse));
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
            DivisionResponse divisionResponse = divisionService.getDivisionById(user.getDivision().getId());

            userResponseList.add(toUserResponse(user, userCredentialResponse, divisionResponse));
        }

        return userResponseList;
    }

    private static UserResponse toUserResponse(User user, UserCredentialResponse userCredentialResponse, DivisionResponse divisionResponse) {
        return UserResponse.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .birthDate(user.getBirthDate())
                .maritalStatus(user.getMaritalStatus())
                .userCredentialResponse(userCredentialResponse)
                .divisionResponse(divisionResponse)
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
