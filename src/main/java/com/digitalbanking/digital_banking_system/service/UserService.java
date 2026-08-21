package com.digitalbanking.digital_banking_system.service;

import com.digitalbanking.digital_banking_system.dto.request.ChangePasswordRequest;
import com.digitalbanking.digital_banking_system.dto.request.UpdateUserRequest;
import com.digitalbanking.digital_banking_system.dto.response.UserDTO;
import com.digitalbanking.digital_banking_system.entity.User;

import java.util.List;

public interface UserService {

    UserDTO getCurrentUser();

    User getCurrentUserEntity();

    UserDTO getUserById(Long id);

    UserDTO getUserByEmail(String email);

    List<UserDTO> getAllUsers();

    UserDTO updateUser(Long id, UpdateUserRequest request);

    void changePassword(ChangePasswordRequest request);

    void deactivateUser(Long id);

    void activateUser(Long id);
}
