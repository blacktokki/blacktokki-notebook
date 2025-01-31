package com.blacktokki.spreadocs.core.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface CustomUserDetailsService extends UserDetailsService{
    UserDetails createGuestUser(String username);
}
