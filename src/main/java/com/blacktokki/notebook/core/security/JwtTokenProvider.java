package com.blacktokki.notebook.core.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;

    private JwtParser parser;

    private final UserDetailsService userDetailsService;

    public JwtTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    protected void init() {
        parser = Jwts.parserBuilder().setSigningKey(secretKey.getBytes()).build();
        // warmup 처리
        try {
            parser.parseClaimsJws("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ5ZGgwNTE1NDFAZ21haWwuY29tIiwib3JpZ19pYXQiOjE3NjIwODEwMjA2NjQsImlhdCI6MTc2MjA4MzU0NSwiZXhwIjoxNzYyMDg1MzQ1fQ.BZ7I_OsbcW12tTTF6bcrLXWpraih4hyA1mp54ddDglY");
        } catch (JwtException e) {
        }
        try {
            userDetailsService.loadUserByUsername("");
        } catch (UsernameNotFoundException e) {
        }
    }

    public Authentication getAuthentication(String token) {
        try {
            String subject = parser.parseClaimsJws(token).getBody().getSubject();
            UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
            return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}