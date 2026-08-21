package com.digitalbanking.digital_banking_system.service.impl;

import com.digitalbanking.digital_banking_system.dto.request.ChangePasswordRequest;
import com.digitalbanking.digital_banking_system.dto.request.UpdateUserRequest;
import com.digitalbanking.digital_banking_system.dto.response.UserDTO;
import com.digitalbanking.digital_banking_system.entity.User;
import com.digitalbanking.digital_banking_system.enums.Status;
import com.digitalbanking.digital_banking_system.exception.InvalidOperationException;
import com.digitalbanking.digital_banking_system.exception.ResourceNotFoundException;
import com.digitalbanking.digital_banking_system.mapper.UserMapper;
import com.digitalbanking.digital_banking_system.repository.UserRepository;
import com.digitalbanking.digital_banking_system.service.AuditService;
import com.digitalbanking.digital_banking_system.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper,
                           PasswordEncoder passwordEncoder,
                           AuditService auditService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getCurrentUser() {
        return userMapper.toDTO(getCurrentUserEntity());
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUserEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return userMapper.toDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return userMapper.toDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userMapper.toDTOList(userRepository.findAll());
    }

    @Override
    public UserDTO updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        String oldValue = String.format("firstName=%s,lastName=%s,phone=%s",
                user.getFirstName(), user.getLastName(), user.getPhone());

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        User savedUser = userRepository.save(user);

        String newValue = String.format("firstName=%s,lastName=%s,phone=%s",
                savedUser.getFirstName(), savedUser.getLastName(), savedUser.getPhone());

        auditService.log("UPDATE_USER", "User", id, oldValue, newValue);

        return userMapper.toDTO(savedUser);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        User user = getCurrentUserEntity();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidOperationException("password change", "Current password is incorrect");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new InvalidOperationException("password change", "New password and confirm password do not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        auditService.log("CHANGE_PASSWORD", "User", user.getId());
    }

    @Override
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        String oldStatus = user.getStatus().name();
        user.setStatus(Status.INACTIVE);
        userRepository.save(user);

        auditService.log("DEACTIVATE_USER", "User", id, oldStatus, Status.INACTIVE.name());
    }

    @Override
    public void activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        String oldStatus = user.getStatus().name();
        user.setStatus(Status.ACTIVE);
        userRepository.save(user);

        auditService.log("ACTIVATE_USER", "User", id, oldStatus, Status.ACTIVE.name());
    }
}
