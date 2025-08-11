package com.example.Attendance.config;

import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();

        // WebConfig보고 프론트 개발 서버 주소 동일하게 적기
        cfg.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173",
            "http://localhost:5174"
        ));
        // (참고) 포트가 자주 바뀐다면 setAllowedOriginPatterns 사용 가능
        // cfg.setAllowedOriginPatterns(Arrays.asList("http://localhost:5173", "http://localhost:5174"));

        cfg.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(Arrays.asList("*"));
        cfg.setAllowCredentials(true); // 쿠키/세션 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
