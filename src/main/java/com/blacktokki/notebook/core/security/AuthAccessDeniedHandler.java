package com.blacktokki.notebook.core.security;

// import com.blacktokki.feedynote.entity.User;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class AuthAccessDeniedHandler implements AccessDeniedHandler {
	 
    @Override
    public void handle(HttpServletRequest request,HttpServletResponse response, AccessDeniedException exc) throws IOException, ServletException {
    }
}