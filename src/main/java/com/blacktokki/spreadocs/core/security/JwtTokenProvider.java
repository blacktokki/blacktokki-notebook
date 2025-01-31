package com.blacktokki.spreadocs.core.security;

import java.util.Base64;
import java.util.Date;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;

    // 토큰 유효시간 30분
    private long tokenValidTime = 30 * 60 * 1000L;

    // refresh 토큰 유효시간 7일
    private long refreshTokenValidTime = 7 * 24 * 3600 * 1000L;

    private final UserDetailsService userDetailsService;

    public JwtTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // 객체 초기화, secretKey를 Base64로 인코딩한다.
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // JWT 토큰 생성 
    public String createToken(String username, Object roles, Date origIat) {
        Claims claims = Jwts.claims().setSubject(username); // JWT payload 에 저장되는 정보단위, 보통 여기서 user를 식별하는 값을 넣는다.
        Date now = new Date();
        claims.put("roles", roles); // 정보는 key / value 쌍으로 저장된다.
        claims.put("orig_iat", (origIat!=null?origIat:now).getTime());
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + tokenValidTime)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey)  // 사용할 암호화 알고리즘과
                // signature 에 들어갈 secret값 세팅
                .compact();
    }

    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        String subject = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String createRefreshToken(String token) {
        try {
            Jwts.parser()
                .setSigningKey(secretKey) // Set Key
                .parseClaimsJws(token); // 파싱 및 검증, 실패 시 에러
        } catch (ExpiredJwtException e) { // 토큰이 만료되었을 경우
            Claims claims = e.getClaims();
            long origIat = (long)claims.get("orig_iat");
            Date now = new Date();
            Date expire = new Date(origIat + refreshTokenValidTime);
            if (now.before(expire)){
                return createToken(claims.getSubject(), claims.get("roles"), new Date(origIat));
            }
        } catch (Exception e) {
        }
        return "";
    }
}