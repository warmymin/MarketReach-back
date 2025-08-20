package com.example.demo.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "customers")
public class Customer {
    
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "uuid")
    private UUID id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String phone;
    
    @Column(nullable = false)
    private Double lat;
    
    @Column(nullable = false)
    private Double lng;
    
    @Column(name = "dong_code")
    private String dongCode;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    // 생성자
    public Customer() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Customer(String name, String phone, Double lat, Double lng, String dongCode) {
        this();
        this.name = name;
        this.phone = phone;
        this.lat = lat;
        this.lng = lng;
        this.dongCode = dongCode;
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
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public Double getLat() {
        return lat;
    }
    
    public void setLat(Double lat) {
        this.lat = lat;
    }
    
    public Double getLng() {
        return lng;
    }
    
    public void setLng(Double lng) {
        this.lng = lng;
    }
    
    public String getDongCode() {
        return dongCode;
    }
    
    public void setDongCode(String dongCode) {
        this.dongCode = dongCode;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", dongCode='" + dongCode + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
