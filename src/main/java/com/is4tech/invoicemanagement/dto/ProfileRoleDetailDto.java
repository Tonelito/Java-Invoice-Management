package com.is4tech.invoicemanagement.dto;

import java.util.List;

import com.is4tech.invoicemanagement.model.Profile;
import com.is4tech.invoicemanagement.model.Rol;

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
public class ProfileRoleDetailDto {
    
    private Profile profile;
    private List<Rol> roles;
}
