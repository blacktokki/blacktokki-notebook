package com.blacktokki.notebook.account.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blacktokki.notebook.account.entity.PersonalAccessToken;

@Repository
public interface PatRepository extends JpaRepository<PersonalAccessToken, Long> {
    public List<PersonalAccessToken> findAllByUserId(Long userId);

    public Optional<PersonalAccessToken> findOneByToken(String token);
}