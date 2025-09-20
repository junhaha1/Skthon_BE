package me.junha.skthon_be.config;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:5173",
                        "https://skthon-fe.vercel.app/",
                        "http://skthon-fe.vercel.app/"
                ) // React 개발 서버 주소
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 🔥 이게 꼭 필요!
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
