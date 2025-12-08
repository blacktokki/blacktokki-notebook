package com.blacktokki.notebook.core.security;

import io.jsonwebtoken.Claims;

public interface BasePatRepository {
    public Long getUserId(Claims claims);
}
