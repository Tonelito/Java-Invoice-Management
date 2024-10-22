package com.is4tech.invoicemanagement.dto;

import com.is4tech.invoicemanagement.annotations.EntityName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@Builder
@EntityName("Users")
public class UserUpdateDto {

    @Schema(hidden = true)
    private Integer userId;
    @Schema(hidden = true)
    private String email;
    @Schema(hidden = true)
    private String password;
    @NotNull(message = "[Nombre completo] no debe de ser nulo.")
    @NotBlank(message = "[Nombre completo] no debe estar en blanco.")
    @Size(max = 100, message = "[Nombre completo] no puede tener más de 100 caracteres.")
    private String fullName;
    @NotNull(message = "[Id del perfil] no debe de ser nulo.")
    @Positive(message = "[Id del perfil] debe de ser un número positivo.")
    private Integer profileId;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirth;
    @Schema(hidden = true)
    private Boolean status;
}
