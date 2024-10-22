package com.is4tech.invoicemanagement.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
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
public class UserChangePasswordDto {

    @NotNull(message = "[Correo electrónico] no debe de ser nulo.")
    @NotBlank(message = "[Correo electrónico] no debe estar en blanco.")
    @Email(message = "[Correo electrónico] debe de tener un formato válido.")
    @Size(max = 50, message = "[Correo electrónico] no puede tener más de 50 caracteres.")
    @Column(unique = true)
    private String email;
    @NotNull(message = "[Contraseña nueva] no debe de ser nulo.")
    @NotBlank(message = "[Contraseña nueva] no debe estar en blanco.")
    private String newPassword;
    @NotNull(message = "[Contraseña antigua] no debe de ser nulo.")
    @NotBlank(message = "[Contraseña antigua] no debe estar en blanco.")
    private String password;
}
