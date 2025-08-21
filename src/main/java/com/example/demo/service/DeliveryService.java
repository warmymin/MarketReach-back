package com.example.demo.service;

import com.example.demo.entity.Delivery;
import com.example.demo.entity.Delivery.DeliveryStatus;
import com.example.demo.entity.Campaign;
import com.example.demo.entity.Customer;
import com.example.demo.repository.DeliveryRepository;
import com.example.demo.repository.CampaignRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.TargetingLocationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.LocalDate;

@Service
public class DeliveryService {

    @Autowired
    private DeliveryRepository deliveryRepository;
    
    @Autowired
    private CampaignRepository campaignRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private TargetingLocationRepository targetingLocationRepository;
    

    
    @Autowired
    private DeliveryStreamService deliveryStreamService;
    
    // 병렬 처리 제거로 인한 ExecutorService 제거
    
    /**
     * 캠페인별 총 발송 건수 조회
     */
    public long countDeliveriesByCampaignId(UUID campaignId) {
        return deliveryRepository.countByCampaignId(campaignId);
    }
    
    /**
     * 캠페인별 성공한 발송 건수 조회
     */
    public long countSuccessfulDeliveriesByCampaignId(UUID campaignId) {
        return deliveryRepository.countByCampaignIdAndStatusIn(
            campaignId, 
            List.of(DeliveryStatus.SENT, DeliveryStatus.SUCCESS)
        );
    }
    
    /**
     * 캠페인 발송 시뮬레이션
     */
    @Transactional
    public Map<String, Object> simulateCampaignDelivery(UUID campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("캠페인을 찾을 수 없습니다."));
        
        // 기존 발송 데이터 삭제 (중복 방지)
        List<Delivery> existingDeliveries = deliveryRepository.findByCampaignIdOrderByCreatedAtDesc(campaignId);
        if (!existingDeliveries.isEmpty()) {
            System.out.println("기존 발송 데이터 " + existingDeliveries.size() + "건 삭제");
            deliveryRepository.deleteAll(existingDeliveries);
        }
        
        List<Customer> targetCustomers = new ArrayList<>();
        
        // 타겟팅 위치가 있는 경우
        if (campaign.getTargetingLocation() != null) {
            targetCustomers = customerRepository.findCustomersNearLocation(
                    campaign.getTargetingLocation().getCenterLat(),
                    campaign.getTargetingLocation().getCenterLng(),
                    campaign.getTargetingLocation().getRadiusM()
            );
        }

        
        if (targetCustomers.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("totalDeliveries", 0);
            result.put("sentCount", 0);
            result.put("failedCount", 0);
            result.put("pendingCount", 0);
            result.put("successRate", 0.0);
            result.put("message", "타겟팅이 설정되지 않았거나 타겟 고객이 없습니다.");
            return result;
        }
        
        List<Delivery> deliveries = new ArrayList<>();
        
        // 각 고객에 대해 순차 발송 시뮬레이션 (더 빠른 처리)
        for (Customer customer : targetCustomers) {
            try {
                Delivery delivery = simulateDeliveryToCustomer(campaign, customer);
                if (delivery != null) {
                    deliveries.add(delivery);
                }
            } catch (Exception e) {
                System.err.println("발송 처리 중 오류: " + e.getMessage());
                // 실패한 발송 처리
                Delivery failedDelivery = new Delivery(campaign, null);
                failedDelivery.setStatus(DeliveryStatus.FAILED);
                failedDelivery.setErrorCode("SIMULATION_ERROR");
                failedDelivery.setCreatedAt(LocalDateTime.now());
                failedDelivery.setMessageTextSent(campaign.getMessage());
                deliveries.add(deliveryRepository.save(failedDelivery));
            }
        }
        
        // 통계 계산
        long sentCount = 0;
        long failedCount = 0;
        long pendingCount = 0;
        
        for (Delivery delivery : deliveries) {
            try {
                switch (delivery.getStatus()) {
                    case SENT:
                    case SUCCESS:
                        sentCount++;
                        break;
                    case FAILED:
                        failedCount++;
                        break;
                    case PENDING:
                        pendingCount++;
                        break;
                    default:
                        failedCount++;
                        break;
                }
            } catch (Exception e) {
                failedCount++;
            }
        }
        
        double successRate = calculateSuccessRate(deliveries);
        
        System.out.println("발송 결과:");
        System.out.println("- 총 발송: " + deliveries.size() + "건");
        System.out.println("- 성공: " + sentCount + "건");
        System.out.println("- 실패: " + failedCount + "건");
        System.out.println("- 성공률: " + successRate + "%");
        
        // 캠페인 상태 업데이트
        if (failedCount == deliveries.size()) {
            campaign.setStatus("FAILED");
        } else {
            campaign.setStatus("COMPLETED");
        }
        campaignRepository.save(campaign);
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalDeliveries", deliveries.size());
        result.put("sentCount", sentCount);
        result.put("failedCount", failedCount);
        result.put("pendingCount", pendingCount);
        result.put("successRate", successRate);
        result.put("message", "캠페인 발송이 완료되었습니다.");
        
        return result;
    }
    
    /**
     * 개별 고객 발송 시뮬레이션
     */
    private Delivery simulateDeliveryToCustomer(Campaign campaign, Customer customer) {
        try {
            Delivery delivery = new Delivery(campaign, customer);
            delivery.setMessageTextSent(campaign.getMessage());
            delivery.setCreatedAt(LocalDateTime.now());
            
            // 더 현실적인 성공/실패/대기 분포
            // 70% 성공, 20% 실패, 10% 대기
            double random = Math.random();
            if (random < 0.70) {
                // 성공 (70%)
                delivery.setStatus(DeliveryStatus.SENT);
                delivery.setSentAt(LocalDateTime.now());
                
                // 지연 제거 - 즉시 처리
            } else if (random < 0.90) {
                // 실패 (20%)
                delivery.setStatus(DeliveryStatus.FAILED);
                delivery.setErrorCode("SIMULATION_FAILED");
            } else {
                // 대기 (10%)
                delivery.setStatus(DeliveryStatus.PENDING);
            }
            
            Delivery saved = deliveryRepository.save(delivery);
            try {
                Map<String, Object> event = new HashMap<>();
                event.put("type", "DELIVERY_CREATED");
                event.put("id", saved.getId());
                event.put("campaignId", campaign.getId());
                event.put("customerId", customer.getId());
                event.put("status", saved.getStatus().name());
                event.put("createdAt", saved.getCreatedAt());
                deliveryStreamService.publish(event);
            } catch (Exception ignored) {}
            return saved;
        } catch (Exception e) {
            System.err.println("고객 발송 시뮬레이션 중 오류: " + e.getMessage());
            return null;
        }
    }

    /**
     * 랜덤 에러 코드 생성
     */
    private String getRandomErrorCode() {
        String[] errorCodes = {
            "NETWORK_TIMEOUT",
            "INVALID_PHONE",
            "SERVICE_UNAVAILABLE",
            "QUOTA_EXCEEDED",
            "BLOCKED_NUMBER"
        };
        return errorCodes[(int) (Math.random() * errorCodes.length)];
    }
    
    /**
     * 성공률 계산
     */
    private double calculateSuccessRate(List<Delivery> deliveries) {
        if (deliveries.isEmpty()) return 0.0;
        
        long sentCount = 0;
        for (Delivery delivery : deliveries) {
            try {
                if (delivery.getStatus() == DeliveryStatus.SENT) {
                    sentCount++;
                }
            } catch (Exception e) {
                // enum 변환 오류 시 무시
                System.err.println("calculateSuccessRate에서 DeliveryStatus 처리 중 오류: " + e.getMessage());
            }
        }
        
        return (double) sentCount / deliveries.size() * 100;
    }
    
    /**
     * 발송 통계 요약 조회
     */
    public Map<String, Object> getDeliverySummary() {
        Map<String, Object> summary = new HashMap<>();
        
        long totalDeliveries = deliveryRepository.count();
        long sentCount = deliveryRepository.countByStatus(DeliveryStatus.SENT);
        long failedCount = deliveryRepository.countByStatus(DeliveryStatus.FAILED);
        long pendingCount = deliveryRepository.countByStatus(DeliveryStatus.PENDING);
        long todayDeliveries = deliveryRepository.countTodayDeliveries();
        
        summary.put("totalDeliveries", totalDeliveries);
        summary.put("sentCount", sentCount);
        summary.put("failedCount", failedCount);
        summary.put("pendingCount", pendingCount);
        summary.put("todayDeliveries", todayDeliveries);
        summary.put("successRate", totalDeliveries > 0 ? (double) sentCount / totalDeliveries * 100 : 0.0);
        
        return summary;
    }
    
    /**
     * 실시간 발송 통계 조회 (최근 30분)
     */
    public List<Map<String, Object>> getRealtimeStats() {
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(30);
        List<Object[]> rawStats = deliveryRepository.getRealtimeDeliveryStats(startTime);
        
        // 시간대별 통계 계산
        Map<Integer, Map<String, Long>> timeSlotStats = new HashMap<>();
        for (Object[] row : rawStats) {
            Integer timeSlot = (Integer) row[0];
            String statusStr = (String) row[1];
            Long count = (Long) row[2];
            
            // enum 변환 시 예외 처리
            String statusName;
            try {
                DeliveryStatus status = DeliveryStatus.valueOf(statusStr);
                statusName = status.name();
            } catch (IllegalArgumentException e) {
                // 알 수 없는 상태는 FAILED로 처리
                statusName = "FAILED";
                System.err.println("알 수 없는 DeliveryStatus: " + statusStr);
            }
            
            timeSlotStats.computeIfAbsent(timeSlot, k -> new HashMap<>())
                    .put(statusName, count);
        }
        
        // 결과 포맷팅
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i <= 6; i++) { // 30분을 5분 단위로 6개 구간
            Map<String, Object> timeSlot = new HashMap<>();
            timeSlot.put("timeSlot", i * 5);
            timeSlot.put("sent", timeSlotStats.getOrDefault(i, new HashMap<>()).getOrDefault("SENT", 0L));
            timeSlot.put("failed", timeSlotStats.getOrDefault(i, new HashMap<>()).getOrDefault("FAILED", 0L));
            timeSlot.put("pending", timeSlotStats.getOrDefault(i, new HashMap<>()).getOrDefault("PENDING", 0L));
            result.add(timeSlot);
        }
        
        return result;
    }
    
    /**
     * 시간대별 발송 통계 조회
     */
    public List<Map<String, Object>> getHourlyStats() {
        List<Object[]> rawStats = deliveryRepository.getHourlyDeliveryStats();
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rawStats) {
            Map<String, Object> hourStat = new HashMap<>();
            hourStat.put("hour", row[0]);
            hourStat.put("count", row[1]);
            result.add(hourStat);
        }
        
        return result;
    }
    
    /**
     * 캠페인별 발송 목록 조회
     */
    public List<Delivery> getDeliveriesByCampaign(UUID campaignId) {
        return deliveryRepository.findByCampaignIdOrderByCreatedAtDesc(campaignId);
    }
    
    /**
     * 상태별 발송 목록 조회
     */
    public List<Delivery> getDeliveriesByStatus(DeliveryStatus status) {
        return deliveryRepository.findByStatusOrderByCreatedAtDesc(status);
    }
    
    /**
     * 캠페인별 발송 통계 조회
     */
    public Map<String, Object> getCampaignDeliveryStats(UUID campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("캠페인을 찾을 수 없습니다."));
        
        List<Delivery> deliveries = deliveryRepository.findByCampaignIdOrderByCreatedAtDesc(campaignId);
        
        long sentCount = 0;
        long failedCount = 0;
        long pendingCount = 0;
        
        for (Delivery delivery : deliveries) {
            try {
                switch (delivery.getStatus()) {
                    case SENT:
                    case SUCCESS:
                        sentCount++;
                        break;
                    case FAILED:
                        failedCount++;
                        break;
                    case PENDING:
                        pendingCount++;
                        break;
                    default:
                        failedCount++;
                        break;
                }
            } catch (Exception e) {
                failedCount++;
            }
        }
        
        double successRate = calculateSuccessRate(deliveries);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDeliveries", deliveries.size());
        stats.put("sentCount", sentCount);
        stats.put("failedCount", failedCount);
        stats.put("pendingCount", pendingCount);
        stats.put("successRate", successRate);
        
        return stats;
    }

    /**
     * 최근 발송 내역 조회
     */
    public List<Delivery> getRecentDeliveries() {
        List<Delivery> allDeliveries = deliveryRepository.findAll();
        allDeliveries.sort((d1, d2) -> d2.getCreatedAt().compareTo(d1.getCreatedAt()));
        return allDeliveries.stream().limit(20).toList();
    }

    /**
     * 타겟팅 위치별 발송 통계 조회
     */
    public Map<String, Object> getDeliveryStatsByTargeting(UUID targetingId) {
        List<Delivery> deliveries = deliveryRepository.findByCampaignTargetingLocationId(targetingId);
        
        long sentCount = 0;
        long failedCount = 0;
        long pendingCount = 0;
        
        for (Delivery delivery : deliveries) {
            try {
                switch (delivery.getStatus()) {
                    case SENT:
                    case SUCCESS:
                        sentCount++;
                        break;
                    case FAILED:
                        failedCount++;
                        break;
                    case PENDING:
                        pendingCount++;
                        break;
                    default:
                        failedCount++;
                        break;
                }
            } catch (Exception e) {
                failedCount++;
            }
        }
        
        double successRate = calculateSuccessRate(deliveries);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDeliveries", deliveries.size());
        stats.put("sentCount", sentCount);
        stats.put("failedCount", failedCount);
        stats.put("pendingCount", pendingCount);
        stats.put("successRate", successRate);
        
        return stats;
    }

    /**
     * 발송 상태 업데이트
     */
    @Transactional
    public Delivery updateDeliveryStatus(UUID deliveryId, DeliveryStatus newStatus) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("발송을 찾을 수 없습니다."));
        
        delivery.setStatus(newStatus);
        
        if (newStatus == DeliveryStatus.SENT) {
            delivery.setSentAt(LocalDateTime.now());
        }
        
        Delivery updated = deliveryRepository.save(delivery);
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("type", "DELIVERY_UPDATED");
            event.put("id", updated.getId());
            event.put("status", updated.getStatus().name());
            event.put("sentAt", updated.getSentAt());
            event.put("updatedAt", LocalDateTime.now());
            deliveryStreamService.publish(event);
        } catch (Exception ignored) {}
        return updated;
    }

    /**
     * 지역별 분포 통계 조회
     */
    public List<Map<String, Object>> getRegionDistribution() {
        List<Map<String, Object>> distribution = new ArrayList<>();
        
        // 서울시 주요 지역별 분포 데이터
        String[] regions = {"강남구", "서초구", "마포구", "종로구", "중구", "용산구", "성동구", "광진구", "동대문구", "중랑구"};
        
        for (String region : regions) {
            Map<String, Object> regionData = new HashMap<>();
            regionData.put("region", region);
            regionData.put("count", (int) (Math.random() * 100) + 50); // 임시 데이터
            regionData.put("percentage", Math.round((Math.random() * 20 + 5) * 10.0) / 10.0); // 5-25% 범위
            distribution.add(regionData);
        }
        
        return distribution;
    }

    /**
     * 실시간 발송 현황 조회 (최근 30분, 5분 간격, 한국 시간 기준)
     */
    public List<Map<String, Object>> getRealtimeDeliveryStatus() {
        // 한국 시간대 설정
        ZoneId koreaZone = ZoneId.of("Asia/Seoul");
        LocalDateTime now = LocalDateTime.now(koreaZone);
        LocalDateTime startTime = now.minusMinutes(30);
        
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 5분 간격으로 6개 구간 생성
        for (int i = 0; i < 6; i++) {
            LocalDateTime slotStart = startTime.plusMinutes(i * 5);
            LocalDateTime slotEnd = slotStart.plusMinutes(5);
            
            // 해당 시간대의 발송 통계 조회
            long totalCount = deliveryRepository.countByCreatedAtBetween(slotStart, slotEnd);
            long sentCount = deliveryRepository.countByStatusAndCreatedAtBetween(DeliveryStatus.SENT, slotStart, slotEnd);
            long failedCount = deliveryRepository.countByStatusAndCreatedAtBetween(DeliveryStatus.FAILED, slotStart, slotEnd);
            
            Map<String, Object> timeSlot = new HashMap<>();
            timeSlot.put("time", slotStart.format(DateTimeFormatter.ofPattern("HH:mm")));
            timeSlot.put("total", totalCount);
            timeSlot.put("success", sentCount);
            timeSlot.put("failed", failedCount);
            
            result.add(timeSlot);
        }
        
        return result;
    }

    /**
     * 오늘 시간대별 발송 통계 조회 (한국 시간 기준)
     */
    public List<Map<String, Object>> getTodayHourlyStats() {
        ZoneId koreaZone = ZoneId.of("Asia/Seoul");
        LocalDateTime todayStart = LocalDateTime.now(koreaZone).toLocalDate().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);
        
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 9시부터 18시까지 시간대별 통계
        for (int hour = 9; hour <= 18; hour++) {
            LocalDateTime hourStart = todayStart.plusHours(hour);
            LocalDateTime hourEnd = hourStart.plusHours(1);
            
            long successCount = deliveryRepository.countByStatusAndCreatedAtBetween(DeliveryStatus.SENT, hourStart, hourEnd);
            long failedCount = deliveryRepository.countByStatusAndCreatedAtBetween(DeliveryStatus.FAILED, hourStart, hourEnd);
            
            Map<String, Object> hourStat = new HashMap<>();
            hourStat.put("hour", String.format("%02d:00", hour));
            hourStat.put("success", successCount);
            hourStat.put("failed", failedCount);
            
            result.add(hourStat);
        }
        
        return result;
    }

    /**
     * 현재 시간 기준 -4시간부터 현재까지의 시간대별 발송 통계 조회
     */
    public List<Map<String, Object>> getRecentHourlyStats() {
        ZoneId koreaZone = ZoneId.of("Asia/Seoul");
        LocalDateTime now = LocalDateTime.now(koreaZone);
        
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 현재 시간부터 -4시간까지 5개 시간대 생성
        for (int i = 4; i >= 0; i--) {
            LocalDateTime targetTime = now.minusHours(i);
            LocalDateTime hourStart = targetTime.withMinute(0).withSecond(0).withNano(0);
            LocalDateTime hourEnd = hourStart.plusHours(1);
            
            // 해당 시간대의 총 발송 건수 조회
            long totalCount = deliveryRepository.countByCreatedAtBetween(hourStart, hourEnd);
            
            Map<String, Object> hourStat = new HashMap<>();
            hourStat.put("hour", hourStart.getHour()); // 숫자로 반환 (예: 15)
            hourStat.put("count", totalCount);
            
            result.add(hourStat);
        }
        
        return result;
    }

    /**
     * 지역별 발송 분포 조회 (실제 데이터 기반)
     */
    public List<Map<String, Object>> getRegionDistributionReal() {
        List<Object[]> rawData = deliveryRepository.getRegionDistributionStats();
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Object[] row : rawData) {
            String dongCode = (String) row[0];
            Long count = (Long) row[1];
            
            // dongCode를 지역명으로 변환
            String regionName = convertDongCodeToRegionName(dongCode);
            
            Map<String, Object> regionData = new HashMap<>();
            regionData.put("name", regionName);
            regionData.put("value", count);
            
            result.add(regionData);
        }
        
        // 데이터가 없으면 기본 데이터 반환
        if (result.isEmpty()) {
            result.add(Map.of("name", "강남구", "value", 29));
            result.add(Map.of("name", "서초구", "value", 23));
            result.add(Map.of("name", "마포구", "value", 18));
            result.add(Map.of("name", "종로구", "value", 15));
            result.add(Map.of("name", "중구", "value", 15));
        }
        
        return result;
    }
    
    /**
     * 동코드를 지역명으로 변환하는 헬퍼 메서드
     */
    private String convertDongCodeToRegionName(String dongCode) {
        if (dongCode == null || dongCode.isEmpty()) {
            return "기타";
        }
        
        // 동코드 앞 2자리가 구 코드
        if (dongCode.length() >= 2) {
            String guCode = dongCode.substring(0, 2);
            switch (guCode) {
                case "11": return "종로구";
                case "12": return "중구";
                case "13": return "용산구";
                case "14": return "성동구";
                case "15": return "광진구";
                case "16": return "동대문구";
                case "17": return "중랑구";
                case "18": return "성북구";
                case "19": return "강북구";
                case "20": return "도봉구";
                case "21": return "노원구";
                case "22": return "은평구";
                case "23": return "서대문구";
                case "24": return "마포구";
                case "25": return "양천구";
                case "26": return "강서구";
                case "27": return "구로구";
                case "28": return "금천구";
                case "29": return "영등포구";
                case "30": return "동작구";
                case "31": return "관악구";
                case "32": return "서초구";
                case "33": return "강남구";
                case "34": return "송파구";
                case "35": return "강동구";
                default: return "기타";
            }
        }
        
        return "기타";
    }

    /**
     * 최근 30분간 5분 단위 발송 현황 조회
     */
    public Map<String, Object> getRecentDeliveriesByTimeSlot() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyMinutesAgo = now.minusMinutes(30);
        
        List<Map<String, Object>> timeSlots = new ArrayList<>();
        
        // 5분 간격으로 6개 구간 생성 (30분)
        for (int i = 0; i < 6; i++) {
            LocalDateTime slotStart = thirtyMinutesAgo.plusMinutes(i * 5);
            LocalDateTime slotEnd = slotStart.plusMinutes(5);
            
            // 해당 시간대의 발송 데이터 조회
            List<Delivery> deliveries = deliveryRepository.findByCreatedAtBetween(slotStart, slotEnd);
            
            int totalCount = deliveries.size();
            int successCount = (int) deliveries.stream()
                .filter(d -> d.getStatus() == DeliveryStatus.SENT)
                .count();
            int failedCount = (int) deliveries.stream()
                .filter(d -> d.getStatus() == DeliveryStatus.FAILED)
                .count();
            
            Map<String, Object> slot = new HashMap<>();
            slot.put("time", slotStart.format(DateTimeFormatter.ofPattern("HH:mm")));
            slot.put("total", totalCount);
            slot.put("success", successCount);
            slot.put("failed", failedCount);
            
            timeSlots.add(slot);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", timeSlots);
        result.put("period", "최근 30분간 5분 단위");
        
        return result;
    }
    
    /**
     * 오늘 시간대별 발송 통계 조회
     */
    public Map<String, Object> getHourlyDeliveries() {
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime todayEnd = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        
        List<Map<String, Object>> hourlyStats = new ArrayList<>();
        
        // 24시간 데이터 생성
        for (int hour = 0; hour < 24; hour++) {
            LocalDateTime hourStart = todayStart.plusHours(hour);
            LocalDateTime hourEnd = hourStart.plusHours(1).minusNanos(1);
            
            // 해당 시간대의 발송 데이터 조회
            List<Delivery> deliveries = deliveryRepository.findByCreatedAtBetween(hourStart, hourEnd);
            
            int totalCount = deliveries.size();
            int successCount = (int) deliveries.stream()
                .filter(d -> d.getStatus() == DeliveryStatus.SENT)
                .count();
            int failedCount = (int) deliveries.stream()
                .filter(d -> d.getStatus() == DeliveryStatus.FAILED)
                .count();
            
            Map<String, Object> hourData = new HashMap<>();
            hourData.put("hour", String.format("%02d:00", hour));
            hourData.put("total", totalCount);
            hourData.put("success", successCount);
            hourData.put("failed", failedCount);
            
            hourlyStats.add(hourData);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", hourlyStats);
        result.put("date", LocalDate.now().toString());
        
        return result;
    }
}
