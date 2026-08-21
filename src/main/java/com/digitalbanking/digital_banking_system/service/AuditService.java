package com.digitalbanking.digital_banking_system.service;

import com.digitalbanking.digital_banking_system.entity.AuditLog;
import com.digitalbanking.digital_banking_system.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AuditService {

    void log(User user, String action, String entityType, Long entityId, String oldValue, String newValue, String ipAddress);

    void log(String action, String entityType, Long entityId);

    void log(String action, String entityType, Long entityId, String oldValue, String newValue);

    List<AuditLog> getAuditLogsByUserId(Long userId);

    Page<AuditLog> getAuditLogsByUserId(Long userId, Pageable pageable);

    Page<AuditLog> getAllAuditLogs(Pageable pageable);
}
