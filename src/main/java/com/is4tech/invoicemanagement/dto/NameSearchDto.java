package com.is4tech.invoicemanagement.dto;

import com.is4tech.invoicemanagement.annotations.EntityName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EntityName("Profile")
public class NameSearchDto {

    @NotNull(message = "[Nombre del perfil a buscar] no debe de ser nulo.")
    @NotBlank(message = "[Nombre del perfil a buscar] no debe estar vacío.")
    private String name;
}
