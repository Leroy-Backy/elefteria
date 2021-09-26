package org.elefteria.elefteriasn.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import org.elefteria.elefteriasn.security.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;

public class UsernameAndPasswordRequestAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private JwtConfig jwtConfig;
    private SecretKey secretKey;

    @Autowired
    public UsernameAndPasswordRequestAuthenticationFilter(AuthenticationManager authenticationManager, JwtConfig jwtConfig, SecretKey secretKey) {
        this.authenticationManager = authenticationManager;
        this.jwtConfig = jwtConfig;
        this.secretKey = secretKey;
        this.setFilterProcessesUrl("/api/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try{
            // get username and password from request and map it to object
            UsernameAndPasswordRequest authenticationRequest =
                    new ObjectMapper().readValue(request.getInputStream(), UsernameAndPasswordRequest.class);

            // Make new authentication with this object
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    // username is principal
                    authenticationRequest.getUsername(),
                    // password is credentials
                    authenticationRequest.getPassword()
            );

            // authenticate that authentication
            Authentication authenticate = authenticationManager.authenticate(authentication);

            return authenticate;
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }



    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        // Build token
        String token = Jwts.builder()
                .setSubject(authResult.getName())
                .claim("authorities", authResult.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(jwtConfig.getTokenExpirationAfterDays())))
                .signWith(secretKey)
                .compact();

        // This header is necessary, because without it Angular don't see header
        response.addHeader("Access-Control-Expose-Headers", jwtConfig.getAuthorizationHeader());
        // Put token into header
        response.addHeader(jwtConfig.getAuthorizationHeader(), jwtConfig.getTokenPrefix() + token);

        response.setHeader("Username", authResult.getName());
        response.addHeader("Access-Control-Expose-Headers", "Username");
    }
}
