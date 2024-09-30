package com.is4tech.invoicemanagement.service;

import com.is4tech.invoicemanagement.dto.AuditDto;
import com.is4tech.invoicemanagement.exception.BadRequestException;
import com.is4tech.invoicemanagement.model.Audit;
import com.is4tech.invoicemanagement.model.User;
import com.is4tech.invoicemanagement.repository.AuditRepository;
import com.is4tech.invoicemanagement.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditRepository auditRepository;
    private final UserRepository userRepository;

    @Transactional
    public void logAudit(AuditDto auditDto) {
        Integer userId = auditDto.getUserId();

        if (userId == null) {
            throw new BadRequestException("User ID must not be null");
        }

        User user = userRepository.findById(userId).orElseThrow(() ->
                new BadRequestException("User not found with ID: " + userId));

        Audit audit = new Audit();
        audit.setEntity(auditDto.getEntity());
        audit.setRequest(auditDto.getRequest());
        audit.setResponse(auditDto.getResponse());
        audit.setResponseTime(auditDto.getResponseTime());
        audit.setDatetime(auditDto.getDateTime());
        audit.setOperation(auditDto.getOperation());
        audit.setUser(user);

        auditRepository.save(audit);
    }
}
