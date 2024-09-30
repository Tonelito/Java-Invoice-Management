package com.is4tech.invoicemanagement.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
    @JsonIgnore
    private Integer roleId;
    private List<Integer> rols;
}
