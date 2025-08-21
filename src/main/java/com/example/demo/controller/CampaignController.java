package com.example.demo.controller;

import com.example.demo.entity.Campaign;
import com.example.demo.entity.CampaignStatus;
import com.example.demo.entity.Company;
import com.example.demo.entity.TargetingLocation;


import com.example.demo.service.CampaignService;
import com.example.demo.service.CompanyService;
import com.example.demo.service.TargetingLocationService;

import com.example.demo.service.DeliveryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/campaigns")
@CrossOrigin(origins = "*")
public class CampaignController {

    @Autowired
    private CampaignService campaignService;
    
    @Autowired
    private CompanyService companyService;
    
    @Autowired
    private TargetingLocationService targetingLocationService;
    
    @Autowired
    private DeliveryService deliveryService;
    


    // 1) 캠페인 생성
    @PostMapping
    public ResponseEntity<?> createCampaign(@RequestBody Map<String, Object> requestData) {
        try {
            System.out.println("=== 캠페인 생성 요청 시작 ===");
            System.out.println("받은 데이터: " + requestData);
            
            // TargetingLocation 처리 (선택사항)
            TargetingLocation targetingLocation = null;
            if (requestData.get("targetingLocationId") != null) {
                // targetingLocationId 처리
                Object targetingLocationIdObj = requestData.get("targetingLocationId");
                UUID targetingId;
                
                if (targetingLocationIdObj instanceof String) {
                    try {
                        targetingId = UUID.fromString((String) targetingLocationIdObj);
                    } catch (IllegalArgumentException e) {
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", false);
                        response.put("message", "잘못된 타겟팅 위치 ID 형식입니다.");
                        return ResponseEntity.badRequest().body(response);
                    }
                } else if (targetingLocationIdObj instanceof Number) {
                    // 숫자로 전송된 경우, 모든 타겟팅 위치를 조회해서 인덱스로 찾기
                    int index = ((Number) targetingLocationIdObj).intValue();
                    List<TargetingLocation> allLocations = targetingLocationService.getAllTargetingLocations();
                    if (index < 0 || index >= allLocations.size()) {
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", false);
                        response.put("message", "타겟팅 위치를 찾을 수 없습니다.");
                        return ResponseEntity.badRequest().body(response);
                    }
                    targetingId = allLocations.get(index).getId();
                } else {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "잘못된 타겟팅 위치 ID 형식입니다.");
                    return ResponseEntity.badRequest().body(response);
                }
                
                Optional<TargetingLocation> targetingLocationOpt = targetingLocationService.getTargetingLocationById(targetingId);
                
                if (targetingLocationOpt.isEmpty()) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "타겟팅 위치를 찾을 수 없습니다.");
                    return ResponseEntity.badRequest().body(response);
                }
                
                targetingLocation = targetingLocationOpt.get();
            }
            

            
            // Campaign 객체 생성
            Campaign campaign = new Campaign();
            campaign.setName((String) requestData.get("name"));
            campaign.setMessage((String) requestData.get("message"));
            campaign.setStatus("DRAFT"); // 기본 상태
            campaign.setTargetingLocation(targetingLocation);
            
            // 회사 설정 (타겟팅 위치가 있으면 해당 회사, 없으면 기본 회사)
            if (targetingLocation != null) {
                campaign.setCompany(targetingLocation.getCompany());
            } else {
                // 기본 회사 설정 (첫 번째 회사 사용)
                List<Company> companies = companyService.getAllCompanies();
                if (!companies.isEmpty()) {
                    campaign.setCompany(companies.get(0));
                }
            }
            
            Campaign created = campaignService.createCampaign(campaign);
            return ResponseEntity.status(CREATED).body(Map.of(
                    "success", true,
                    "message", "캠페인이 성공적으로 생성되었습니다.",
                    "data", created
            ));
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "데이터 무결성 오류: " + e.getMostSpecificCause().getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "캠페인 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 2) 전체 조회
    @GetMapping
    public ResponseEntity<?> getAllCampaigns() {
        return ResponseEntity.ok(Map.of("success", true, "data", campaignService.getAllCampaigns()));
    }
    
    // 2-1) 캠페인별 통계 정보 조회
    @GetMapping("/{id}/stats")
    public ResponseEntity<?> getCampaignStats(@PathVariable UUID id) {
        try {
            Optional<Campaign> campaignOpt = campaignService.getCampaignById(id);
            if (campaignOpt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "message", "캠페인을 찾을 수 없습니다."
                ));
            }
            
            Campaign campaign = campaignOpt.get();
            Map<String, Object> stats = new HashMap<>();
            
            // 타겟팅 정보 - targetingLocationId로 직접 조회
            UUID targetingLocationId = campaign.getTargetingLocationId();
            if (targetingLocationId != null) {
                Optional<TargetingLocation> targetingLocationOpt = targetingLocationService.getTargetingLocationById(targetingLocationId);
                if (targetingLocationOpt.isPresent()) {
                    TargetingLocation location = targetingLocationOpt.get();
                    stats.put("targetingLocationName", location.getName());
                    stats.put("targetingRadiusM", location.getRadiusM());
                } else {
                    stats.put("targetingLocationName", null);
                    stats.put("targetingRadiusM", null);
                }
            } else {
                stats.put("targetingLocationName", null);
                stats.put("targetingRadiusM", null);
            }
            
            // 발송 통계
            long totalDeliveries = deliveryService.countDeliveriesByCampaignId(id);
            long successfulDeliveries = deliveryService.countSuccessfulDeliveriesByCampaignId(id);
            double successRate = totalDeliveries > 0 ? (double) successfulDeliveries / totalDeliveries * 100 : 0.0;
            
            stats.put("totalDeliveries", totalDeliveries);
            stats.put("successfulDeliveries", successfulDeliveries);
            stats.put("successRate", Math.round(successRate * 100.0) / 100.0);
            
            return ResponseEntity.ok(Map.of("success", true, "data", stats));
            
        } catch (Exception e) {
            System.err.println("캠페인 통계 조회 오류: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "통계 정보 조회 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }

    // 3) 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> getCampaignById(@PathVariable UUID id) {
        Campaign c = campaignService.getCampaignById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "캠페인을 찾을 수 없습니다."));
        return ResponseEntity.ok(Map.of("success", true, "data", c));
    }

    // 4) 회사별 페이지 조회
    @GetMapping("/company/{companyId}")
    public ResponseEntity<?> getCampaignsByCompany(
            @PathVariable UUID companyId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {

        companyService.getCompanyById(companyId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "회사를 찾을 수 없습니다."));

        Page<Campaign> p = campaignService.getCampaignsByCompanyId(companyId, PageRequest.of(page, size));
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", p.getContent(),
                "totalElements", p.getTotalElements(),
                "totalPages", p.getTotalPages(),
                "currentPage", p.getNumber()
        ));
    }

    // 5) 이름 검색
    @GetMapping("/search/name")
    public ResponseEntity<?> searchCampaignsByName(@RequestParam String name) {
        return ResponseEntity.ok(Map.of("success", true, "data", campaignService.searchCampaignsByName(name)));
    }

    // 6) 최근 캠페인 조회
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentCampaigns() {
        try {
            List<Campaign> recentCampaigns = campaignService.getRecentCampaigns();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", recentCampaigns
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "최근 캠페인 조회 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }



    // 9) 타겟 미리보기
    @GetMapping("/{id}/preview-targeting")
    public ResponseEntity<?> previewTargeting(@PathVariable UUID id) {
        Map<String, Object> preview = campaignService.previewTargeting(id);
        if (preview == null) throw new ResponseStatusException(NOT_FOUND, "캠페인을 찾을 수 없습니다.");
        return ResponseEntity.ok(Map.of("success", true, "data", preview));
    }

    // 10) 수정
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCampaign(@PathVariable UUID id,
                                            @RequestBody Map<String, Object> requestData) {
        try {
            System.out.println("=== 캠페인 수정 요청 시작 ===");
            System.out.println("캠페인 ID: " + id);
            System.out.println("받은 데이터: " + requestData);
            
            // 기존 캠페인 조회
            Optional<Campaign> existingCampaignOpt = campaignService.getCampaignById(id);
            if (existingCampaignOpt.isEmpty()) {
                return ResponseEntity.status(NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "캠페인을 찾을 수 없습니다."
                ));
            }
            
            Campaign existingCampaign = existingCampaignOpt.get();
            
            // 이름과 메시지 업데이트
            if (requestData.get("name") != null) {
                existingCampaign.setName((String) requestData.get("name"));
            }
            if (requestData.get("message") != null) {
                existingCampaign.setMessage((String) requestData.get("message"));
            }
            
            // 타겟팅 위치 업데이트 (선택적)
            if (requestData.get("targetingLocationId") != null) {
                UUID targetingId = UUID.fromString((String) requestData.get("targetingLocationId"));
                Optional<TargetingLocation> targetingLocationOpt = targetingLocationService.getTargetingLocationById(targetingId);
                
                if (targetingLocationOpt.isPresent()) {
                    existingCampaign.setTargetingLocation(targetingLocationOpt.get());
                }
            }
            
            // 상태 업데이트 (선택적)
            if (requestData.get("status") != null) {
                existingCampaign.setStatus((String) requestData.get("status"));
            }
            
            Campaign updated = campaignService.updateCampaign(id, existingCampaign);
            return ResponseEntity.ok(Map.of(
                "success", true, 
                "message", "캠페인 정보가 성공적으로 수정되었습니다.", 
                "data", updated
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "잘못된 요청: " + e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "캠페인 수정 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }

    // 11) 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCampaign(@PathVariable UUID id) {
        try {
            System.out.println("=== 캠페인 삭제 요청 시작 ===");
            System.out.println("삭제 요청된 캠페인 ID: " + id);
            
            boolean deleted = campaignService.deleteCampaign(id);
            
            if (!deleted) {
                System.out.println("캠페인을 찾을 수 없음: " + id);
                return ResponseEntity.status(NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "캠페인을 찾을 수 없습니다."
                ));
            }
            
            System.out.println("캠페인 삭제 성공: " + id);
            return ResponseEntity.ok(Map.of(
                "success", true, 
                "message", "캠페인이 성공적으로 삭제되었습니다."
            ));
        } catch (Exception e) {
            System.err.println("캠페인 삭제 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "캠페인 삭제 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }
    
    // 12) 캠페인 발송 시뮬레이션
    @PostMapping("/{id}/send")
    public ResponseEntity<?> sendCampaign(@PathVariable UUID id) {
        try {
            System.out.println("=== 캠페인 발송 요청 시작 ===");
            System.out.println("캠페인 ID: " + id);
            
            // 캠페인 존재 확인
            Optional<Campaign> campaignOpt = campaignService.getCampaignById(id);
            if (campaignOpt.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "캠페인을 찾을 수 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            Campaign campaign = campaignOpt.get();
            
            // 이미 발송된 캠페인인지 확인
            if (campaign.getStatus() == CampaignStatus.COMPLETED || campaign.getStatus() == CampaignStatus.SENDING) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "이미 발송된 캠페인입니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 타겟팅 위치 확인
            if (campaign.getTargetingLocationId() == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "타겟팅 위치가 설정되지 않은 캠페인입니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 발송 실행
            Map<String, Object> deliveryResult = deliveryService.simulateCampaignDelivery(id);
            
            // 캠페인 상태를 COMPLETED로 변경 (즉시 발송 완료)
            campaign.setStatus("COMPLETED");
            campaignService.updateCampaign(id, campaign);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "캠페인 발송이 완료되었습니다.");
            response.put("data", deliveryResult);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "캠페인 발송 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 13) 캠페인별 발송 통계 조회
    @GetMapping("/{id}/delivery-stats")
    public ResponseEntity<?> getCampaignDeliveryStats(@PathVariable UUID id) {
        try {
            Map<String, Object> stats = deliveryService.getCampaignDeliveryStats(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", stats
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "발송 통계 조회 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }
}
