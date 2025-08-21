package com.example.demo.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "campaigns")
public class Campaign {
    
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "uuid")
    private UUID id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "targeting_location_id", nullable = true)
    @JsonBackReference("targeting-location-campaigns")
    private TargetingLocation targetingLocation;
    

    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column
    private CampaignStatus status = CampaignStatus.DRAFT;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = true)
    @JsonBackReference("company-campaigns")
    private Company company;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Delivery> deliveries = new ArrayList<>();
    

    

    
    // DTO용 필드 (JSON 직렬화/역직렬화용)
    @Transient
    private UUID companyId;
    
    // 생성자
    public Campaign() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Campaign(String name, String message, TargetingLocation targetingLocation) {
        this();
        this.name = name;
        this.message = message;
        this.targetingLocation = targetingLocation;
    }
    
    // Getter와 Setter
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public TargetingLocation getTargetingLocation() {
        return targetingLocation;
    }
    
    public void setTargetingLocation(TargetingLocation targetingLocation) {
        this.targetingLocation = targetingLocation;
    }
    
    public UUID getTargetingLocationId() {
        return targetingLocation != null ? targetingLocation.getId() : null;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public CampaignStatus getStatus() {
        return status;
    }
    
    public void setStatus(CampaignStatus status) {
        this.status = status;
    }
    
    // String으로 상태를 받는 메서드 (API 호환성)
    public void setStatus(String status) {
        try {
            this.status = CampaignStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            this.status = CampaignStatus.DRAFT;
        }
    }
    
    public Company getCompany() {
        return company;
    }
    
    public void setCompany(Company company) {
        this.company = company;
        if (company != null) {
            this.companyId = company.getId();
        }
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public List<Delivery> getDeliveries() {
        return deliveries;
    }
    
    public void setDeliveries(List<Delivery> deliveries) {
        this.deliveries = deliveries;
    }
    

    

    
    public UUID getCompanyId() {
        return companyId;
    }
    
    public void setCompanyId(UUID companyId) {
        this.companyId = companyId;
    }
    

    
    @Override
    public String toString() {
        return "Campaign{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", message='" + message + '\'' +
                ", targetingLocation=" + (targetingLocation != null ? targetingLocation.getName() : "null") +
                ", status='" + status + '\'' +
                ", company=" + (company != null ? company.getName() : "null") +
                ", createdAt=" + createdAt +
                '}';
    }
}
