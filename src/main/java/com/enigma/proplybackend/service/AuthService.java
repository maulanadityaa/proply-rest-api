package com.enigma.proplybackend.service;

import com.enigma.proplybackend.model.request.AuthRequest;
import com.enigma.proplybackend.model.response.LoginResponse;
import com.enigma.proplybackend.model.response.RegisterResponse;

public interface AuthService {
    RegisterResponse registerAdmin(AuthRequest authRequest);

    RegisterResponse registerEmployee(AuthRequest authRequest);

    LoginResponse login(AuthRequest authRequest);
}
