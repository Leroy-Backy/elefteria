package org.elefteria.elefteriasn.security;

import org.elefteria.elefteriasn.security.jwt.JwtConfig;
import org.elefteria.elefteriasn.security.jwt.JwtTokenVerifier;
import org.elefteria.elefteriasn.security.jwt.UsernameAndPasswordRequestAuthenticationFilter;
import org.elefteria.elefteriasn.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletResponse;

import java.util.Arrays;

import static org.elefteria.elefteriasn.security.UserRole.*;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private PasswordEncoder passwordEncoder;
    private MyUserDetailsService userDetailsService;

    private JwtConfig jwtConfig;
    private SecretKey secretKey;

    @Autowired
    public SecurityConfig(PasswordEncoder passwordEncoder,
                          MyUserDetailsService userDetailsService,
                          JwtConfig jwtConfig,
                          SecretKey secretKey) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.jwtConfig = jwtConfig;
        this.secretKey = secretKey;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and()  // Enable CrossOrigin
                .csrf().disable()  // disable csrf
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)      // make session stateless for jwt
                .and()
                .exceptionHandling()                      // Generate 401 http status exception
                .authenticationEntryPoint(
                        (httpServletRequest, httpServletResponse, e) -> {
                            httpServletResponse.sendError(
                                    HttpServletResponse.SC_UNAUTHORIZED,
                                    e.getMessage()
                            );
                        }
                )
                .and()
                .addFilter(new UsernameAndPasswordRequestAuthenticationFilter(authenticationManager(), jwtConfig, secretKey))    // add filter to login
                .addFilterAfter(new JwtTokenVerifier(jwtConfig, secretKey), UsernameAndPasswordRequestAuthenticationFilter.class)    // add next filter to verify token
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/users").permitAll()
                .antMatchers(HttpMethod.GET, "/api/users/activateUser").permitAll()
                .antMatchers( "/api/users/changePassword").permitAll()
                .antMatchers(HttpMethod.GET,"/api/images/{.+}").permitAll()
                .antMatchers("/ws/**").permitAll()
                .antMatchers("/testLikes/*").permitAll()
                .anyRequest()
                .authenticated();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);

        return provider;
    }
}
