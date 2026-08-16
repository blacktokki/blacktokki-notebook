package com.blacktokki.notebook.mcp;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import com.blacktokki.notebook.content.dto.ContentDto;
import com.blacktokki.notebook.content.dto.ContentQueryParam;
import com.blacktokki.notebook.content.entity.ContentOption;
import com.blacktokki.notebook.content.entity.ContentType;
import com.blacktokki.notebook.content.service.ContentService;
import com.blacktokki.notebook.core.dto.AuthenticateDto;
import com.blacktokki.notebook.core.service.UtilService;
import com.blacktokki.notebook.mcp.tool.NotebookMcpTools;

@ExtendWith(MockitoExtension.class)
class NotebookMcpToolsTest {

    @Mock
    private ContentService contentService;

    @Mock
    private UtilService utilService;

    @InjectMocks
    private NotebookMcpTools tools;

    private final AuthenticateDto mockUser = new AuthenticateDto(1L, "testuser", "Test User");

    @BeforeEach
    void setUp() {
        when(utilService.getUser()).thenReturn(mockUser);
    }

    @Test
    @DisplayName("list_workspace_items: ContentService.getList(QueryService)를 통한 조회 성공")
    void testListWorkspaceItems() {
        ContentDto noteDto = new ContentDto(
                100L,
                1L,
                null,
                ContentType.NOTE,
                0,
                "Sample Note",
                "Description",
                new ContentOption.Map(),
                null
        );

        when(contentService.getList(any(ContentQueryParam.class), any(Sort.class)))
                .thenReturn(List.of(noteDto));

        String resultJson = tools.listWorkspaceItems(0L, null);

        assertNotNull(resultJson);
        assertTrue(resultJson.contains("Sample Note"));
        assertTrue(resultJson.contains("\"status\":\"success\""));
    }

    @Test
    @DisplayName("read_note_content: ContentService.getOptional(QueryService)를 통한 단건 조회 성공")
    void testReadNoteContent() {
        ContentDto noteDto = new ContentDto(
                100L,
                1L,
                0L,
                ContentType.NOTE,
                0,
                "Sample Note",
                "Hello World Markdown",
                new ContentOption.Map(),
                null
        );

        when(contentService.getOptional(100L))
                .thenReturn(Optional.of(noteDto));

        String resultJson = tools.readNoteContent(100L);

        assertNotNull(resultJson);
        assertTrue(resultJson.contains("Hello World Markdown"));
        assertTrue(resultJson.contains("\"status\":\"success\""));
    }

    @Test
    @DisplayName("search_notes_by_keyword: ContentService.getList(QueryService)를 통한 키워드 검색 성공")
    void testSearchNotesByKeyword() {
        ContentDto noteDto = new ContentDto(
                100L,
                1L,
                0L,
                ContentType.NOTE,
                0,
                "Spring AI Guide",
                "MCP server guide content",
                new ContentOption.Map(),
                null
        );

        when(contentService.getList(any(ContentQueryParam.class), any(Sort.class)))
                .thenReturn(List.of(noteDto));

        String resultJson = tools.searchNotesByKeyword("guide", null, 0, 10);

        assertNotNull(resultJson);
        assertTrue(resultJson.contains("Spring AI Guide"));
        assertTrue(resultJson.contains("\"status\":\"success\""));
    }

    @Test
    @DisplayName("create_note: ContentService.create(CommandService)를 통한 생성 성공")
    void testCreateNote() {
        ContentDto createdDto = new ContentDto(
                200L,
                1L,
                null,
                ContentType.NOTE,
                0,
                "New Note Title",
                "New Content",
                new ContentOption.Map(),
                null
        );

        when(contentService.create(any(ContentDto.class))).thenReturn(createdDto);

        String resultJson = tools.createNote("New Note Title", "New Content", 0L, "NOTE");

        assertNotNull(resultJson);
        assertTrue(resultJson.contains("New Note Title"));
        assertTrue(resultJson.contains("\"id\":200"));
        assertTrue(resultJson.contains("\"status\":\"success\""));
    }

    @Test
    @DisplayName("update_note_content: ContentService.update(CommandService)를 통한 수정 성공")
    void testUpdateNoteContent() {
        ContentDto existing = new ContentDto(
                100L,
                1L,
                null,
                ContentType.NOTE,
                0,
                "Old Title",
                "Old Content",
                new ContentOption.Map(),
                null
        );

        when(contentService.getOptional(100L)).thenReturn(Optional.of(existing));

        ContentDto updatedDto = new ContentDto(
                100L,
                1L,
                null,
                ContentType.NOTE,
                0,
                "Updated Title",
                "Updated Content",
                new ContentOption.Map(),
                null
        );

        when(contentService.update(eq(100L), any(ContentDto.class))).thenReturn(updatedDto);

        String resultJson = tools.updateNoteContent(100L, "Updated Title", "Updated Content");

        assertNotNull(resultJson);
        assertTrue(resultJson.contains("Updated Title"));
        assertTrue(resultJson.contains("\"status\":\"success\""));
    }

    @Test
    @DisplayName("delete_note: ContentService.bulkDelete(CommandService)를 통한 삭제 성공")
    void testDeleteNote() {
        ContentDto existing = new ContentDto(
                100L,
                1L,
                null,
                ContentType.NOTE,
                0,
                "Note To Delete",
                "",
                new ContentOption.Map(),
                null
        );

        when(contentService.getOptional(100L)).thenReturn(Optional.of(existing));

        String resultJson = tools.deleteNote(100L);

        assertNotNull(resultJson);
        assertTrue(resultJson.contains("\"status\":\"success\""));
        verify(contentService).bulkDelete(List.of(100L));
    }
}
