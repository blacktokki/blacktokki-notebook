package com.blacktokki.notebook.mcp.config;

import org.springframework.ai.tool.ToolCallbackProvider;
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
}
