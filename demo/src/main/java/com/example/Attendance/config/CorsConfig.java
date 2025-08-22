//package com.example.Attendance.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class CorsConfig implements WebMvcConfigurer {
//
//    //최소한의 CORS 설정, 기존 WebConfig와 충돌 없이 주간 근태관리 API를 안정적 호출 지원.
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/api/work/**")  // 주간 근태관리 관련 API 경로
//            .allowedOrigins("http://localhost:5173", "http://localhost:5174") // 프론트 개발 서버 주소
//            .allowedMethods("*")     // 모든 HTTP 메서드 허용
//            .allowedHeaders("*")     // 모든 헤더 허용
//            .allowCredentials(true); // 쿠키, 세션 등 인증정보 허용
//    }
//}
