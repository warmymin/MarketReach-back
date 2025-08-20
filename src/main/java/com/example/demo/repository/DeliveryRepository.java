package com.example.demo.repository;

import com.example.demo.entity.Delivery;
import com.example.demo.entity.Delivery.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {
    
    // 캠페인별 발송 목록 조회
    List<Delivery> findByCampaignIdOrderByCreatedAtDesc(UUID campaignId);
    
    // 상태별 발송 목록 조회
    List<Delivery> findByStatusOrderByCreatedAtDesc(DeliveryStatus status);
    
    // 특정 시간 범위 내 발송 목록 조회
    @Query("SELECT d FROM Delivery d WHERE d.createdAt BETWEEN :startTime AND :endTime ORDER BY d.createdAt DESC")
    List<Delivery> findByCreatedAtBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    // 상태별 발송 건수 조회
    long countByStatus(DeliveryStatus status);
    
    // 캠페인별 상태별 발송 건수 조회
    long countByCampaignIdAndStatus(UUID campaignId, DeliveryStatus status);
    
    // 오늘 발송된 건수 조회
    @Query("SELECT COUNT(d) FROM Delivery d WHERE CAST(d.createdAt AS DATE) = CURRENT_DATE")
    long countTodayDeliveries();
    
    // 시간대별 발송 건수 조회 (오늘)
    @Query("SELECT EXTRACT(HOUR FROM d.createdAt) as hour, COUNT(d) as count FROM Delivery d " +
           "WHERE CAST(d.createdAt AS DATE) = CURRENT_DATE " +
           "GROUP BY EXTRACT(HOUR FROM d.createdAt) ORDER BY hour")
    List<Object[]> getHourlyDeliveryStats();
    
    // 최근 30분간 5분 단위 발송 통계
    @Query("SELECT " +
           "FLOOR(EXTRACT(MINUTE FROM d.createdAt) / 5) * 5 as timeSlot, " +
           "CAST(d.status AS string) as status, " +
           "COUNT(d) as count " +
           "FROM Delivery d " +
           "WHERE d.createdAt >= :startTime " +
           "GROUP BY FLOOR(EXTRACT(MINUTE FROM d.createdAt) / 5) * 5, d.status " +
           "ORDER BY timeSlot, d.status")
    List<Object[]> getRealtimeDeliveryStats(@Param("startTime") LocalDateTime startTime);

    // 타겟팅 위치별 발송 목록 조회 (Campaign을 통해)
    @Query("SELECT d FROM Delivery d JOIN d.campaign c WHERE c.targetingLocation.id = :targetingLocationId")
    List<Delivery> findByCampaignTargetingLocationId(@Param("targetingLocationId") UUID targetingLocationId);
    
    // === 새로 추가하는 메서드들 ===
    
    // 특정 시간대의 발송 개수 조회
    @Query("SELECT COUNT(d) FROM Delivery d WHERE d.createdAt BETWEEN :startTime AND :endTime")
    long countByCreatedAtBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    // 특정 상태와 시간대의 발송 개수 조회
    @Query("SELECT COUNT(d) FROM Delivery d WHERE d.status = :status AND d.createdAt BETWEEN :startTime AND :endTime")
    long countByStatusAndCreatedAtBetween(@Param("status") DeliveryStatus status, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    // 지역별 발송 분포 통계 (고객 지역 기준)
    @Query("SELECT c.dongCode, COUNT(d) FROM Delivery d JOIN d.customer c GROUP BY c.dongCode ORDER BY COUNT(d) DESC")
    List<Object[]> getRegionDistributionStats();
    
    // 오늘 시간대별 성공/실패 통계 (한국 시간 기준)
    @Query("SELECT EXTRACT(HOUR FROM d.createdAt) as hour, d.status, COUNT(d) as count " +
           "FROM Delivery d " +
           "WHERE CAST(d.createdAt AS DATE) = CURRENT_DATE " +
           "GROUP BY EXTRACT(HOUR FROM d.createdAt), d.status " +
           "ORDER BY hour, d.status")
    List<Object[]> getTodayHourlyStatsByStatus();
}
