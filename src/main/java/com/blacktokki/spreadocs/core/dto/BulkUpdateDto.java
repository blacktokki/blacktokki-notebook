package com.blacktokki.spreadocs.core.dto;


import java.util.List;


public record BulkUpdateDto<T, ID>(List<ID> ids, T updated) {
}