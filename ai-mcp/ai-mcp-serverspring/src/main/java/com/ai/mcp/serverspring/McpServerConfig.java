package com.ai.mcp.serverspring;

import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.json.jackson3.JacksonMcpJsonMapper;
import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@Configuration
@EnableWebMvc
public class McpServerConfig implements WebMvcConfigurer {

    // 1. 手动声明缺失的 McpJsonMapper Bean
    @Bean
    public McpJsonMapper mcpJsonMapper(JsonMapper jsonMapper) {
        // 使用 Spring 默认注入的 Jackson ObjectMapper 来初始化 MCP 专用 Mapper
        return new JacksonMcpJsonMapper(jsonMapper);
    }

    @Bean
    public HttpServletStreamableServerTransportProvider transportProvider(McpJsonMapper jsonMapper) {
        return HttpServletStreamableServerTransportProvider.builder()
                .jsonMapper(jsonMapper)
                // 基础路由前缀。流式 GET 在 /mcp/sse，指令 POST 在 /mcp/message
                .mcpEndpoint("/mcp")
                .build();
    }

    @Bean
    public ServletRegistrationBean<?> mcpServlet(
            HttpServletStreamableServerTransportProvider transportProvider) {
        return new ServletRegistrationBean<>(transportProvider);
        // 【关键修复点】：映射路径必须使用 "/mcp/*" 确保通配其下的 /sse 和 /message 子端点
//        ServletRegistrationBean<HttpServletStreamableServerTransportProvider> registrationBean =
//                new ServletRegistrationBean<>(transportProvider, "/mcp/*");
//        registrationBean.setName("McpServlet");
//        return registrationBean;
    }
}