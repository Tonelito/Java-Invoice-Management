package com.is4tech.invoicemanagement.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class ProfileRoleDetailDtoId {

    @Schema(hidden = true)
    private Integer profileId;
    @Schema(hidden = true)
    private Integer roleId;
    private List<Integer> rols;
}
