package com.is4tech.invoicemanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
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
    @NotEmpty(message = "Code is required")
    private CodePasswordDto codePassword;
    @NotEmpty(message = "Code is required")
    @Email(message = "Email must be valid")
    private EmailDto email;
}
