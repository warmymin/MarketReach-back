package com.example.demo.entity;

// 캠페인 상태 enum
public enum CampaignStatus {
    DRAFT("초안"),
    SENDING("발송 중"),
    COMPLETED("발송 완료"),
    PAUSED("일시정지"),
    CANCELLED("취소됨");
    
    private final String displayName;
    
    CampaignStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getColor() {
        switch (this) {
            case DRAFT:
                return "#A0AEC0"; // 회색
            case SENDING:
                return "#4299E1"; // 파랑
            case COMPLETED:
                return "#48BB78"; // 녹색
            case PAUSED:
                return "#F6AD55"; // 주황
            case CANCELLED:
                return "#E53E3E"; // 빨강
            default:
                return "#A0AEC0";
        }
    }
    
    public boolean canSend() {
        return this == DRAFT || this == PAUSED;
    }
    
    public boolean canEdit() {
        return this == DRAFT || this == PAUSED;
    }
    
    public boolean canDelete() {
        return this == DRAFT || this == COMPLETED || this == PAUSED;
    }
}
