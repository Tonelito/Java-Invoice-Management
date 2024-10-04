package com.is4tech.invoicemanagement.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
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
      
  private Integer profileId;
  @NotEmpty(message = "Name is required")
  private String name;
  @NotEmpty(message = "Description is required")
  private String description;
  private Boolean status;
  private List<RolDto> rolsId;
}
