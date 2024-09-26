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
public class ProfileRoleDetailDtoId {
    
    private Integer profileId;
    private Integer roleId;
    private List<Integer> rols;
}
