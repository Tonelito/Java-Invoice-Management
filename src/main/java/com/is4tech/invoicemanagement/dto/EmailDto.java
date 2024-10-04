package com.is4tech.invoicemanagement.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailDto {
    @NotEmpty(message = "The email is required")
    private String email;
}
