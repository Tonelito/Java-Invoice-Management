package com.is4tech.invoicemanagement.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AuditDto {

    private Integer auditId;
    private String entity;
    private String request;
    private String response;
    private Float responseTime;
    private LocalDateTime datetime;
    private String operation;
    private Integer userId;

}
