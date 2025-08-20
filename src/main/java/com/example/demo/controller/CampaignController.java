package com.example.demo.controller;

import com.example.demo.entity.Campaign;
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
            
            // TargetingLocation 먼저 조회
            if (requestData.get("targetingLocationId") == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "타겟팅 위치 ID가 필요합니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            UUID targetingId = UUID.fromString((String) requestData.get("targetingLocationId"));
            Optional<TargetingLocation> targetingLocationOpt = targetingLocationService.getTargetingLocationById(targetingId);
            
            if (targetingLocationOpt.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "타겟팅 위치를 찾을 수 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            TargetingLocation targetingLocation = targetingLocationOpt.get();
            
            // Campaign 객체 생성
            Campaign campaign = new Campaign();
            campaign.setName((String) requestData.get("name"));
            campaign.setMessage((String) requestData.get("message"));
            campaign.setStatus("DRAFT"); // 기본 상태
            campaign.setTargetingLocation(targetingLocation);
            campaign.setCompany(targetingLocation.getCompany()); // ✅ 핵심: 타겟팅 위치의 회사를 캠페인 회사로 설정
            
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
            Map<String, Object> result = deliveryService.simulateCampaignDelivery(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "캠페인 발송이 완료되었습니다.",
                "data", result
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "캠페인 발송 중 오류가 발생했습니다: " + e.getMessage()
            ));
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
