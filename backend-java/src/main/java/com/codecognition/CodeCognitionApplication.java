package com.codecognition;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class CodeCognitionApplication {

    public static void main(String[] args) {
        logEnvironmentStatus();
        SpringApplication.run(CodeCognitionApplication.class, args);
    }

    private static void logEnvironmentStatus() {
        System.out.println("\n=== CodeCognition Backend Environment Status ===");
        
        String dbHost = System.getenv("DB_HOST");
        String dbPort = System.getenv("DB_PORT");
        String dbName = System.getenv("DB_NAME");
        String jwtSecret = System.getenv("JWT_SECRET");
        String allowedOrigins = System.getenv("ALLOWED_ORIGINS");
        
        System.out.println("[✓] Backend starting...");
        System.out.println("[DB] Host: " + (dbHost != null ? dbHost : "localhost (default)"));
        System.out.println("[DB] Port: " + (dbPort != null ? dbPort : "3306 (default)"));
        System.out.println("[DB] Database: " + (dbName != null ? dbName : "codecognition_db (default)"));
        System.out.println("[JWT] Secret configured: " + (jwtSecret != null ? "✓" : "✗ (using default)"));
        System.out.println("[CORS] Custom origins: " + (allowedOrigins != null ? allowedOrigins : "using defaults"));
        System.out.println("==============================================\n");
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                    .allowedOrigins(
                        "http://localhost:5173",      // Frontend dev server
                        "http://localhost:3000",      // Alternative frontend port
                        "http://localhost:8000"       // Backend dev
                        // TODO: Add production domain when deploying
                        // "https://yourdomain.com",
                        // "https://www.yourdomain.com"
                    )
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .maxAge(3600);
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
