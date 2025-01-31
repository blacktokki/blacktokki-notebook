package com.blacktokki.spreadocs.core.security;

import java.io.IOException;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
 
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

 
@Component
public class AuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    
	Logger logger = Logger.getGlobal();
	
	// private UserService userService;
	
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
    	logger.info("login fail");
		logger.info(exception.getMessage());
    	// if(exception.getMessage().equals("invailed username")) {
    	// 	userService.addFailureCount(request.getParameter("loginId"));
    	// }
    	String str="/user/login?error";
    	setDefaultFailureUrl(str);
    	super.onAuthenticationFailure(request, response,exception);
    }
}