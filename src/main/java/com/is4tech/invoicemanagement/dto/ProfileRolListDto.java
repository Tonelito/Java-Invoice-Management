package com.is4tech.invoicemanagement.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
public class ProfileRolListDto {

  @Schema(hidden = true)
  private Integer profileId;
  @NotNull(message = "[Nombre] no debe de ser nulo.")
  @NotBlank(message = "[Nombre] no debe estar en blanco.")
  private String name;
  @NotNull(message = "[Descripción] no debe de ser nulo.")
  @NotBlank(message = "[Descripción] no debe estar en blanco")
  private String description;
  @Schema(hidden = true)
  private Boolean status;
  private List<RolDto> rolsId;
}
