package me.junha.skthon_be.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hackathon API")
                        .description("해커톤용 로그인/회원가입 API 문서")
                        .version("1.0.0"))
                .servers(List.of(
                        new Server().url("https://skthonbe-production.up.railway.app")
                                .description("Railway Production 서버"),
                        new Server().url("http://localhost:8080")
                                .description("로컬 개발 서버")
                ));
    }
}
