package com.blacktokki.notebook.account.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blacktokki.notebook.account.entity.PersonalAccessToken;
import com.blacktokki.notebook.account.repository.PatRepository;
import com.blacktokki.notebook.core.dto.BaseUserDto;
import com.blacktokki.notebook.core.security.JwtTokenProvider;
import com.blacktokki.notebook.core.service.UtilService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatService {

    private final PatRepository patRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final long tokenValidTime = 14 * 24 * 60 * 60 * 1000L;
    private final UtilService utilService;

    @Transactional
    public String issueToken() {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + tokenValidTime);
        BaseUserDto user = utilService.getUser();

        PersonalAccessToken pat = PersonalAccessToken.builder()
                .userId(user.id())
                .name(user.name() + '(' + now.toInstant().toString() + ')')
                .expirationDate(LocalDateTime.ofInstant(expireDate.toInstant(), ZoneId.of("Asia/Seoul")))
                .build();
        PersonalAccessToken savedPat = patRepository.save(pat);

        return jwtTokenProvider.createPatToken(savedPat.getId(), user.username(), expireDate);
    }

    public List<PersonalAccessToken> getMyTokens() {
        BaseUserDto user = utilService.getUser();
        return patRepository.findAllByUserId(user.id());
    }

    @Transactional
    public void deleteToken(Long patId) {
        BaseUserDto user = utilService.getUser();
        PersonalAccessToken pat = patRepository.findById(patId)
                .orElseThrow(() -> new IllegalArgumentException("토큰이 존재하지 않습니다."));

        if (!pat.getUserId().equals(user.id())) {
            throw new IllegalArgumentException("본인의 토큰만 삭제할 수 있습니다.");
        }

        patRepository.delete(pat);
    }
}