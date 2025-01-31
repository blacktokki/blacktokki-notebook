package com.blacktokki.spreadocs.core.security;

import java.io.IOException;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
 
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
 
@Component
public class AuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
 
	Logger logger = Logger.getGlobal();

	// private UserService userService;
	
	@Override // disable redirect
	protected void handle(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws ServletException, IOException {
    	logger.info("login success");
    	// userService.clearFailureCount(LoginUser.getId());
    	// userService.visit(LoginUser.getId());
    	super.onAuthenticationSuccess(request, response, authentication);
    }
 
}