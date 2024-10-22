package com.is4tech.invoicemanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailDto {

    @NotNull(message = "[Correo electrónico] no debe de ser nulo.")
    @NotBlank(message = "[Correo electrónico] no debe estar en blanco.")
    @Email(message = "[Correo electrónico] debe tener un formato válido.")
    private String email;
}
