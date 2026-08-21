package com.digitalbanking.digital_banking_system.repository;

import com.digitalbanking.digital_banking_system.entity.User;
import com.digitalbanking.digital_banking_system.enums.Role;
import com.digitalbanking.digital_banking_system.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .firstName("John")
                .lastName("Doe")
                .phone("+1234567890")
                .role(Role.CUSTOMER)
                .status(Status.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("Should find user by email")
    void findByEmail_Success() {
        entityManager.persistAndFlush(testUser);

        Optional<User> found = userRepository.findByEmail("test@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
        assertThat(found.get().getFirstName()).isEqualTo("John");
    }

    @Test
    @DisplayName("Should return empty when email not found")
    void findByEmail_NotFound() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should check if email exists")
    void existsByEmail_True() {
        entityManager.persistAndFlush(testUser);

        boolean exists = userRepository.existsByEmail("test@example.com");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when email does not exist")
    void existsByEmail_False() {
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should check if phone exists")
    void existsByPhone_True() {
        entityManager.persistAndFlush(testUser);

        boolean exists = userRepository.existsByPhone("+1234567890");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should find users by status")
    void findByStatus_Success() {
        entityManager.persistAndFlush(testUser);

        User inactiveUser = User.builder()
                .email("inactive@example.com")
                .password("encodedPassword")
                .firstName("Jane")
                .lastName("Doe")
                .role(Role.CUSTOMER)
                .status(Status.INACTIVE)
                .build();
        entityManager.persistAndFlush(inactiveUser);

        List<User> activeUsers = userRepository.findByStatus(Status.ACTIVE);

        assertThat(activeUsers).hasSize(1);
        assertThat(activeUsers.get(0).getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should find users by role")
    void findByRole_Success() {
        entityManager.persistAndFlush(testUser);

        User adminUser = User.builder()
                .email("admin@example.com")
                .password("encodedPassword")
                .firstName("Admin")
                .lastName("User")
                .role(Role.ADMIN)
                .status(Status.ACTIVE)
                .build();
        entityManager.persistAndFlush(adminUser);

        List<User> customers = userRepository.findByRole(Role.CUSTOMER);

        assertThat(customers).hasSize(1);
        assertThat(customers.get(0).getRole()).isEqualTo(Role.CUSTOMER);
    }

    @Test
    @DisplayName("Should find users by role and status")
    void findByRoleAndStatus_Success() {
        entityManager.persistAndFlush(testUser);

        List<User> activeCustomers = userRepository.findByRoleAndStatus(Role.CUSTOMER, Status.ACTIVE);

        assertThat(activeCustomers).hasSize(1);
        assertThat(activeCustomers.get(0).getEmail()).isEqualTo("test@example.com");
    }
}
