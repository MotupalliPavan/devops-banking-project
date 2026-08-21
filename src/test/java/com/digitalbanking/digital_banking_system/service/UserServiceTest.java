package com.digitalbanking.digital_banking_system.service;

import com.digitalbanking.digital_banking_system.dto.request.ChangePasswordRequest;
import com.digitalbanking.digital_banking_system.dto.request.UpdateUserRequest;
import com.digitalbanking.digital_banking_system.dto.response.UserDTO;
import com.digitalbanking.digital_banking_system.entity.User;
import com.digitalbanking.digital_banking_system.enums.Role;
import com.digitalbanking.digital_banking_system.enums.Status;
import com.digitalbanking.digital_banking_system.exception.InvalidOperationException;
import com.digitalbanking.digital_banking_system.exception.ResourceNotFoundException;
import com.digitalbanking.digital_banking_system.mapper.UserMapper;
import com.digitalbanking.digital_banking_system.repository.UserRepository;
import com.digitalbanking.digital_banking_system.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditService auditService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .firstName("John")
                .lastName("Doe")
                .phone("+1234567890")
                .role(Role.CUSTOMER)
                .status(Status.ACTIVE)
                .build();

        testUserDTO = UserDTO.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .phone("+1234567890")
                .role(Role.CUSTOMER)
                .status(Status.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("Should get user by ID successfully")
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toDTO(testUser)).thenReturn(testUserDTO);

        UserDTO result = userService.getUserById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void getUserById_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User");

        verify(userRepository).findById(99L);
    }

    @Test
    @DisplayName("Should update user successfully")
    void updateUser_Success() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDTO(any(User.class))).thenReturn(testUserDTO);

        UserDTO result = userService.updateUser(1L, request);

        assertThat(result).isNotNull();
        verify(userRepository).save(any(User.class));
        verify(auditService).log(anyString(), anyString(), anyLong(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should deactivate user successfully")
    void deactivateUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.deactivateUser(1L);

        assertThat(testUser.getStatus()).isEqualTo(Status.INACTIVE);
        verify(userRepository).save(testUser);
        verify(auditService).log(anyString(), anyString(), anyLong(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should activate user successfully")
    void activateUser_Success() {
        testUser.setStatus(Status.INACTIVE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.activateUser(1L);

        assertThat(testUser.getStatus()).isEqualTo(Status.ACTIVE);
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should change password successfully")
    void changePassword_Success() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("oldPassword")
                .newPassword("newPassword123")
                .confirmPassword("newPassword123")
                .build();

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");

        userService.changePassword(request);

        verify(userRepository).save(testUser);
        verify(auditService).log(anyString(), anyString(), anyLong());
    }

    @Test
    @DisplayName("Should throw exception when current password is wrong")
    void changePassword_WrongCurrentPassword() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("wrongPassword")
                .newPassword("newPassword123")
                .confirmPassword("newPassword123")
                .build();

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThatThrownBy(() -> userService.changePassword(request))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("Current password is incorrect");
    }
}
