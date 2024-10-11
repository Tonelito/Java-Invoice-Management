package com.is4tech.invoicemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AuditSearchDto {

    @NotNull(message = "[Entidad] no debe de ser nula.")
    @NotBlank(message = "[Entidad] no debe de estar en blanco.")
    private String entity;
    @NotNull(message = "[Fecha inicio] no debe de ser nula.")
    private LocalDate startDate;
    @NotNull(message = "[Fecha fin] no debe de ser nula.")
    private LocalDate endDate;
    private Integer userId;
}
