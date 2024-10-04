package com.is4tech.invoicemanagement.dto;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AuditSearchDto {
    private String entity;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer userId;
}
