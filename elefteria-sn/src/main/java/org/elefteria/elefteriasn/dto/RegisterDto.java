package org.elefteria.elefteriasn.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RegisterDto {
    @NotNull
    @NotBlank
    @Size(min = 2, max = 60)
    private String username;
    @NotNull
    @NotBlank
    @Size(min = 2, max = 60)
    private String password;
    @NotNull
    @Email
    private String email;
}
