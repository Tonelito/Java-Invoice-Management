package com.is4tech.invoicemanagement.dto;

import com.is4tech.invoicemanagement.annotations.EntityName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityName("Role")
public class RolDto {

    @Schema(hidden = true)
    private Integer rolId;
    @NotNull(message = "[Nombre del rol] no debe ser nulo.")
    @NotBlank(message = "[Nombre del rol] no debe estar en blanco.")
    @Size(max = 50, message = "[Nombre del rol] no puede tener más de 50 caracteres.")
    private String name;
    @NotNull(message = "[Código del rol] no debe ser nulo.")
    @NotBlank(message = "[Código del rol] no debe estar en blanco.")
    @Size(max = 75, message = "[Código del rol] no puede tener más de 75 caracteres.")
    private String code;
    @Schema(hidden = true)
    private Boolean status;
}
