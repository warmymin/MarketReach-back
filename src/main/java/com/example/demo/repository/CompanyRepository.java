package com.example.demo.repository;

import com.example.demo.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {
    
    /**
     * 회사명으로 회사 검색
     */
    List<Company> findByNameContainingIgnoreCase(String name);
    
    /**
     * 업종으로 회사 검색
     */
    List<Company> findByIndustryContainingIgnoreCase(String industry);
    
    /**
     * 캠페인 수를 포함한 회사 정보 조회
     */
    @Query("SELECT c, COUNT(DISTINCT ca.id) as campaignCount " +
           "FROM Company c " +
           "LEFT JOIN c.campaigns ca " +
           "GROUP BY c.id " +
           "ORDER BY c.createdAt DESC")
    List<Object[]> findCompaniesWithStats();
    
    /**
     * 특정 회사의 캠페인 수 조회
     */
    @Query("SELECT COUNT(ca) FROM Company c JOIN c.campaigns ca WHERE c.id = :companyId")
    Long countCampaignsByCompanyId(@Param("companyId") UUID companyId);
}
