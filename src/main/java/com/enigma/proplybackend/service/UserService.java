package com.enigma.proplybackend.service;

import com.enigma.proplybackend.model.entity.User;
import com.enigma.proplybackend.model.request.UserRequest;
import com.enigma.proplybackend.model.response.UserResponse;

import java.util.List;

public interface UserService {
    User addUser(UserRequest userRequest);

    UserResponse updateUser(UserRequest userRequest);

    void deleteUser(String userId);

    UserResponse getUserById(String userId);

    UserResponse getUserByEmail(String userEmail);

    List<UserResponse> getAllUsers();

    List<UserResponse> getAllUserWhereActive();
}
