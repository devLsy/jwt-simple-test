package com.test.lsy.jwt1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);   // 내 서버가 응답할 때 json을 자바스크립트(ajax, axios 등)에서 처리할 수 있게 설정
        config.addAllowedOrigin("*"); // 모든 ip에 대한 응답 허용
        config.addAllowedHeader("*");   // 모든 헤더에 응답을 허용
        config.addAllowedMethod("*");   // 모든 HTTP method 방식 허용

        source.registerCorsConfiguration("/api/**", config);
        return new CorsFilter(source);
    }
}
