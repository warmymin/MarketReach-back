package com.example.demo.repository;

import com.example.demo.entity.TargetingLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TargetingLocationRepository extends JpaRepository<TargetingLocation, UUID> {
    
    // 회사별 타겟팅 위치 조회
    List<TargetingLocation> findByCompanyIdOrderByCreatedAtDesc(UUID companyId);
    
    // 이름으로 검색
    List<TargetingLocation> findByNameContainingIgnoreCase(String name);
    
    // 회사별 이름으로 검색
    List<TargetingLocation> findByCompanyIdAndNameContainingIgnoreCase(UUID companyId, String name);
    
    // 특정 반경 내의 타겟팅 위치 조회 (위치 기반 검색)
    @Query(value = "SELECT * FROM targeting_location tl WHERE ST_DWithin(ST_MakePoint(tl.center_lng, tl.center_lat)::geography, ST_MakePoint(:lng, :lat)::geography, :radiusM)", nativeQuery = true)
    List<TargetingLocation> findWithinRadius(@Param("lat") Double lat, 
                                           @Param("lng") Double lng, 
                                           @Param("radiusM") Integer radiusM);

    // 예상 도달 고객 수 계산 (반경 내 고객 수)
    @Query(value = "SELECT COUNT(*) FROM customer c WHERE ST_DWithin(ST_MakePoint(c.lng, c.lat)::geography, ST_MakePoint(:lng, :lat)::geography, :radiusM)", nativeQuery = true)
    Long countCustomersInRadius(@Param("lat") Double lat, 
                               @Param("lng") Double lng, 
                               @Param("radiusM") Integer radiusM);
}
