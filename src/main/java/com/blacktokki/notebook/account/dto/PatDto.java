package com.blacktokki.notebook.account.dto;

import java.time.LocalDateTime;

public record PatDto(Long id, String description, LocalDateTime expired) {
}