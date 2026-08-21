package com.digitalbanking.digital_banking_system.service;

import com.digitalbanking.digital_banking_system.dto.request.CreateUserRequest;
import com.digitalbanking.digital_banking_system.dto.request.LoginRequest;
import com.digitalbanking.digital_banking_system.dto.request.RefreshTokenRequest;
import com.digitalbanking.digital_banking_system.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(CreateUserRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);
}
