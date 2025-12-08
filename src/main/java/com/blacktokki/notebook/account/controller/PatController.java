package com.blacktokki.notebook.account.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.blacktokki.notebook.account.entity.PersonalAccessToken;
import com.blacktokki.notebook.account.service.PatService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pat")
@RequiredArgsConstructor
public class PatController {

    private final PatService patService;

    // 1. PAT 생성
    @PostMapping("")
    public ResponseEntity<String> createPat() {
        String token = patService.issueToken();
        return ResponseEntity.ok(token);
    }

    // 2. PAT 목록 조회
    @GetMapping("")
    public ResponseEntity<List<PersonalAccessToken>> getPats() {
        return ResponseEntity.ok(patService.getMyTokens());
    }

    // 3. PAT 삭제 (Revoke)
    @DeleteMapping("/{patId}")
    public ResponseEntity<Void> deletePat(@PathVariable Long patId) {
        patService.deleteToken(patId);
        return ResponseEntity.noContent().build();
    }
}