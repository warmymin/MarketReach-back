package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableJpaRepositories
public class KtMarketReachApplication {

	public static void main(String[] args) {
		SpringApplication.run(KtMarketReachApplication.class, args);
		        		System.out.println("🚀 KT MarketReach 애플리케이션이 시작되었습니다!");
        System.out.println("📊 API 서버: http://localhost:8083");
        System.out.println("🏥 헬스 체크: http://localhost:8083/health");
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/api/**")
					.allowedOriginPatterns("*")
					.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
					.allowedHeaders("*")
					.allowCredentials(false)
					.maxAge(3600);
			}
		};
	}
}
