package com.example.demo.controller;

import com.example.demo.service.CompanyService;
import com.example.demo.service.CampaignService;
import com.example.demo.service.CustomerService;
import com.example.demo.service.TargetingLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "*")
public class StatisticsController {
    
    @Autowired
    private CompanyService companyService;
    
    @Autowired
    private CampaignService campaignService;
    
    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private TargetingLocationService targetingLocationService;
    
    /**
     * 전체 통계 정보 조회 API
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getStatistics() {
        try {
            long companiesCount = companyService.getCompaniesCount();
            long customersCount = customerService.getCustomersCount();
            long campaignsCount = campaignService.getCampaignsCount();
            long targetingLocationsCount = targetingLocationService.getTargetingLocationsCount();
            
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("companies", companiesCount);
            statistics.put("customers", customersCount);
            statistics.put("campaigns", campaignsCount);
            statistics.put("targetingLocations", targetingLocationsCount);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", statistics);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "통계 조회 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 대시보드 통계 정보 조회 API
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStatistics() {
        try {
            long companiesCount = companyService.getCompaniesCount();
            long customersCount = customerService.getCustomersCount();
            long campaignsCount = campaignService.getCampaignsCount();
            long targetingLocationsCount = targetingLocationService.getTargetingLocationsCount();
            
            // 발송 통계 (임시 데이터)
            long activeDeliveries = 8;
            long completedDeliveries = 156;
            
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalCompanies", companiesCount);
            statistics.put("totalCustomers", customersCount);
            statistics.put("totalCampaigns", campaignsCount);
            statistics.put("totalTargetingLocations", targetingLocationsCount);
            statistics.put("activeDeliveries", activeDeliveries);
            statistics.put("completedDeliveries", completedDeliveries);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", statistics);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "대시보드 통계 조회 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }
}
