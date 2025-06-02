package com.dev.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")	// 모든 엔드포인트
                .allowedOrigins("http://localhost:5173")	// Vite dev server
                .allowedMethods("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false);
        // 자격증명 인증 정보 필요시 true.
        //브라우저 보안 정책상, Access-Control-Allow-Credentials: true가 응답에 포함되면 Access-Control-Allow-Origin에는 * (와일드카드)를 쓸 수 없음
        //allowCredentials(false) 덕분에 "*" 와일드카드가 안 써도 문제 없음. 대신 자격 증명(세션, 쿠키 등)은 못 씀.
    }
}