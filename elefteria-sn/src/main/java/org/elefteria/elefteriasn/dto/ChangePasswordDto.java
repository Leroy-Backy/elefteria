package org.elefteria.elefteriasn.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordDto {
    private String password;
    private String confirmPassword;
    private String token;
}
