package com.blacktokki.spreadocs.core.security;


import com.blacktokki.spreadocs.core.service.CustomUserDetailsService;

import org.springframework.transaction.annotation.Transactional;



import org.springframework.security.authentication.AuthenticationProvider;
// import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class AuthProvider implements AuthenticationProvider  {
    private final CustomUserDetailsService service;

    private final PasswordEncoder passwordEncoder;

    public AuthProvider(CustomUserDetailsService service, PasswordEncoder passwordEncoder) {
        this.service = service;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String)authentication.getCredentials();
        Boolean isGuestCreate = username.equals("guest") && password.equals("guest");
        UserDetails user = service.loadUserByUsername(username);
        if (isGuestCreate){
            WebAuthenticationDetails details = (WebAuthenticationDetails)authentication.getDetails();
            username = details.getSessionId().substring(0, 10) + "@*.guest";
            user =  service.loadUserByUsername(username);
            if(user == null){
                user = service.createGuestUser(username);
            }  
        }
        // id에 맞는 user가 없거나 비밀번호가 맞지 않는 경우.
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
        	throw new UsernameNotFoundException("Unregistered user or incorrect password.");
        }    
        return new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
    }
    
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
