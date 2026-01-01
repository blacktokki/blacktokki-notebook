package com.blacktokki.notebook.core.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;


public class AuthenticationFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;
    private final PatProvider patProvider;

    public AuthenticationFilter(JwtTokenProvider jwtTokenProvider, PatProvider patProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.patProvider = patProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 헤더에서 JWT 를 받아옵니다.
        String authorization = ((HttpServletRequest) request).getHeader("Authorization");
        if (authorization != null){
            String[] authorizationSplits = authorization.split(" ");
            String token = authorizationSplits.length == 2 ? authorizationSplits[1] : "";
            
            // 토큰이 유효하면 토큰으로부터 유저 정보를 받아옵니다.
            Authentication authentication = null;
            if (token.startsWith(patProvider.getPrefix())) {
                authentication = patProvider.getAuthentication(token);
            }
            if (authentication == null) {
                authentication = jwtTokenProvider.getAuthentication(token);
            }
            // 유효한 토큰인지 확인합니다.
            if (authentication != null) {    
                // SecurityContext 에 Authentication 객체를 저장합니다.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }
}