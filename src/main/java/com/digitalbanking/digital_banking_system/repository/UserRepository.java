package com.digitalbanking.digital_banking_system.repository;

import com.digitalbanking.digital_banking_system.entity.User;
import com.digitalbanking.digital_banking_system.enums.Role;
import com.digitalbanking.digital_banking_system.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    List<User> findByStatus(Status status);

    List<User> findByRole(Role role);

    List<User> findByRoleAndStatus(Role role, Status status);
}
