package com.blacktokki.notebook.core.security;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.blacktokki.notebook.core.dto.BaseUserDto;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.pat-secret}")
    private String patSecretKey;

    private JwtParser parser;

    private JwtParser patParser;

    private final UserDetailsService userDetailsService;

    private final BasePatRepository patRepository;

    public JwtTokenProvider(UserDetailsService userDetailsService, BasePatRepository patRepository) {
        this.userDetailsService = userDetailsService;
        this.patRepository = patRepository;
    }

    @PostConstruct
    private void init() {
        parser = Jwts.parserBuilder().setSigningKey(secretKey.getBytes()).build();
        patParser = Jwts.parserBuilder().setSigningKey(patSecretKey.getBytes()).build();
        // warmup 처리
        try {
            parser.parseClaimsJws("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ5ZGgwNTE1NDFAZ21haWwuY29tIiwib3JpZ19pYXQiOjE3NjIwODEwMjA2NjQsImlhdCI6MTc2MjA4MzU0NSwiZXhwIjoxNzYyMDg1MzQ1fQ.BZ7I_OsbcW12tTTF6bcrLXWpraih4hyA1mp54ddDglY");
        } catch (JwtException e) {
        }
        try {
            patParser.parseClaimsJws("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ5ZGgwNTE1NDFAZ21haWwuY29tIiwib3JpZ19pYXQiOjE3NjIwODEwMjA2NjQsImlhdCI6MTc2MjA4MzU0NSwiZXhwIjoxNzYyMDg1MzQ1fQ.BZ7I_OsbcW12tTTF6bcrLXWpraih4hyA1mp54ddDglY");
        } catch (JwtException e) {
        }
        try {
            userDetailsService.loadUserByUsername("");
        } catch (UsernameNotFoundException e) {
        }
    }
    
    private UserDetails getUserDetails(String token) {
        try {
            Claims claims = patParser.parseClaimsJws(token).getBody();
            Long id = patRepository.getUserId(claims);
            UserDetails userDetails = userDetailsService.loadUserByUsername(claims.getSubject());
            if (((BaseUserDto)userDetails).id() != id) {
                return null;
            }
            return userDetails;
        }
        catch (JwtException e) {
            String subject = parser.parseClaimsJws(token).getBody().getSubject();
            return userDetailsService.loadUserByUsername(subject);
        }
    }

    public Authentication getAuthentication(String token) {
        try {
            UserDetails userDetails = getUserDetails(token);
            if (userDetails == null) {
                return null;
            }
            return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        }
        catch(Exception e) {
            return null;    
        }
    }

    public String createPatToken(Long id, String username, Date expireation) {
        Claims claims = Jwts.claims().setSubject(username).setId(id.toString()); // JWT payload 에 저장되는 정보단위, 보통 여기서 user를 식별하는 값을 넣는다.
        Date now = new Date();
        Key patKey = Keys.hmacShaKeyFor(patSecretKey.getBytes());
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(expireation) // set Expire Time
                .signWith(patKey)  // 사용할 암호화 알고리즘과
                // signature 에 들어갈 secret값 세팅
                .compact();
    }
}