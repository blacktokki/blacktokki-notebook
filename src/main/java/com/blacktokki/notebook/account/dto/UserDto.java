package com.blacktokki.notebook.account.dto;

import com.blacktokki.notebook.core.dto.BaseUserDto;

public record UserDto(Long id, String username, String name) implements BaseUserDto{
}