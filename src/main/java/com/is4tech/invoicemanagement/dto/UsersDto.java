package com.is4tech.invoicemanagement.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
@Builder
public class UsersDto {

    @JsonIgnore
    private Integer userId;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico debe ser válido")
    @Column(unique = true)
    private String email;

    @JsonIgnore
    private String password;

    @NotBlank(message = "El nombre completo es obligatorio")
    private String fullName;

    @NotNull(message = "El ID del perfil es obligatorio")
    private Integer profileId;

    private Date dateOfBirth;

    @JsonIgnore
    private Boolean status;
}
