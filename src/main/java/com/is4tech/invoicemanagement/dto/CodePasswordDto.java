package com.is4tech.invoicemanagement.dto;

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
public class CodePasswordDto {
    @NotEmpty(message = "Code is required")
    private String code;
    @NotEmpty(message = "The new Passoword is required")
    private String newPassword;
}
