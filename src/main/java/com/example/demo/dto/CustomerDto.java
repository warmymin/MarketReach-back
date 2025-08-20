package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private Double latitude;
    private Double longitude;
    private String address;
    private LocalDateTime createdAt;
}
