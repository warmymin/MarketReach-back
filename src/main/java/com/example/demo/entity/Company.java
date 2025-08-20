package com.example.demo.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "companies")
public class Company {
    
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "uuid")
    private UUID id;
    
    @Column(nullable = false)
    private String name;
    
    @Column
    private String industry;
    
    @Column(name = "business_number")
    private String businessNumber;
    
    @Column
    private String address;
    
    @Column
    private String phone;
    
    @Column
    private String email;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("company-campaigns")
    private List<Campaign> campaigns = new ArrayList<>();
    
    // 생성자
    public Company() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Company(String name, String industry) {
        this();
        this.name = name;
        this.industry = industry;
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
    
    public String getIndustry() {
        return industry;
    }
    
    public void setIndustry(String industry) {
        this.industry = industry;
    }
    
    public String getBusinessNumber() {
        return businessNumber;
    }
    
    public void setBusinessNumber(String businessNumber) {
        this.businessNumber = businessNumber;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public List<Campaign> getCampaigns() {
        return campaigns;
    }
    
    public void setCampaigns(List<Campaign> campaigns) {
        this.campaigns = campaigns;
    }
    
    // 편의 메서드
    public void addCampaign(Campaign campaign) {
        campaigns.add(campaign);
        campaign.setCompany(this);
    }
    
    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", industry='" + industry + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
