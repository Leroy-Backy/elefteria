package org.elefteria.elefteriasn.security.jwt;

import com.google.common.base.Strings;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class JwtTokenVerifier extends OncePerRequestFilter {

    private JwtConfig jwtConfig;
    private final SecretKey secretKey;

    @Autowired
    public JwtTokenVerifier(JwtConfig jwtConfig, SecretKey secretKey) {
        this.jwtConfig = jwtConfig;
        this.secretKey = secretKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Get token from request header
        String authHeader = httpServletRequest.getHeader(jwtConfig.getAuthorizationHeader());

        // Check if token is there
        if(Strings.isNullOrEmpty(authHeader) || !authHeader.startsWith(jwtConfig.getTokenPrefix())){
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        // Remove prefix from token
        String token = authHeader.replace(jwtConfig.getTokenPrefix(), "");

        Authentication authentication = getAuthenticationFromToken(token, secretKey);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Send to the next filter
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    // this method parse jwt token and return authentication object made with values from jwt token
    public static Authentication getAuthenticationFromToken(String token, SecretKey secretKey){
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            Claims body = claimsJws.getBody();

            String username = body.getSubject();

            var authorities = (List<Map<String, String>>) body.get("authorities");

            // Get authorities from token
            Set<SimpleGrantedAuthority> authoritySet = authorities.stream()
                    .map(m -> new SimpleGrantedAuthority(m.get("authority")))
                    .collect(Collectors.toSet());

            // Set Authentication
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    authoritySet
            );

            return authentication;
        } catch (JwtException e){
            throw new IllegalStateException("Token " + token + " cannot be truest");
        }
    }
}





















