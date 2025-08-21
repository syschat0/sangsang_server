package kr.co.bnksys.sangsang.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        /*
                        .allowedOrigins("http://localhost:3001")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowCredentials(true);

                         */
                        .allowedOriginPatterns("*")   // 전체 Origin 허용
                        .allowedMethods("*")          // 모든 HTTP 메소드 허용
                        .allowedHeaders("*")          // 모든 헤더 허용
                        .allowCredentials(true);      // 쿠키/인증정보 허용
            }
        };
    }
}
