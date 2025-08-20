package com.example.demo.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class HealthController {
    
    /**
     * 헬스 체크 API
     */
    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "KT MarketReach API");
        response.put("version", "1.0.0");
        
        return response;
    }
    
    /**
     * 루트 엔드포인트
     */
    @GetMapping("/")
    public Map<String, Object> root() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "KT MarketReach 위치 기반 마케팅 플랫폼 API");
        response.put("version", "1.0.0");
        response.put("endpoints", Map.of(
            "companies", "/api/companies",
            "customers", "/api/customers",
            "campaigns", "/api/campaigns",
            "deliveries", "/api/deliveries",
            "qr-events", "/api/qr-events",
            "health", "/health"
        ));
        
        return response;
    }
}
