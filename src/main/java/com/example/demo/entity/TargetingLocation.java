package com.example.demo.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "targeting_locations")
public class TargetingLocation {
    
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "uuid")
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = true)
    @JsonBackReference("company-targeting-locations")
    private Company company;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "center_lat", nullable = false)
    private Double centerLat;
    
    @Column(name = "center_lng", nullable = false)
    private Double centerLng;
    
    @Column(name = "radius_m", nullable = false)
    private Integer radiusM;
    
    @Column(columnDefinition = "TEXT")
    private String memo;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    // 생성자
    public TargetingLocation() {
        this.createdAt = LocalDateTime.now();
    }
    
    public TargetingLocation(Company company, String name, Double centerLat, Double centerLng, Integer radiusM) {
        this();
        this.company = company;
        this.name = name;
        this.centerLat = centerLat;
        this.centerLng = centerLng;
        this.radiusM = radiusM;
    }
    
    // Getter와 Setter
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public Company getCompany() {
        return company;
    }
    
    public void setCompany(Company company) {
        this.company = company;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Double getCenterLat() {
        return centerLat;
    }
    
    public void setCenterLat(Double centerLat) {
        this.centerLat = centerLat;
    }
    
    public Double getCenterLng() {
        return centerLng;
    }
    
    public void setCenterLng(Double centerLng) {
        this.centerLng = centerLng;
    }
    
    public Integer getRadiusM() {
        return radiusM;
    }
    
    public void setRadiusM(Integer radiusM) {
        this.radiusM = radiusM;
    }
    
    public String getMemo() {
        return memo;
    }
    
    public void setMemo(String memo) {
        this.memo = memo;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "TargetingLocation{" +
                "id=" + id +
                ", company=" + (company != null ? company.getName() : "null") +
                ", name='" + name + '\'' +
                ", centerLat=" + centerLat +
                ", centerLng=" + centerLng +
                ", radiusM=" + radiusM +
                ", memo='" + memo + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
