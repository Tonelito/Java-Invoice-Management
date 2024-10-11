package com.is4tech.invoicemanagement.dto;

import java.util.List;

import com.is4tech.invoicemanagement.annotations.EntityName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@EntityName("Profile")
public class ProfileDto {

  @Schema(hidden = true)
  private Integer profileId;
  @NotNull(message = "[Nombre del perfil] no debe de ser nulo.")
  @NotBlank(message = "[Nombre del perfil] no debe estar en blanco")
  @Size(max = 30, message = "[Nombre del perfil] no puede tener más de 30 caracteres.")
  private String name;
  @NotNull(message = "[Descripción] no debe de ser nulo.")
  @NotBlank(message = "[Descripción] no debe estar en blanco.")
  @Size(max = 75, message = "[Descripción] no puede tener más de 75 caracteres.")
  private String description;
  @Schema(hidden = true)
  private Boolean status;
  private List<Integer> rolsId;

}
