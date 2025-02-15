package com.blacktokki.feedynote.account.dto;

import com.blacktokki.feedynote.core.dto.BaseUserDto;

public record UserDto(Long id, String username, String name) implements BaseUserDto{
}