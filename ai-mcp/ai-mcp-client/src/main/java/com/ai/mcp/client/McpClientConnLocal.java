package com.ai.mcp.client;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.spec.McpClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Map;

/**
 * mcp client用于连接本地mcp server
 * 执行tool:read_local_file,并输出内容
 * @author lijianyu
 * @date 2026/7/11 07:07
 */
public class McpClientConnLocal {

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();

        ServerParameters params = ServerParameters.builder("java")
                .args("-jar", "/Users/liyu/develop/wp/java-wp/self-projects/ai-learn/ai-mcp/ai-mcp-serverlocal/target/ai-mcp-serverlocal-1.0-SNAPSHOT.jar")
                .build();
        McpClientTransport transport = new StdioClientTransport(params, McpJsonDefaults.getMapper());

        // Create a sync client with custom configuration
        McpSyncClient client = McpClient.sync(transport)
                .requestTimeout(Duration.ofSeconds(30))
                .capabilities(McpSchema.ClientCapabilities.builder()
                        .roots(true)       // Enable roots capability
                        .sampling()        // Enable sampling capability
                        .elicitation()     // Enable elicitation capability
                        .build())
                .sampling(request -> McpSchema.CreateMessageResult.builder().build())
                .elicitation(request -> new McpSchema.ElicitResult(McpSchema.ElicitResult.Action.ACCEPT, null))
                .loggingConsumer(loggingMessageNotification -> {
                    System.out.println("Received log message: " + loggingMessageNotification.data());
                })
                .build();

        client.initialize();

        client.listTools().tools().forEach(tool -> {
            String jsonString = mapper.writeValueAsString(tool);

            System.err.println(jsonString);
            McpSchema.CallToolResult callToolResult = client.callTool(new McpSchema.CallToolRequest("read_local_file", Map.of("filePath", "/Users/liyu/Downloads/123.txt")));
            System.err.println(mapper.writeValueAsString(callToolResult.content()));
        });

        //client close;
        client.closeGracefully();
    }
}
