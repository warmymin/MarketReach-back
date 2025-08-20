-- deliveries 테이블 수정
-- 1. 기존 테이블 삭제
DROP TABLE IF EXISTS deliveries CASCADE;

-- 2. 새로운 구조로 테이블 생성
CREATE TABLE deliveries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    campaign_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    message_text_sent TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    error_code VARCHAR(100),
    sent_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_deliveries_campaign FOREIGN KEY (campaign_id) REFERENCES campaigns(id) ON DELETE CASCADE,
    CONSTRAINT fk_deliveries_customer FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);

-- 3. 인덱스 생성
CREATE INDEX idx_deliveries_campaign_id ON deliveries(campaign_id);
CREATE INDEX idx_deliveries_customer_id ON deliveries(customer_id);
CREATE INDEX idx_deliveries_status ON deliveries(status);
CREATE INDEX idx_deliveries_created_at ON deliveries(created_at);
