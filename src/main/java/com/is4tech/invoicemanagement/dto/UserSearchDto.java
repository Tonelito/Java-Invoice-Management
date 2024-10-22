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
@EntityName("Users")
public class UserSearchDto {

    @NotNull(message = "[Nombre] no debe de ser nulo.")
    @NotBlank(message = "[Nombre] no debe estar en blanco.")
    private String fullName;
}
