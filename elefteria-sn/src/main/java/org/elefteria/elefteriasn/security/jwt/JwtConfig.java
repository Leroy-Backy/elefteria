package org.elefteria.elefteriasn.security.jwt;

import com.google.common.net.HttpHeaders;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.jwt")
@Getter
@Setter
public class JwtConfig {

    private String secretKey;
    private String tokenPrefix;
    private Integer tokenExpirationAfterDays;
    private Integer registrationTokenExpirationMinutes;

    public JwtConfig(){}

    public String getAuthorizationHeader(){
        return HttpHeaders.AUTHORIZATION;
    }
}
