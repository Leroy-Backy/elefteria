package org.elefteria.elefteriasn.service;

import org.elefteria.elefteriasn.dao.UserRepository;
import org.elefteria.elefteriasn.entity.Role;
import org.elefteria.elefteriasn.entity.User;
import org.elefteria.elefteriasn.security.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    Logger logger = LoggerFactory.getLogger(getClass().getName());

    @Autowired
    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return formatUserToUserDetails(username);
    }

    // Get User from repository and transform it to userDetails for security
    private UserDetails formatUserToUserDetails(String username){
        Optional<User> userOptional = this.userRepository.findByUsername(username);

        if(userOptional.isEmpty()) {
            logger.warn("User " + username + " not found!");
            throw new UsernameNotFoundException("User " + username + " not found!");
        }
        User user = userOptional.get();

        return buildUserDetails(user);
    }

    // Build UserDetails object
    private UserDetails buildUserDetails(User user){
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        for(Role role: user.getRoles())
            UserRole.getAuthoritiesByName(role.getName()).forEach(authority -> authorities.add(authority));

        UserDetails userDetails
                = org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(!user.isAccountNonExpired())
                .accountLocked(!user.isAccountNonLocked())
                .credentialsExpired(!user.isCredentialsNonExpired())
                .disabled(!user.isEnabled())
                .build();

        return userDetails;
    }
}
