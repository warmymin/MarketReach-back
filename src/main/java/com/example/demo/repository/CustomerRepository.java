package com.example.demo.repository;

import com.example.demo.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.Map;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    
    /**
     * 전화번호로 고객 검색
     */
    List<Customer> findByPhoneContaining(String phone);
    
    /**
     * 이름으로 고객 검색
     */
    List<Customer> findByNameContainingIgnoreCase(String name);
    
    /**
     * 동 코드로 고객 검색
     */
    List<Customer> findByDongCode(String dongCode);
    
    /**
     * 위치 기반 고객 조회 (Haversine 공식 사용)
     */
    @Query(value = "SELECT c.* FROM customers c " +
                   "WHERE (6371000 * acos(cos(radians(:lat)) * cos(radians(c.lat)) * " +
                   "cos(radians(c.lng) - radians(:lng)) + sin(radians(:lat)) * sin(radians(c.lat)))) <= :radiusM " +
                   "ORDER BY (6371000 * acos(cos(radians(:lat)) * cos(radians(c.lat)) * " +
                   "cos(radians(c.lng) - radians(:lng)) + sin(radians(:lat)) * sin(radians(c.lat)))) ASC", 
           nativeQuery = true)
    List<Customer> findCustomersNearLocation(@Param("lat") Double lat, 
                                           @Param("lng") Double lng, 
                                           @Param("radiusM") Integer radiusM);
    
    /**
     * 간단한 거리 계산으로 고객 조회 (테스트용)
     */
    @Query(value = "SELECT c.* FROM customers c " +
                   "WHERE ABS(c.lat - :lat) <= 0.1 AND ABS(c.lng - :lng) <= 0.1", 
           nativeQuery = true)
    List<Customer> findCustomersNearLocationSimple(@Param("lat") Double lat, 
                                                 @Param("lng") Double lng);

    /**
     * 반경 내 고객 수 계산
     */
    @Query(value = "SELECT COUNT(*) FROM customers c " +
                   "WHERE (6371000 * acos(cos(radians(:lat)) * cos(radians(c.lat)) * " +
                   "cos(radians(c.lng) - radians(:lng)) + sin(radians(:lat)) * sin(radians(c.lat)))) <= :radiusM", 
           nativeQuery = true)
    Long countCustomersInRadius(@Param("lat") Double lat, 
                               @Param("lng") Double lng, 
                               @Param("radiusM") Integer radiusM);

    /**
     * 반경 내 고객 조회 (거리 정보 포함)
     */
    @Query(value = "SELECT c.id, c.name, c.phone, c.lat, c.lng, " +
                   "ROUND((6371000 * acos(cos(radians(:lat)) * cos(radians(c.lat)) * " +
                   "cos(radians(c.lng) - radians(:lng)) + sin(radians(:lat)) * sin(radians(c.lat)))) / 1000, 2) as distance " +
                   "FROM customers c " +
                   "WHERE (6371000 * acos(cos(radians(:lat)) * cos(radians(c.lat)) * " +
                   "cos(radians(c.lng) - radians(:lng)) + sin(radians(:lat)) * sin(radians(c.lat)))) <= :radiusM " +
                   "ORDER BY distance ASC", 
           nativeQuery = true)
    List<Object[]> findCustomersInRadiusWithDistanceRaw(@Param("lat") Double lat, 
                                                       @Param("lng") Double lng, 
                                                       @Param("radiusM") Integer radiusM);

    /**
     * 반경 내 고객 조회 (Map 형태로 반환)
     */
    default List<Map<String, Object>> findCustomersInRadiusWithDistance(Double lat, Double lng, Integer radiusM) {
        List<Object[]> results = findCustomersInRadiusWithDistanceRaw(lat, lng, radiusM);
        return results.stream().map(row -> {
            Map<String, Object> customer = new java.util.HashMap<>();
            customer.put("id", row[0]);
            customer.put("name", row[1]);
            customer.put("phone", row[2]);
            customer.put("lat", row[3]);
            customer.put("lng", row[4]);
            customer.put("distance", row[5]);
            return customer;
        }).toList();
    }

    /**
     * 지역별 고객 분포 조회 (dongCode 기준)
     */
    @Query("SELECT c.dongCode, COUNT(c) FROM Customer c GROUP BY c.dongCode ORDER BY COUNT(c) DESC")
    List<Object[]> getCustomerDistributionByRegion();
}
