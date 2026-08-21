package com.digitalbanking.digital_banking_system.service.impl;

import com.digitalbanking.digital_banking_system.dto.request.CreateUserRequest;
import com.digitalbanking.digital_banking_system.dto.request.LoginRequest;
import com.digitalbanking.digital_banking_system.dto.request.RefreshTokenRequest;
import com.digitalbanking.digital_banking_system.dto.response.AuthResponse;
import com.digitalbanking.digital_banking_system.dto.response.UserDTO;
import com.digitalbanking.digital_banking_system.entity.Account;
import com.digitalbanking.digital_banking_system.entity.User;
import com.digitalbanking.digital_banking_system.enums.AccountStatus;
import com.digitalbanking.digital_banking_system.enums.AccountType;
import com.digitalbanking.digital_banking_system.enums.Role;
import com.digitalbanking.digital_banking_system.enums.Status;
import com.digitalbanking.digital_banking_system.exception.DuplicateResourceException;
import com.digitalbanking.digital_banking_system.exception.InvalidOperationException;
import com.digitalbanking.digital_banking_system.mapper.UserMapper;
import com.digitalbanking.digital_banking_system.repository.AccountRepository;
import com.digitalbanking.digital_banking_system.repository.UserRepository;
import com.digitalbanking.digital_banking_system.security.JwtService;
import com.digitalbanking.digital_banking_system.service.AuditService;
import com.digitalbanking.digital_banking_system.service.AuthService;
import com.digitalbanking.digital_banking_system.util.AccountNumberGenerator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final AuditService auditService;
    private final AccountNumberGenerator accountNumberGenerator;

    public AuthServiceImpl(UserRepository userRepository,
                           AccountRepository accountRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           AuthenticationManager authenticationManager,
                           UserMapper userMapper,
                           AuditService auditService,
                           AccountNumberGenerator accountNumberGenerator) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userMapper = userMapper;
        this.auditService = auditService;
        this.accountNumberGenerator = accountNumberGenerator;
    }

    @Override
    public AuthResponse register(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("User", "phone", request.getPhone());
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .role(Role.CUSTOMER)
                .status(Status.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);

        // Auto-create a SAVINGS account for the new user
        String accountNumber;
        do {
            accountNumber = accountNumberGenerator.generate();
        } while (accountRepository.existsByAccountNumber(accountNumber));

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .accountType(AccountType.SAVINGS)
                .user(savedUser)
                .balance(BigDecimal.ZERO)
                .status(AccountStatus.ACTIVE)
                .build();

        accountRepository.save(account);

        String accessToken = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);
        UserDTO userDTO = userMapper.toDTO(savedUser);

        auditService.log(savedUser, "USER_REGISTRATION", "User", savedUser.getId(), null, null, null);
        auditService.log(savedUser, "ACCOUNT_CREATED", "Account", account.getId(), null, "accountNumber=" + accountNumber, null);

        return AuthResponse.of(accessToken, refreshToken, jwtService.getExpirationTime(), userDTO);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidOperationException("login", "User not found"));

        if (user.getStatus() != Status.ACTIVE) {
            throw new InvalidOperationException("login", "User account is not active");
        }

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        UserDTO userDTO = userMapper.toDTO(user);

        auditService.log(user, "USER_LOGIN", "User", user.getId(), null, null, null);

        return AuthResponse.of(accessToken, refreshToken, jwtService.getExpirationTime(), userDTO);
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        String userEmail = jwtService.extractUsername(refreshToken);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new InvalidOperationException("token refresh", "User not found"));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new InvalidOperationException("token refresh", "Invalid refresh token");
        }

        String accessToken = jwtService.generateToken(user);
        UserDTO userDTO = userMapper.toDTO(user);

        return AuthResponse.of(accessToken, refreshToken, jwtService.getExpirationTime(), userDTO);
    }
}
