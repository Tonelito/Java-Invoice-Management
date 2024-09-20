package com.is4tech.invoicemanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class RegisterDto {
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico debe ser válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @NotBlank(message = "El nombre completo es obligatorio")
    private String fullName;

    @NotNull(message = "El ID del perfil es obligatorio")
    private Integer profileId;

    private Date dateOfBirth;
}
