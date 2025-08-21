package com.example.demo.service;

import com.example.demo.entity.Campaign;
import com.example.demo.entity.CampaignStatus;
import com.example.demo.entity.TargetingLocation;
import com.example.demo.repository.CampaignRepository;
import com.example.demo.repository.TargetingLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

@Service
public class CampaignService {

    @Autowired
    private CampaignRepository campaignRepository;
    
    @Autowired
    private TargetingLocationRepository targetingLocationRepository;

    public Campaign createCampaign(Campaign campaign) {
        return campaignRepository.save(campaign);
    }

    public List<Campaign> getAllCampaigns() {
        return campaignRepository.findAllByOrderByCreatedAtDesc();
    }
    


    public Optional<Campaign> getCampaignById(UUID id) {
        return campaignRepository.findById(id);
    }

    public Page<Campaign> getCampaignsByCompanyId(UUID companyId, Pageable pageable) {
        return campaignRepository.findByCompanyId(companyId, pageable);
    }

    public List<Campaign> searchCampaignsByName(String name) {
        return campaignRepository.findByNameContainingIgnoreCase(name);
    }



    public Campaign updateCampaign(UUID id, Campaign campaignDetails) {
        try {
            System.out.println("CampaignService - 수정 요청 ID: " + id);
            
            Optional<Campaign> campaign = campaignRepository.findById(id);
            if (campaign.isPresent()) {
                Campaign existingCampaign = campaign.get();
                
                System.out.println("CampaignService - 기존 캠페인 정보:");
                System.out.println("  이름: " + existingCampaign.getName());
                System.out.println("  메시지: " + existingCampaign.getMessage());
                System.out.println("  상태: " + existingCampaign.getStatus());
                
                // 업데이트할 필드들
                if (campaignDetails.getName() != null) {
                    existingCampaign.setName(campaignDetails.getName());
                }
                if (campaignDetails.getMessage() != null) {
                    existingCampaign.setMessage(campaignDetails.getMessage());
                }
                if (campaignDetails.getTargetingLocation() != null) {
                    existingCampaign.setTargetingLocation(campaignDetails.getTargetingLocation());
                }
                if (campaignDetails.getStatus() != null) {
                    CampaignStatus oldStatus = existingCampaign.getStatus();
                    existingCampaign.setStatus(campaignDetails.getStatus());
                    CampaignStatus newStatus = existingCampaign.getStatus();
                    
                    // 캠페인이 DRAFT에서 SENDING으로 변경되면 발송 시작
                    if (oldStatus == CampaignStatus.DRAFT && newStatus == CampaignStatus.SENDING) {
                        System.out.println("캠페인 발송 시작: " + id);
                        // 발송 로직은 별도 서비스에서 처리
                    }
                }
                
                System.out.println("CampaignService - 수정된 캠페인 정보:");
                System.out.println("  이름: " + existingCampaign.getName());
                System.out.println("  메시지: " + existingCampaign.getMessage());
                System.out.println("  상태: " + existingCampaign.getStatus());
                
                Campaign updated = campaignRepository.save(existingCampaign);
                System.out.println("CampaignService - 캠페인 수정 완료");
                return updated;
            } else {
                System.out.println("CampaignService - 캠페인을 찾을 수 없음: " + id);
                return null;
            }
        } catch (Exception e) {
            System.err.println("CampaignService - 수정 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public boolean deleteCampaign(UUID id) {
        try {
            System.out.println("CampaignService - 삭제 요청 ID: " + id);
            
            if (campaignRepository.existsById(id)) {
                System.out.println("CampaignService - 캠페인 존재 확인됨, 삭제 시작");
                
                // 캠페인을 찾아서 관련된 배송 데이터도 함께 삭제
                Optional<Campaign> campaignOpt = campaignRepository.findById(id);
                if (campaignOpt.isPresent()) {
                    Campaign campaign = campaignOpt.get();
                    
                    // 관련된 배송 데이터 삭제 (CASCADE 대신 수동으로 처리)
                    // 이 부분은 DeliveryService를 주입받아서 처리하는 것이 좋지만,
                    // 현재는 간단히 JPA의 cascade 설정을 활용
                    campaignRepository.delete(campaign);
                }
                
                System.out.println("CampaignService - 캠페인 및 관련 데이터 삭제 완료");
                return true;
            } else {
                System.out.println("CampaignService - 캠페인을 찾을 수 없음: " + id);
                return false;
            }
        } catch (Exception e) {
            System.err.println("CampaignService - 삭제 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 캠페인 타겟팅 미리보기 (반경 내 고객 수 조회)
     */
    public Map<String, Object> previewTargeting(UUID campaignId) {
        Optional<Campaign> campaign = campaignRepository.findById(campaignId);
        if (campaign.isPresent()) {
            Campaign c = campaign.get();
            // TODO: CustomerService를 주입받아서 반경 내 고객 수를 계산
            Map<String, Object> result = new HashMap<>();
            result.put("campaign", c);
            result.put("estimatedCustomers", 0); // 임시 값
            if (c.getTargetingLocation() != null) {
                result.put("radius", c.getTargetingLocation().getRadiusM());
                result.put("center", Map.of("lat", c.getTargetingLocation().getCenterLat(), "lng", c.getTargetingLocation().getCenterLng()));
            }
            return result;
        }
        return null;
    }

    /**
     * 최근 캠페인 조회
     */
    public List<Campaign> getRecentCampaigns() {
        return campaignRepository.findAll().stream()
                .sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
                .limit(10)
                .toList();
    }
    
    /**
     * 캠페인 개수 조회
     */
    public long getCampaignsCount() {
        return campaignRepository.count();
    }
}
