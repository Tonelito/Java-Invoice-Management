package com.is4tech.invoicemanagement.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.is4tech.invoicemanagement.dto.AuditDto;
import com.is4tech.invoicemanagement.exception.BadRequestException;
import com.is4tech.invoicemanagement.exception.ResourceNorFoundException;
import com.is4tech.invoicemanagement.model.Audit;
import com.is4tech.invoicemanagement.model.User;
import com.is4tech.invoicemanagement.repository.AuditRepository;
import com.is4tech.invoicemanagement.repository.UserRepository;
import com.is4tech.invoicemanagement.utils.JwtUtil;
import com.is4tech.invoicemanagement.utils.MessagePage;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditService {
    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);

    private final UserRepository userRepository;
    private final AuditRepository auditRepository;
    private final JwtUtil jwtUtil;

    public AuditService(UserRepository userRepository, AuditRepository auditRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.auditRepository = auditRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
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
        this.saveAudit(auditDto);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAudit(AuditDto auditDto) {
        if (auditDto.getUserId() == null) {
            throw new BadRequestException("User ID must not be null");
        }

        User user = userRepository.findById(auditDto.getUserId()).orElseThrow(() ->
                new BadRequestException("User not found with ID: " + auditDto.getUserId()));

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

    @Transactional(readOnly = true)
    public MessagePage findByEntityAndDateRangeAndOptionalUserId(String entity, LocalDate startDate, LocalDate endDate, String fullName, Pageable pageable) {
        Page<Audit> auditsPage = auditRepository.findByEntityAndDateRangeAndOptionalFullName(entity, startDate, endDate, fullName, pageable);

        if (auditsPage.isEmpty()) {
            if (fullName != null && !fullName.isEmpty()) {
                throw ResourceNorFoundException.auditNotFoundWithName(entity, startDate, endDate, fullName);
            } else {
                throw ResourceNorFoundException.auditNotFoundWithoutName(entity, startDate, endDate);
            }
        }

        List<AuditDto> auditDtos = auditsPage.getContent().stream()
                .map(audit -> AuditDto.builder()
                        .auditId(audit.getAuditId())
                        .entity(audit.getEntity())
                        .request(audit.getRequest())
                        .statusCode(audit.getStatusCode())
                        .errorMessage(audit.getErrorMessage())
                        .datetime(audit.getDatetime())
                        .operation(audit.getOperation())
                        .userId(audit.getUser().getUserId())
                        .fullName(audit.getUser().getFullName())
                        .build())
                .collect(Collectors.toList());

        return MessagePage.builder()
                .note("Auditorías encontradas")
                .object(auditDtos)
                .totalElements((int) auditsPage.getTotalElements())
                .totalPages(auditsPage.getTotalPages())
                .currentPage(auditsPage.getNumber())
                .pageSize(auditsPage.getSize())
                .build();
    }

    @Transactional(readOnly = true)
    public MessagePage findAllAudits(Pageable pageable) {
        Page<Audit> auditsPage = auditRepository.findAllByOrderByDatetimeAsc(pageable);

        if (auditsPage.isEmpty()) {
            throw new ResourceNorFoundException("Auditorías");
        }

        List<AuditDto> auditDtos = auditsPage.getContent().stream()
                .map(audit -> AuditDto.builder()
                        .auditId(audit.getAuditId())
                        .entity(audit.getEntity())
                        .request(audit.getRequest())
                        .statusCode(audit.getStatusCode())
                        .errorMessage(audit.getErrorMessage())
                        .datetime(audit.getDatetime())
                        .operation(audit.getOperation())
                        .userId(audit.getUser().getUserId())
                        .fullName(audit.getUser().getFullName())
                        .build())
                .collect(Collectors.toList());

        return MessagePage.builder()
                .note("Auditorías encontradas")
                .object(auditDtos)
                .totalElements((int) auditsPage.getTotalElements())
                .totalPages(auditsPage.getTotalPages())
                .currentPage(auditsPage.getNumber())
                .pageSize(auditsPage.getSize())
                .build();
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
