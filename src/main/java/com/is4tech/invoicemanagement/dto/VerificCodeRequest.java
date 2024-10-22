package com.is4tech.invoicemanagement.dto;

import jakarta.validation.constraints.Email;
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
public class VerificCodeRequest {
    @NotNull(message = "[Código] no debe de ser nulo.")
    private CodePasswordDto codePassword;
    @NotNull(message = "[Correo electrónico] no debe de ser nulo.")
    @Email(message = "[Correo electrónico] debe de tener un formato válido.")
    private EmailDto email;
}
