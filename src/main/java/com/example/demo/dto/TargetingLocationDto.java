package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TargetingLocationDto {
    private UUID id;
    private String name;
    private Double latitude;
    private Double longitude;
    private Double radius;
    private String description;
    private UUID companyId;
    private LocalDateTime createdAt;
}
