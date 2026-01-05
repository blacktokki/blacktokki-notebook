package com.blacktokki.notebook.core.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
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
            Claims body = parser.parseClaimsJws(token).getBody();
            String subject = body.getSubject();
            UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
            Collection<GrantedAuthority> authorities = new ArrayList<>(userDetails.getAuthorities());
            if (body.containsKey("roles")) {
                for (Object key :body.get("roles", List.class)){
                    authorities.add(new SimpleGrantedAuthority(key.toString().toUpperCase()));
                }
            }
            return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}