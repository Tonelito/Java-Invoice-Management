package com.is4tech.invoicemanagement.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
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
public class UserChangePasswordDto {
    
    @NotEmpty(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(unique = true)
    private String email;
    @NotEmpty(message = "New Password is required")
    private String newPassword;
    @NotEmpty(message = "Old password is required")
    private String password;
}
