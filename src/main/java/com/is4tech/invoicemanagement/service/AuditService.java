package com.is4tech.invoicemanagement.service;

import com.is4tech.invoicemanagement.dto.AuditDto;
import com.is4tech.invoicemanagement.exception.BadRequestException;
import com.is4tech.invoicemanagement.model.Audit;
import com.is4tech.invoicemanagement.model.User;
import com.is4tech.invoicemanagement.repository.AuditRepository;
import com.is4tech.invoicemanagement.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
        audit.setDatetime(auditDto.getDatetime());
        audit.setOperation(auditDto.getOperation());
        audit.setUser(user);

        auditRepository.save(audit);
    }

    @Transactional
    public Page<AuditDto> findByEntityAndDate(String entity, LocalDate date, Pageable pageable) {
        Page<Audit> auditsPage = auditRepository.findByEntityAndDate(entity, date, pageable);

        return auditsPage.map(audit -> {
            AuditDto dto = new AuditDto();
            dto.setAuditId(audit.getAuditId());
            dto.setEntity(audit.getEntity());
            dto.setRequest(audit.getRequest());
            dto.setResponse(audit.getResponse());
            dto.setResponseTime(audit.getResponseTime());
            dto.setDatetime(audit.getDatetime());
            dto.setOperation(audit.getOperation());
            dto.setUserId(audit.getUser().getUserId());
            return dto;
        });
    }
}
