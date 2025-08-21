package com.example.demo.service;

import com.example.demo.entity.TargetingLocation;
import com.example.demo.entity.Company;
import com.example.demo.entity.Campaign;
import com.example.demo.repository.TargetingLocationRepository;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.CampaignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

@Service
public class TargetingLocationService {
    
    @Autowired
    private TargetingLocationRepository targetingLocationRepository;
    
    @Autowired
    private CompanyRepository companyRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private CampaignRepository campaignRepository;
    
    // 타겟팅 위치 생성
    public TargetingLocation createTargetingLocation(TargetingLocation targetingLocation) {
        return targetingLocationRepository.save(targetingLocation);
    }
    
    // 모든 타겟팅 위치 조회
    public List<TargetingLocation> getAllTargetingLocations() {
        return targetingLocationRepository.findAll();
    }
    
    // ID로 타겟팅 위치 조회
    public Optional<TargetingLocation> getTargetingLocationById(UUID id) {
        return targetingLocationRepository.findById(id);
    }
    
    // 숫자 ID로 타겟팅 위치 조회 (프론트엔드 호환성)
    public Optional<TargetingLocation> getTargetingLocationByNumericId(Long numericId) {
        // 모든 타겟팅 위치를 조회해서 numericId와 일치하는 것을 찾음
        List<TargetingLocation> allLocations = targetingLocationRepository.findAll();
        return allLocations.stream()
                .filter(location -> location.getId().toString().hashCode() == numericId.intValue())
                .findFirst();
    }
    
    // 회사별 타겟팅 위치 조회
    public List<TargetingLocation> getTargetingLocationsByCompany(UUID companyId) {
        return targetingLocationRepository.findByCompanyIdOrderByCreatedAtDesc(companyId);
    }
    
    // 이름으로 검색
    public List<TargetingLocation> searchTargetingLocationsByName(String name) {
        return targetingLocationRepository.findByNameContainingIgnoreCase(name);
    }
    
    // 회사별 이름으로 검색
    public List<TargetingLocation> searchTargetingLocationsByCompanyAndName(UUID companyId, String name) {
        return targetingLocationRepository.findByCompanyIdAndNameContainingIgnoreCase(companyId, name);
    }
    
    // 타겟팅 위치 수정
    public TargetingLocation updateTargetingLocation(UUID id, TargetingLocation updatedTargetingLocation) {
        Optional<TargetingLocation> existing = targetingLocationRepository.findById(id);
        if (existing.isPresent()) {
            TargetingLocation targetingLocation = existing.get();
            targetingLocation.setName(updatedTargetingLocation.getName());
            targetingLocation.setCenterLat(updatedTargetingLocation.getCenterLat());
            targetingLocation.setCenterLng(updatedTargetingLocation.getCenterLng());
            targetingLocation.setRadiusM(updatedTargetingLocation.getRadiusM());
            targetingLocation.setMemo(updatedTargetingLocation.getMemo());
            return targetingLocationRepository.save(targetingLocation);
        }
        throw new RuntimeException("타겟팅 위치를 찾을 수 없습니다: " + id);
    }
    
    // 타겟팅 위치 삭제
    public boolean deleteTargetingLocation(UUID id) {
        if (targetingLocationRepository.existsById(id)) {
            // 해당 타겟팅 위치를 참조하는 캠페인들의 타겟팅 위치를 null로 설정
            List<Campaign> campaigns = campaignRepository.findAll();
            for (Campaign campaign : campaigns) {
                if (campaign.getTargetingLocation() != null && campaign.getTargetingLocation().getId().equals(id)) {
                    campaign.setTargetingLocation(null);
                    campaignRepository.save(campaign);
                }
            }
            
            targetingLocationRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    // 예상 도달 고객 수 계산
    public Long getEstimatedReach(Double lat, Double lng, Integer radiusM) {
        return targetingLocationRepository.countCustomersInRadius(lat, lng, radiusM);
    }
    
    // 반경 내 타겟팅 위치 조회
    public List<TargetingLocation> getTargetingLocationsWithinRadius(Double lat, Double lng, Integer radiusM) {
        return targetingLocationRepository.findWithinRadius(lat, lng, radiusM);
    }
    
    // 회사 존재 여부 확인
    public boolean companyExists(UUID companyId) {
        return companyRepository.existsById(companyId);
    }

    // 타겟팅 위치별 고객 목록 조회
    public List<Map<String, Object>> getCustomersByTargeting(UUID targetingId) {
        Optional<TargetingLocation> targetingLocation = targetingLocationRepository.findById(targetingId);
        if (targetingLocation.isPresent()) {
            TargetingLocation location = targetingLocation.get();
            return customerRepository.findCustomersInRadiusWithDistance(
                location.getCenterLat(),
                location.getCenterLng(),
                location.getRadiusM()
            );
        }
        return List.of();
    }

    // 타겟팅 위치별 고객 수 조회 (간단한 버전)
    public Long getCustomerCountByTargeting(UUID targetingId) {
        Optional<TargetingLocation> targetingLocation = targetingLocationRepository.findById(targetingId);
        if (targetingLocation.isPresent()) {
            TargetingLocation location = targetingLocation.get();
            return customerRepository.countCustomersInRadius(
                location.getCenterLat(),
                location.getCenterLng(),
                location.getRadiusM()
            );
        }
        return 0L;
    }

    // 타겟팅 위치별 캠페인 목록 조회
    public List<Map<String, Object>> getCampaignsByTargeting(UUID targetingId) {
        return campaignRepository.findByTargetingLocationId(targetingId);
    }
    
    /**
     * 타겟팅 위치 개수 조회
     */
    public long getTargetingLocationsCount() {
        return targetingLocationRepository.count();
    }
}
