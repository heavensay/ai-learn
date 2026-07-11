package com.ai.mcp.serverspring;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpStreamableServerTransportProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Configuration
public class McpFeatureConfig {

    @Bean
    public McpSyncServer mcpServer(
            McpStreamableServerTransportProvider transportProvider) {
        // 定义一个读取文本文件的工具
        McpSchema.Tool fileReadTool = McpSchema.Tool.builder("read_file")
                .title("读取指定路径的文本文件内容")
                .description("读取指定路径的文本文件内容")
                .inputSchema(Map.of(
                        "type", "object",
                        "properties",Map.of("filePath",Map.of("type","string","description","要读取的文件绝对路径")),
                        "required",List.of("filePath")))
                .build();

        // Sync tool specification using builder
        return McpServer.sync(transportProvider)
                .serverInfo("MySpringMcpServer", "1.0.0")
                .capabilities(McpSchema.ServerCapabilities.builder()
                        .resources(false, true)  // Resource support: subscribe=false, listChanged=true
                        .tools(true)             // Enable tool support with list changes
                        .prompts(true)           // Enable prompt support with list changes
                        .completions()           // Enable completions support
                        .logging()               // Enable logging support
                        .build())
                .tools(List.of(
                        //tool：用于读取server端文件
                        new McpServerFeatures.SyncToolSpecification(
                                fileReadTool,
                                (exchange, args) -> {
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
                                    String filePath = (String) args.arguments().get("filePath");
                                    try {
                                        String content = Files.readString(Path.of(filePath));
                                        return McpSchema.CallToolResult.builder().textContent(List.of(content)).build();
                                    } catch (Exception e) {
                                        return McpSchema.CallToolResult.builder().textContent(List.of("读取文件失败: " + e.getMessage())).isError(true).build();
                                    }
                                }
                        )))
                .resources(List.of(new McpServerFeatures.SyncResourceSpecification(
                        McpSchema.Resource.builder("file:///Users/liyu/Downloads/diagram.png","my-picture").description("时序图片").build(),
                        (mcpSyncServerExchange, readResourceRequest) -> {
                            String uri = readResourceRequest.uri();
                            Path path = null;
                            // 示例：如果 uri 是 file:///path/to/img.png
                            if (uri.startsWith("file://")) {
                                path = Paths.get(URI.create(uri));
                            }

                            byte[] imageBytes = null;
                            String mimeType = null;
                            try {
                                imageBytes = Files.readAllBytes(path);
                                mimeType = Files.probeContentType(path);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            // 4. 将二进制字节进行 Base64 编码（MCP 规范要求 Blob 必须是 Base64 字符串）
                            String base64Content = Base64.getEncoder().encodeToString(imageBytes);
                            // 5. 动态探测或指定图片的真实 MIME 类型 (如 image/png, image/jpeg)

                            // 6. 关键点：使用 BlobResourceContents 承载二进制 Base64 数据
                            McpSchema.BlobResourceContents blobContent = new McpSchema.BlobResourceContents(
                                    base64Content,   // Base64 编码后的字符串
                                    uri,       // 资源的原 URI
                                    mimeType         // 必须提供正确的图片 MimeType 供大模型多模态解析
                            );
                            // 7. 组装结果并返回
                            return new McpSchema.ReadResourceResult(List.of(blobContent));
                        }
                )))
                .build();
    }
}