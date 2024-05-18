package com.enigma.proplybackend.service;

import com.enigma.proplybackend.model.request.UserRequest;
import com.enigma.proplybackend.model.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse addUser(UserRequest userRequest);
    UserResponse updateUser(UserRequest userRequest);
    void deleteUser(String userId);
    UserResponse getUserById(String userId);
    List<UserResponse> getAllUsers();
    List<UserResponse> getAllUserWhereActive();
}
