package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignStatsDto {
    private UUID id;
    private String name;
    private String message;
    private String description;
    private String imageUrl;
    private String imageAlt;
    private String status;
    private UUID companyId;
    private UUID targetingLocationId;
    private LocalDateTime createdAt;
    
    // 통계 정보
    private String targetingLocationName;
    private Integer targetingRadiusM;
    private Long totalDeliveries;
    private Long successfulDeliveries;
    private Double successRate;
}
