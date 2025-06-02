package com.dev.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("My API 문서")
                .version("v1.0.0")
                .description("Swagger로 자동 생성된 API 명세서입니다.")
                .contact(new Contact()
                    .name("API 관리자")
                    .email("admin@example.com")
                )
            )
            .addServersItem(new Server().url("http://localhost").description("Local 환경=========="));
    }
}