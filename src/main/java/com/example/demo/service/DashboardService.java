package com.example.demo.service;

import com.example.demo.entity.Delivery;
import com.example.demo.repository.CampaignRepository;
import com.example.demo.repository.DeliveryRepository;
import com.example.demo.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class DashboardService {
    
    @Autowired
    private CampaignRepository campaignRepository;
    
    @Autowired
    private DeliveryRepository deliveryRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    /**
     * 대시보드 요약 지표 조회
     */
    public Map<String, Object> getDashboardSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        try {
            // 1. 총 캠페인 수
            long totalCampaigns = campaignRepository.count();
            
            // 2. 발송된 메시지 수
            long totalMessages = deliveryRepository.count();
            
            // 3. 도달한 고객 수 (SENT 상태인 고유 customer_id 수)
            long reachedCustomers = deliveryRepository.countDistinctCustomerIdByStatus(Delivery.DeliveryStatus.SENT);
            
            // 4. 평균 도달률 계산
            double reachRate = 0.0;
            if (totalMessages > 0) {
                long sentMessages = deliveryRepository.countByStatus(Delivery.DeliveryStatus.SENT);
                reachRate = (double) sentMessages / totalMessages * 100;
                reachRate = Math.round(reachRate * 10.0) / 10.0; // 소수점 1자리
            }
            
            summary.put("success", true);
            summary.put("data", Map.of(
                "totalCampaigns", totalCampaigns,
                "totalMessages", totalMessages,
                "reachedCustomers", reachedCustomers,
                "reachRate", reachRate
            ));
            
        } catch (Exception e) {
            summary.put("success", false);
            summary.put("message", "요약 지표 조회 중 오류: " + e.getMessage());
        }
        
        return summary;
    }
    
    /**
     * 시간대별 발송 현황 조회
     */
    public Map<String, Object> getHourlyDeliveries() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            LocalDate today = LocalDate.now();
            LocalDate yesterday = today.minusDays(1);
            
            // 오늘 시간대별 발송 현황
            List<Object[]> todayData = deliveryRepository.getHourlyDeliveryStatsByDate(today);
            
            // 어제 시간대별 발송 현황 (전일 대비 계산용)
            List<Object[]> yesterdayData = deliveryRepository.getHourlyDeliveryStatsByDate(yesterday);
            
            // 데이터 변환
            List<Map<String, Object>> hourlyStats = new ArrayList<>();
            Map<Integer, Long> yesterdayMap = new HashMap<>();
            
            // 어제 데이터를 맵으로 변환
            for (Object[] row : yesterdayData) {
                Integer hour = (Integer) row[0];
                Long count = (Long) row[1];
                yesterdayMap.put(hour, count);
            }
            
            // 오늘 데이터 처리
            for (Object[] row : todayData) {
                Integer hour = (Integer) row[0];
                Long todayCount = (Long) row[1];
                Long yesterdayCount = yesterdayMap.getOrDefault(hour, 0L);
                
                // 증감률 계산
                double changeRate = 0.0;
                if (yesterdayCount > 0) {
                    changeRate = ((double) (todayCount - yesterdayCount) / yesterdayCount) * 100;
                    changeRate = Math.round(changeRate * 10.0) / 10.0;
                }
                
                Map<String, Object> hourData = new HashMap<>();
                hourData.put("hour", String.format("%02d:00", hour));
                hourData.put("count", todayCount);
                hourData.put("changeRate", changeRate);
                
                hourlyStats.add(hourData);
            }
            
            result.put("success", true);
            result.put("data", hourlyStats);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "시간대별 발송 현황 조회 중 오류: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 지역별 고객 분포 조회
     */
    public Map<String, Object> getCustomerDistribution() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<Object[]> distribution = customerRepository.getCustomerDistributionByRegion();
            
            List<Map<String, Object>> regionData = new ArrayList<>();
            long totalCustomers = customerRepository.count();
            
            for (Object[] row : distribution) {
                String dongCode = (String) row[0];
                Long count = (Long) row[1];
                
                // 동코드를 지역명으로 변환
                String regionName = convertDongCodeToRegionName(dongCode);
                
                double percentage = 0.0;
                if (totalCustomers > 0) {
                    percentage = (double) count / totalCustomers * 100;
                    percentage = Math.round(percentage * 10.0) / 10.0;
                }
                
                Map<String, Object> regionInfo = new HashMap<>();
                regionInfo.put("region", regionName);
                regionInfo.put("count", count);
                regionInfo.put("percentage", percentage);
                
                regionData.add(regionInfo);
            }
            
            result.put("success", true);
            result.put("data", regionData);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "지역별 고객 분포 조회 중 오류: " + e.getMessage());
        }
        
        return result;
    }
    
    // 동코드를 지역명으로 변환하는 메서드
    private String convertDongCodeToRegionName(String dongCode) {
        if (dongCode == null || dongCode.length() < 5) {
            return "기타";
        }
        
        // 서울시 지역 코드 (11xxxx)
        if (dongCode.startsWith("11")) {
            switch (dongCode.substring(0, 5)) {
                case "11110": return "종로구";
                case "11140": return "중구";
                case "11170": return "용산구";
                case "11200": return "성동구";
                case "11215": return "광진구";
                case "11230": return "동대문구";
                case "11260": return "중랑구";
                case "11290": return "성북구";
                case "11305": return "강북구";
                case "11320": return "도봉구";
                case "11350": return "노원구";
                case "11380": return "은평구";
                case "11410": return "서대문구";
                case "11440": return "마포구";
                case "11470": return "양천구";
                case "11500": return "강서구";
                case "11530": return "구로구";
                case "11545": return "금천구";
                case "11560": return "영등포구";
                case "11590": return "동작구";
                case "11620": return "관악구";
                case "11650": return "서초구";
                case "11680": return "강남구";
                case "11710": return "송파구";
                case "11740": return "강동구";
                default: return "서울시 기타";
            }
        }
        
        return dongCode; // 변환할 수 없는 경우 원본 코드 반환
    }
    
    /**
     * 최근 캠페인 목록 조회
     */
    public Map<String, Object> getRecentCampaigns() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<Object[]> campaigns = campaignRepository.getRecentCampaignsWithStats();
            
            List<Map<String, Object>> campaignList = new ArrayList<>();
            
            for (Object[] row : campaigns) {
                String id = (String) row[0];
                String name = (String) row[1];
                String status = (String) row[2];
                LocalDateTime createdAt = (LocalDateTime) row[3];
                String targetingLocationName = (String) row[4];
                Long messageCount = (Long) row[5];
                
                Map<String, Object> campaign = new HashMap<>();
                campaign.put("id", id);
                campaign.put("name", name);
                campaign.put("status", status);
                campaign.put("createdAt", createdAt.toString());
                campaign.put("location", targetingLocationName != null ? targetingLocationName : "미설정");
                campaign.put("messageCount", messageCount != null ? messageCount : 0);
                
                campaignList.add(campaign);
            }
            
            result.put("success", true);
            result.put("data", campaignList);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "최근 캠페인 목록 조회 중 오류: " + e.getMessage());
        }
        
        return result;
    }
}
