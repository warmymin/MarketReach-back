package com.example.demo.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "deliveries")
public class Delivery {
    
    public enum DeliveryStatus {
        PENDING, SENT, SUCCESS, FAILED
    }
    
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "uuid")
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    @JsonBackReference("campaign-deliveries")
    private Campaign campaign;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonBackReference("customer-deliveries")
    private Customer customer;
    
    @Column(name = "message_text_sent", columnDefinition = "TEXT")
    private String messageTextSent;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status = DeliveryStatus.PENDING;
    
    @Column(name = "error_code")
    private String errorCode;
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // 생성자
    public Delivery() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Delivery(Campaign campaign, Customer customer) {
        this();
        this.campaign = campaign;
        this.customer = customer;
    }
    
    // Getter와 Setter
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public Campaign getCampaign() {
        return campaign;
    }
    
    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    public String getMessageTextSent() {
        return messageTextSent;
    }
    
    public void setMessageTextSent(String messageTextSent) {
        this.messageTextSent = messageTextSent;
    }
    
    public DeliveryStatus getStatus() {
        return status;
    }
    
    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public LocalDateTime getSentAt() {
        return sentAt;
    }
    
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
