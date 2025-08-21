package com.example.demo.controller;

import com.example.demo.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {
    
    @Autowired
    private DashboardService dashboardService;
    
    /**
     * 대시보드 요약 지표 조회
     */
    @GetMapping("/summary")
    public ResponseEntity<?> getDashboardSummary() {
        try {
            Map<String, Object> summary = dashboardService.getDashboardSummary();
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "대시보드 요약 지표 조회 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 시간대별 발송 현황 조회
     */
    @GetMapping("/hourly-deliveries")
    public ResponseEntity<?> getHourlyDeliveries() {
        try {
            Map<String, Object> hourlyData = dashboardService.getHourlyDeliveries();
            return ResponseEntity.ok(hourlyData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "시간대별 발송 현황 조회 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 지역별 고객 분포 조회
     */
    @GetMapping("/customer-distribution")
    public ResponseEntity<?> getCustomerDistribution() {
        try {
            Map<String, Object> distribution = dashboardService.getCustomerDistribution();
            return ResponseEntity.ok(distribution);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "지역별 고객 분포 조회 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 최근 캠페인 목록 조회
     */
    @GetMapping("/recent-campaigns")
    public ResponseEntity<?> getRecentCampaigns() {
        try {
            Map<String, Object> campaigns = dashboardService.getRecentCampaigns();
            return ResponseEntity.ok(campaigns);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "최근 캠페인 목록 조회 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }
}
