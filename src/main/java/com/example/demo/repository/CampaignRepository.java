package com.example.demo.repository;

import com.example.demo.entity.Campaign;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Map;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, UUID> {
    
    /**
     * 회사별 캠페인 조회
     */
    @Query("SELECT c FROM Campaign c WHERE c.company.id = :companyId")
    Page<Campaign> findByCompanyId(@Param("companyId") UUID companyId, Pageable pageable);
    
    /**
     * 회사별 캠페인 수 조회
     */
    @Query("SELECT COUNT(c) FROM Campaign c WHERE c.company.id = :companyId")
    Long countByCompanyId(@Param("companyId") UUID companyId);
    
    /**
     * 이름으로 캠페인 검색
     */
    List<Campaign> findByNameContainingIgnoreCase(String name);

    /**
     * 타겟팅 위치별 캠페인 조회 (Entity 반환)
     */
    @Query("SELECT c FROM Campaign c WHERE c.targetingLocation.id = :targetingLocationId")
    List<Campaign> findCampaignsByTargetingLocationId(@Param("targetingLocationId") UUID targetingLocationId);

    /**
     * 타겟팅 위치별 캠페인 조회 (Map 형태로 반환)
     */
    @Query("SELECT c.id, c.name, c.message, c.status, c.createdAt FROM Campaign c WHERE c.targetingLocation.id = :targetingLocationId")
    List<Object[]> findCampaignsByTargetingLocationIdRaw(@Param("targetingLocationId") UUID targetingLocationId);

    /**
     * 타겟팅 위치별 캠페인 조회 (Map 형태로 반환)
     */
    default List<Map<String, Object>> findByTargetingLocationId(UUID targetingLocationId) {
        List<Object[]> results = findCampaignsByTargetingLocationIdRaw(targetingLocationId);
        return results.stream().map(row -> {
            Map<String, Object> campaign = new java.util.HashMap<>();
            campaign.put("id", row[0]);
            campaign.put("name", row[1]);
            campaign.put("message", row[2]);
            campaign.put("status", row[3]);
            campaign.put("createdAt", row[4]);
            return campaign;
        }).toList();
    }

    /**
     * 최근 캠페인 목록과 통계 조회
     */
    @Query("SELECT c.id, c.name, c.status, c.createdAt, tl.name as locationName, " +
           "COUNT(d) as messageCount " +
           "FROM Campaign c " +
           "LEFT JOIN c.targetingLocation tl " +
           "LEFT JOIN Delivery d ON d.campaign = c " +
           "GROUP BY c.id, c.name, c.status, c.createdAt, tl.name " +
           "ORDER BY c.createdAt DESC")
    List<Object[]> getRecentCampaignsWithStats();
    
    /**
     * 모든 캠페인을 생성일 기준 내림차순으로 조회
     */
    List<Campaign> findAllByOrderByCreatedAtDesc();
}
