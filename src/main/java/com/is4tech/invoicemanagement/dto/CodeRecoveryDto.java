package com.is4tech.invoicemanagement.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CodeRecoveryDto {

    private Integer codeRecoveryId;
    private String code;
    private Date expirationDate;
}