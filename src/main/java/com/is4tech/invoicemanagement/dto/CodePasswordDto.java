package com.is4tech.invoicemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "[Código] no debe de ser nulo.")
    @NotBlank(message = "[Código] no debe estar en blanco.")
    private String code;
    @NotNull(message = "[Contraseña] no debe de ser nulo.")
    @NotBlank(message = "[Contraseña] no debe estar en blanco.")
    private String newPassword;
}
