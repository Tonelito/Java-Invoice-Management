package com.is4tech.invoicemanagement.controller;

import com.is4tech.invoicemanagement.dto.AuditDto;
import com.is4tech.invoicemanagement.dto.AuditSearchDto;
import com.is4tech.invoicemanagement.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invoice-management/v0.1/audit/")
public class AuditController {

    @Autowired
    private AuditService auditService;

    @PostMapping("/search")
    public ResponseEntity<Page<AuditDto>> findByEntityAndDateRange(
            @RequestBody AuditSearchDto auditSearchDto,
            Pageable pageable) {

        Page<AuditDto> audits = auditService.findByEntityAndDateRangeAndOptionalUserId(
                auditSearchDto.getEntity(),
                auditSearchDto.getStartDate(),
                auditSearchDto.getEndDate(),
                auditSearchDto.getUserId(),
                pageable);

        return ResponseEntity.ok(audits);
    }
}
