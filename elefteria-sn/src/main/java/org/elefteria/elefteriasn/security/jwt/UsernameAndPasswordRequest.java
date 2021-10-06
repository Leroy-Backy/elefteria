package org.elefteria.elefteriasn.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UsernameAndPasswordRequest {

    private String username;
    private String password;

    public UsernameAndPasswordRequest(){}


}
