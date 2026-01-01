package com.blacktokki.notebook.core.security;

import org.springframework.security.core.Authentication;

public interface PatProvider {
    public String getPrefix();
    public Authentication getAuthentication(String token);
}
