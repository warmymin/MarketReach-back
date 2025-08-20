-- deliveries 테이블을 ERD에 맞게 수정
-- 1. 기존 테이블 백업
CREATE TABLE IF NOT EXISTS deliveries_backup AS SELECT * FROM deliveries;

-- 2. 기존 테이블 삭제
DROP TABLE IF EXISTS deliveries;

-- 3. 새로운 구조로 테이블 생성
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

-- 4. 인덱스 생성
CREATE INDEX idx_deliveries_campaign_id ON deliveries(campaign_id);
CREATE INDEX idx_deliveries_customer_id ON deliveries(customer_id);
CREATE INDEX idx_deliveries_status ON deliveries(status);
CREATE INDEX idx_deliveries_created_at ON deliveries(created_at);

-- 5. 기존 데이터 마이그레이션 (필요한 경우)
-- INSERT INTO deliveries (id, campaign_id, customer_id, message_text_sent, status, error_code, sent_at, created_at)
-- SELECT 
--     d.id,
--     c.id as campaign_id,
--     cu.id as customer_id,
--     d.message as message_text_sent,
--     d.status,
--     d.error_code,
--     d.sent_at,
--     d.created_at
-- FROM deliveries_backup d
-- JOIN campaigns c ON c.targeting_location_id = d.targeting_location_id
-- JOIN customers cu ON cu.id = d.customer_id;

-- 6. 백업 테이블 삭제 (마이그레이션 완료 후)
-- DROP TABLE deliveries_backup;
