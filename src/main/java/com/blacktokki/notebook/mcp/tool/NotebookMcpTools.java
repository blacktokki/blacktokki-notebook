package com.blacktokki.notebook.mcp.tool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.blacktokki.notebook.content.dto.ContentDto;
import com.blacktokki.notebook.content.dto.ContentQueryParam;
import com.blacktokki.notebook.content.entity.ContentOption;
import com.blacktokki.notebook.content.entity.ContentType;
import com.blacktokki.notebook.content.service.ContentService;
import com.blacktokki.notebook.core.dto.BaseUserDto;
import com.blacktokki.notebook.core.service.UtilService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotebookMcpTools {

    private final ContentService contentService;
    private final UtilService utilService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Long getCurrentUserId() {
        BaseUserDto user = utilService.getUser();
        if (user == null || user.id() == null) {
            throw new IllegalStateException("인증된 사용자 정보를 찾을 수 없습니다.");
        }
        return user.id();
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("JSON 직렬화 실패: {}", e.getMessage());
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }

    @Tool(
        name = "list_workspace_items",
        description = "현재 사용자의 최상위 또는 특정 상위 폴더(parentId) 내에 있는 노트북, 노트, 보드, 북마크 목록을 조회합니다."
    )
    public String listWorkspaceItems(
        @ToolParam(description = "상위 항목 ID (최상위 루트 조회 시 null 또는 0 입력)", required = false) Long parentId,
        @ToolParam(description = "필터링할 컨텐츠 타입 (NOTE, NOTEBOOK, BOARD, BOOKMARK 등 / 생략 시 전체)", required = false) String type
    ) {
        try {
            Long targetParentId = (parentId != null && parentId > 0) ? parentId : null;

            List<ContentType> types = null;
            if (type != null && !type.isBlank()) {
                try {
                    types = List.of(ContentType.valueOf(type.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    return toJson(Map.of("error", "유효하지 않은 컨텐츠 타입입니다: " + type));
                }
            }

            ContentQueryParam queryParam = new ContentQueryParam(types, targetParentId, false);
            List<ContentDto> contents = contentService.getList(queryParam, Sort.by(Sort.Direction.ASC, "order"));

            // 루트 조회 시 (targetParentId == null) parentId가 null 또는 0인 항목만 필터링
            if (targetParentId == null) {
                contents = contents.stream()
                        .filter(c -> c.parentId() == null || c.parentId() == 0L)
                        .toList();
            }

            List<Map<String, Object>> items = contents.stream().map(c -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", c.id());
                map.put("title", c.title());
                map.put("type", c.type() != null ? c.type().name() : null);
                map.put("parentId", c.parentId());
                map.put("order", c.order());
                map.put("updated", c.updated() != null ? c.updated().toString() : null);
                return map;
            }).toList();

            return toJson(Map.of(
                "status", "success",
                "count", items.size(),
                "parentId", targetParentId != null ? targetParentId : 0,
                "items", items
            ));
        } catch (Exception e) {
            log.error("list_workspace_items 실행 오류", e);
            return toJson(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @Tool(
        name = "read_note_content",
        description = "특정 노트의 상세 본문 내용(HTML/마크다운)과 메타데이터(제목, 생성/수정일, 옵션)를 조회합니다."
    )
    public String readNoteContent(
        @ToolParam(description = "조회할 노트의 고유 ID", required = true) Long noteId
    ) {
        try {
            Long userId = getCurrentUserId();
            Optional<ContentDto> contentOpt = contentService.getOptional(noteId);

            if (contentOpt.isEmpty() || !userId.equals(contentOpt.get().userId())) {
                return toJson(Map.of("status", "error", "message", "해당 ID의 노트를 찾을 수 없거나 접근 권한이 없습니다. ID: " + noteId));
            }

            ContentDto c = contentOpt.get();
            Map<String, Object> result = new HashMap<>();
            result.put("status", "success");
            result.put("id", c.id());
            result.put("title", c.title());
            result.put("type", c.type() != null ? c.type().name() : null);
            result.put("parentId", c.parentId());
            result.put("content", c.description());
            result.put("option", c.option());
            result.put("updated", c.updated() != null ? c.updated().toString() : null);

            return toJson(result);
        } catch (Exception e) {
            log.error("read_note_content 실행 오류", e);
            return toJson(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @Tool(
        name = "search_notes_by_keyword",
        description = "키워드를 검색하여 제목이나 본문에 해당 키워드가 포함된 노트 목록을 찾습니다."
    )
    public String searchNotesByKeyword(
        @ToolParam(description = "검색할 단어 또는 문장", required = true) String query,
        @ToolParam(description = "필터링할 컨텐츠 타입 (NOTE, NOTEBOOK 등 / 생략 시 전체)", required = false) String type,
        @ToolParam(description = "페이지 번호 (0부터 시작, 기본값: 0)", required = false) Integer page,
        @ToolParam(description = "가져올 결과 개수 (기본값: 20)", required = false) Integer size
    ) {
        try {
            int pageNum = (page != null && page >= 0) ? page : 0;
            int pageSize = (size != null && size > 0) ? size : 20;

            List<ContentType> types = null;
            if (type != null && !type.isBlank()) {
                try {
                    types = List.of(ContentType.valueOf(type.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    return toJson(Map.of("error", "유효하지 않은 컨텐츠 타입입니다: " + type));
                }
            }

            ContentQueryParam queryParam = new ContentQueryParam(types, null, false);
            List<ContentDto> allContents = contentService.getList(queryParam, Sort.by(Sort.Direction.DESC, "updated"));

            String lowerQuery = (query != null) ? query.toLowerCase() : "";
            List<ContentDto> matched = allContents.stream()
                    .filter(c -> (c.title() != null && c.title().toLowerCase().contains(lowerQuery))
                            || (c.description() != null && c.description().toLowerCase().contains(lowerQuery)))
                    .toList();

            int total = matched.size();
            int fromIndex = Math.min(pageNum * pageSize, total);
            int toIndex = Math.min(fromIndex + pageSize, total);
            List<ContentDto> paged = matched.subList(fromIndex, toIndex);

            List<Map<String, Object>> items = paged.stream().map(c -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", c.id());
                map.put("title", c.title());
                map.put("type", c.type() != null ? c.type().name() : null);
                map.put("parentId", c.parentId());
                map.put("updated", c.updated() != null ? c.updated().toString() : null);
                String desc = c.description();
                if (desc != null && desc.length() > 200) {
                    map.put("preview", desc.substring(0, 200) + "...");
                } else {
                    map.put("preview", desc);
                }
                return map;
            }).toList();

            return toJson(Map.of(
                "status", "success",
                "query", query,
                "totalElements", total,
                "totalPages", (pageSize > 0) ? (int) Math.ceil((double) total / pageSize) : 1,
                "currentPage", pageNum,
                "items", items
            ));
        } catch (Exception e) {
            log.error("search_notes_by_keyword 실행 오류", e);
            return toJson(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @Tool(
        name = "create_note",
        description = "새로운 노트 또는 컨텐츠를 생성합니다."
    )
    public String createNote(
        @ToolParam(description = "노트 제목", required = true) String title,
        @ToolParam(description = "노트 본문 내용", required = true) String content,
        @ToolParam(description = "상위 폴더/노트북 ID (최상위 루트는 null 또는 0)", required = false) Long parentId,
        @ToolParam(description = "컨텐츠 타입 (NOTE, NOTEBOOK, BOARD, BOOKMARK 등 / 기본값: NOTE)", required = false) String type
    ) {
        try {
            Long userId = getCurrentUserId();
            ContentType contentType = ContentType.NOTE;
            if (type != null && !type.isBlank()) {
                try {
                    contentType = ContentType.valueOf(type.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return toJson(Map.of("error", "유효하지 않은 컨텐츠 타입입니다: " + type));
                }
            }

            Long targetParentId = (parentId != null && parentId > 0) ? parentId : null;
            ContentDto newDto = new ContentDto(
                null,
                userId,
                targetParentId,
                contentType,
                0,
                title,
                content,
                new ContentOption.Map(),
                null
            );

            ContentDto created = contentService.create(newDto);
            return toJson(Map.of(
                "status", "success",
                "message", "노트가 성공적으로 생성되었습니다.",
                "id", created.id(),
                "title", created.title(),
                "parentId", created.parentId() != null ? created.parentId() : 0,
                "type", created.type().name()
            ));
        } catch (Exception e) {
            log.error("create_note 실행 오류", e);
            return toJson(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @Tool(
        name = "update_note_content",
        description = "기존 노트의 제목이나 본문 내용을 수정합니다. 이전 이력은 시스템 스냅샷/델타로 자동 보존됩니다."
    )
    public String updateNoteContent(
        @ToolParam(description = "수정할 노트의 고유 ID", required = true) Long noteId,
        @ToolParam(description = "새로운 노트 제목 (변경하지 않을 경우 null)", required = false) String title,
        @ToolParam(description = "새로운 노트 본문 내용 (변경하지 않을 경우 null)", required = false) String content
    ) {
        try {
            Long userId = getCurrentUserId();
            Optional<ContentDto> existingOpt = contentService.getOptional(noteId);

            if (existingOpt.isEmpty() || !userId.equals(existingOpt.get().userId())) {
                return toJson(Map.of("status", "error", "message", "수정할 노트를 찾을 수 없거나 접근 권한이 없습니다. ID: " + noteId));
            }

            ContentDto existing = existingOpt.get();
            String newTitle = (title != null && !title.isBlank()) ? title : existing.title();
            String newDescription = (content != null) ? content : existing.description();

            ContentDto updateDto = new ContentDto(
                existing.id(),
                existing.userId(),
                existing.parentId(),
                existing.type(),
                existing.order(),
                newTitle,
                newDescription,
                existing.option(),
                existing.updated()
            );

            ContentDto updated = contentService.update(noteId, updateDto);
            return toJson(Map.of(
                "status", "success",
                "message", "노트가 성공적으로 수정되었습니다.",
                "id", updated.id(),
                "title", updated.title(),
                "updated", updated.updated() != null ? updated.updated().toString() : null
            ));
        } catch (Exception e) {
            log.error("update_note_content 실행 오류", e);
            return toJson(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @Tool(
        name = "delete_note",
        description = "지정한 노트를 삭제(소프트 딜리트)합니다."
    )
    public String deleteNote(
        @ToolParam(description = "삭제할 노트의 고유 ID", required = true) Long noteId
    ) {
        try {
            Long userId = getCurrentUserId();
            Optional<ContentDto> existingOpt = contentService.getOptional(noteId);

            if (existingOpt.isEmpty() || !userId.equals(existingOpt.get().userId())) {
                return toJson(Map.of("status", "error", "message", "삭제할 노트를 찾을 수 없거나 접근 권한이 없습니다. ID: " + noteId));
            }

            contentService.bulkDelete(List.of(noteId));
            return toJson(Map.of(
                "status", "success",
                "message", "노트(ID: " + noteId + ")가 성공적으로 삭제되었습니다.",
                "id", noteId
            ));
        } catch (Exception e) {
            log.error("delete_note 실행 오류", e);
            return toJson(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}
