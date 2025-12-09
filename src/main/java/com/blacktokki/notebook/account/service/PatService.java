package com.blacktokki.notebook.account.service;
import lombok.RequiredArgsConstructor;

import org.bouncycastle.util.encoders.Hex;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blacktokki.notebook.account.entity.PersonalAccessToken;
import com.blacktokki.notebook.account.entity.User;
import com.blacktokki.notebook.account.repository.PatRepository;
import com.blacktokki.notebook.core.dto.AuthenticateDto;
import com.blacktokki.notebook.core.dto.BaseUserDto;
import com.blacktokki.notebook.core.security.PatProvider;
import com.blacktokki.notebook.core.service.UtilService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatService implements PatProvider {
    private final String PREFIX = "pat_";
    private final SecureRandom secureRandom = new SecureRandom();
    private final Base64.Encoder base64Encoder = Base64.getUrlEncoder();
    private final long tokenValidTime = 14 * 24 * 60 * 60 * 1000L;
    private final PatRepository patRepository;
    private final UtilService utilService;

    @Transactional
    public String issueToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        String token = PREFIX + base64Encoder.encodeToString(randomBytes).replace("=", "");
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + tokenValidTime);
        BaseUserDto user = utilService.getUser();

        PersonalAccessToken pat = PersonalAccessToken.builder()
                .userId(user.id())
                .name(user.name() + '(' + now.toInstant().toString() + ')')
                .token(this.sha256(token))
                .expirationDate(LocalDateTime.ofInstant(expireDate.toInstant(), ZoneId.of("Asia/Seoul")))
                .build();
        patRepository.save(pat);
        return token;
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

    @Override
    public Authentication getAuthentication(String token){
        Optional<PersonalAccessToken> pat = patRepository.findOneByToken(this.sha256(token));
        if (pat.isEmpty()) {
            return null;
        }
        User user = pat.get().getUser();
        AuthenticateDto userDetails = new AuthenticateDto(user.getId(), user.getUsername(), user.getPassword(), user.getName());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private String sha256(String original) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(original.getBytes(StandardCharsets.UTF_8));
            return Hex.toHexString(hash); 
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}