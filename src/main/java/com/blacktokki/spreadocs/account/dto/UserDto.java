package com.blacktokki.spreadocs.account.dto;

import com.blacktokki.spreadocs.core.dto.BaseUserDto;

public record UserDto(Long id, String username, String name) implements BaseUserDto{
}