package org.elefteria.elefteriasn.security.jwt;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsernameAndPasswordRequest {

    private String username;
    private String password;

    public UsernameAndPasswordRequest(){}
}
