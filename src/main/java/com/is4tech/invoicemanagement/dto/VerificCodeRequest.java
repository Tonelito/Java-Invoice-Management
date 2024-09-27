package com.is4tech.invoicemanagement.dto;

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
public class VerificCodeRequest {
    private CodePasswordDto codePassword;
    private EmailDto email;
}
