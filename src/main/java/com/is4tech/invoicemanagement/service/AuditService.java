package com.is4tech.invoicemanagement.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.is4tech.invoicemanagement.dto.AuditDto;
import com.is4tech.invoicemanagement.exception.BadRequestException;
import com.is4tech.invoicemanagement.model.Audit;
import com.is4tech.invoicemanagement.model.User;
import com.is4tech.invoicemanagement.repository.AuditRepository;
import com.is4tech.invoicemanagement.repository.UserRepository;
import com.is4tech.invoicemanagement.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class AuditService {
    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);

    private final UserRepository userRepository;
    private final AuditRepository auditRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuditService(UserRepository userRepository, AuditRepository auditRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.auditRepository = auditRepository;
        this.jwtUtil = jwtUtil;
    }

    public void logAudit(Object object, Method method, Exception ex, int statusCode, String entity, HttpServletRequest request) {
        AuditDto auditDto = new AuditDto();

        Integer userId = null;
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            userId = jwtUtil.extractUserId(token).intValue();
        }

        auditDto.setStatusCode(statusCode);
        auditDto.setDatetime(LocalDateTime.now());
        auditDto.setOperation(request.getMethod());
        auditDto.setUserId(userId);
        auditDto.setEntity(entity);
        auditDto.setErrorMessage(ex != null ? ex.getMessage() : null);
        auditDto.setRequest(formatRequestToJson(object));
        System.out.println(auditDto.getRequest() + " request");
        saveAudit(auditDto);
    }

    private void saveAudit(AuditDto auditDto) {
        Integer userId = auditDto.getUserId();
        logger.info("Logging audit: {}", auditDto);

        if (userId == null) {
            throw new BadRequestException("User ID must not be null");
        }

        User user = userRepository.findById(userId).orElseThrow(() ->
                new BadRequestException("User not found with ID: " + userId));

        Audit audit = new Audit();
        audit.setEntity(auditDto.getEntity());
        audit.setRequest(auditDto.getRequest());
        audit.setStatusCode(auditDto.getStatusCode());
        audit.setErrorMessage(auditDto.getErrorMessage());
        audit.setDatetime(auditDto.getDatetime());
        audit.setOperation(auditDto.getOperation());
        audit.setUser(user);

        auditRepository.save(audit);
    }

    @Transactional
    public Page<AuditDto> findByEntityAndDateRangeAndOptionalUserId(String entity, LocalDate startDate, LocalDate endDate, Integer userId, Pageable pageable) {
        Page<Audit> auditsPage = auditRepository.findByEntityAndDateRangeAndOptionalUserId(entity, startDate, endDate, userId, pageable);

        return auditsPage.map(audit -> {
            AuditDto dto = new AuditDto();
            dto.setAuditId(audit.getAuditId());
            dto.setEntity(audit.getEntity());
            dto.setRequest(audit.getRequest());
            dto.setStatusCode(audit.getStatusCode());
            dto.setErrorMessage(audit.getErrorMessage());
            dto.setDatetime(audit.getDatetime());
            dto.setOperation(audit.getOperation());
            dto.setUserId(audit.getUser().getUserId());
            dto.setFullName(audit.getUser().getFullName());
            return dto;
        });
    }

    private String formatRequestToJson(Object object) {
        if (object == null) {
            return "{}";
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Error converting object to JSON: {}", e.getMessage());
            return "{}";
        }
    }
}