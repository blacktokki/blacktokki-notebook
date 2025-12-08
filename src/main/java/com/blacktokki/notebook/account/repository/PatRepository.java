package com.blacktokki.notebook.account.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.jsonwebtoken.Claims;

import com.blacktokki.notebook.account.entity.PersonalAccessToken;
import com.blacktokki.notebook.core.security.BasePatRepository;

@Repository
public interface PatRepository extends JpaRepository<PersonalAccessToken, Long>, BasePatRepository {
    public List<PersonalAccessToken> findAllByUserId(Long userId);

    @Override
    default Long getUserId(Claims claims) {
        return this.findById(Long.parseLong(claims.getId())).map(v->v.getId()).orElse(null);
    }
}