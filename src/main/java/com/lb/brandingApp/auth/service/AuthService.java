package com.lb.brandingApp.auth.service;

import com.lb.brandingApp.auth.data.dto.request.RegistrationRequestDto;
import com.lb.brandingApp.auth.data.dto.request.UserRequestDto;
import com.lb.brandingApp.auth.data.dto.response.UserResponseDto;

import java.util.List;

public interface AuthService {
    UserResponseDto authenticate(UserRequestDto request);

    void changePassword(UserRequestDto request);

    List<UserResponseDto> getAllUsers(boolean fetchActive);

    UserResponseDto getUser(String username);

    void resetUserPassword(UserRequestDto request);

    void toggleUserActivation(UserRequestDto request, boolean active);

    UserResponseDto registerUser(RegistrationRequestDto request);

    UserResponseDto updateUser(RegistrationRequestDto request);
}
