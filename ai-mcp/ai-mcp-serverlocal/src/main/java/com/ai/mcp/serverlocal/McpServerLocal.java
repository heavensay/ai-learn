package com.ai.mcp.serverlocal;

import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class McpServerLocal {
    public static void main(String[] args) {
        StdioServerTransportProvider transportProvider =
                new StdioServerTransportProvider(McpJsonDefaults.getMapper());
        // 定义一个读取文本文件的工具
        McpSchema.Tool fileReadTool = McpSchema.Tool.builder("read_local_file")
                .title("读取本地指定路径的文本文件内容")
                .description("读取本地指定路径的文本文件内容")
                .inputSchema(Map.of(
                        "type", "object",
                        "properties",Map.of("filePath",Map.of("type","string","description","要读取的文件绝对路径")),
                        "required",List.of("filePath")))
                .build();

        // Sync tool specification using builder
        McpServer.sync(transportProvider)
                .serverInfo("McpServerLocal", "1.0.0")
                .capabilities(McpSchema.ServerCapabilities.builder()
                        .resources(false, true)  // Resource support: subscribe=false, listChanged=true
                        .tools(true)             // Enable tool support with list changes
                        .prompts(true)           // Enable prompt support with list changes
                        .completions()           // Enable completions support
                        .logging()               // Enable logging support
                        .build())
                .tools(List.of(
                        new McpServerFeatures.SyncToolSpecification(
                                fileReadTool,
                                (exchange, request) -> {
                                    //模拟read_file处理进度，以logging形式把进度信息反馈给client；
                                    for (int i = 0; i < 10; i++) {
                                        try {
                                            Thread.sleep(1000L);
                                        } catch (InterruptedException e) {
                                            throw new RuntimeException(e);
                                        }
                                        exchange.loggingNotification(
                                                McpSchema.LoggingMessageNotification.builder(
                                                        McpSchema.LoggingLevel.INFO,"当前进度"+(i+1)*10+"%")
                                                        .build()
                                        );
                                    }

                                    // 获取传入的参数
                                    String filePath = (String) request.arguments().get("filePath");
                                    try {
                                        String content = Files.readString(Path.of(filePath));
                                        return McpSchema.CallToolResult.builder().textContent(List.of(content)).build();
                                    } catch (Exception e) {
                                        return McpSchema.CallToolResult.builder().textContent(List.of("读取文件失败: " + e.getMessage())).isError(true).build();
                                    }
                                }
                        )
                ))
                .build();

        System.err.println("mcp server local 启动完成");
    }
}