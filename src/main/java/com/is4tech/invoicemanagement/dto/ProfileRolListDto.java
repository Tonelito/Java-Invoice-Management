package com.is4tech.invoicemanagement.dto;

import java.util.List;

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
  private String name;
  private String description;
  private Boolean status;
  private List<RolDto> rolsId;
}
