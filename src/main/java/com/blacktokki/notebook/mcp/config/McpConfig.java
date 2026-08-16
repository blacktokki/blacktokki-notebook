package com.blacktokki.notebook.mcp.config;

import java.util.List;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.blacktokki.notebook.mcp.tool.NotebookMcpTools;

@Configuration
public class McpConfig {

    @Bean
    public ToolCallbackProvider notebookToolCallbackProvider(NotebookMcpTools notebookMcpTools) {
        return MethodToolCallbackProvider.builder().toolObjects(notebookMcpTools).build();
    }

    @Bean
    public List<ToolCallback> notebookToolCallbacks(NotebookMcpTools notebookMcpTools) {
        return List.of(ToolCallbacks.from(notebookMcpTools));
    }
}
