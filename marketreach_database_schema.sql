-- MarketReach 데이터베이스 스키마 생성 스크립트
-- PostgreSQL 기반으로 작성됨

-- 1. 기존 테이블들 삭제 (순서 주의: 외래키 의존성 때문에)
DROP TABLE IF EXISTS deliveries CASCADE;
DROP TABLE IF EXISTS qr_events CASCADE;
DROP TABLE IF EXISTS targeting_locations CASCADE;
DROP TABLE IF EXISTS campaigns CASCADE;
DROP TABLE IF EXISTS customers CASCADE;
DROP TABLE IF EXISTS companies CASCADE;

-- 2. companies 테이블 생성
CREATE TABLE companies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    industry VARCHAR(255),
    business_number VARCHAR(255),
    address VARCHAR(255),
    phone VARCHAR(255),
    email VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. customers 테이블 생성
CREATE TABLE customers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL UNIQUE,
    lat DOUBLE PRECISION NOT NULL,
    lng DOUBLE PRECISION NOT NULL,
    dong_code VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 4. targeting_locations 테이블 생성
CREATE TABLE targeting_locations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID,
    name VARCHAR(255) NOT NULL,
    center_lat DOUBLE PRECISION NOT NULL,
    center_lng DOUBLE PRECISION NOT NULL,
    radius_m INTEGER NOT NULL,
    memo TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_targeting_locations_company 
        FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE SET NULL
);

-- 5. campaigns 테이블 생성
CREATE TABLE campaigns (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'SENDING', 'COMPLETED', 'PAUSED', 'CANCELLED')),
    targeting_location_id UUID,
    company_id UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_campaigns_targeting_location 
        FOREIGN KEY (targeting_location_id) REFERENCES targeting_locations(id) ON DELETE SET NULL,
    CONSTRAINT fk_campaigns_company 
        FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE SET NULL
);

-- 6. deliveries 테이블 생성
CREATE TABLE deliveries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    campaign_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    message_text_sent TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'SENT', 'SUCCESS', 'FAILED')),
    error_code VARCHAR(100),
    sent_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_deliveries_campaign 
        FOREIGN KEY (campaign_id) REFERENCES campaigns(id) ON DELETE CASCADE,
    CONSTRAINT fk_deliveries_customer 
        FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);

-- 7. qr_events 테이블 생성 (QR 코드 이벤트 추적용)
CREATE TABLE qr_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL,
    campaign_id UUID,
    delivery_id UUID,
    event_type VARCHAR(50) NOT NULL,
    event_data JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_qr_events_customer 
        FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    CONSTRAINT fk_qr_events_campaign 
        FOREIGN KEY (campaign_id) REFERENCES campaigns(id) ON DELETE SET NULL,
    CONSTRAINT fk_qr_events_delivery 
        FOREIGN KEY (delivery_id) REFERENCES deliveries(id) ON DELETE SET NULL
);

-- 8. 인덱스 생성 (성능 최적화)
CREATE INDEX idx_customers_phone ON customers(phone);
CREATE INDEX idx_customers_location ON customers(lat, lng);
CREATE INDEX idx_customers_dong_code ON customers(dong_code);
CREATE INDEX idx_campaigns_company_id ON campaigns(company_id);
CREATE INDEX idx_campaigns_status ON campaigns(status);
CREATE INDEX idx_campaigns_created_at ON campaigns(created_at);
CREATE INDEX idx_targeting_locations_company_id ON targeting_locations(company_id);
CREATE INDEX idx_targeting_locations_center ON targeting_locations(center_lat, center_lng);
CREATE INDEX idx_deliveries_campaign_id ON deliveries(campaign_id);
CREATE INDEX idx_deliveries_customer_id ON deliveries(customer_id);
CREATE INDEX idx_deliveries_status ON deliveries(status);
CREATE INDEX idx_deliveries_created_at ON deliveries(created_at);
CREATE INDEX idx_qr_events_customer_id ON qr_events(customer_id);
CREATE INDEX idx_qr_events_campaign_id ON qr_events(campaign_id);
CREATE INDEX idx_qr_events_created_at ON qr_events(created_at);

-- 9. 샘플 데이터 삽입

-- 회사 데이터
INSERT INTO companies (name, industry, business_number, address, phone, email) VALUES
('KT Corporation', 'IT/통신', '123-45-67890', '서울특별시 강남구 테헤란로 152', '02-1234-5678', 'contact@kt.com'),
('SK Telecom', 'IT/통신', '987-65-43210', '서울특별시 중구 을지로 65', '02-9876-5432', 'contact@sk.com'),
('LG Uplus', 'IT/통신', '456-78-90123', '서울특별시 영등포구 여의대로 128', '02-4567-8901', 'contact@lguplus.co.kr'),
('삼성전자', '전자제품', '789-01-23456', '경기도 수원시 영통구 삼성로 129', '031-7890-1234', 'contact@samsung.com'),
('현대자동차', '자동차', '321-54-67890', '서울특별시 강남구 테헤란로 140', '02-3215-4678', 'contact@hyundai.com');

-- 고객 데이터 (서울 주요 지역별) - 500명 생성
-- 강남구 고객들 (100명)
INSERT INTO customers (name, phone, lat, lng, dong_code)
SELECT 
    '고객' || LPAD(ROW_NUMBER() OVER ()::TEXT, 3, '0') as name,
    '010-' || LPAD(FLOOR(RANDOM() * 9999)::TEXT, 4, '0') || '-' || LPAD(FLOOR(RANDOM() * 9999)::TEXT, 4, '0') as phone,
    37.498000 + (RANDOM() - 0.5) * 0.01 as lat,
    127.027600 + (RANDOM() - 0.5) * 0.01 as lng,
    '1168010100' as dong_code
FROM generate_series(1, 100);

-- 홍대/마포구 고객들 (80명)
INSERT INTO customers (name, phone, lat, lng, dong_code)
SELECT 
    '고객' || LPAD((ROW_NUMBER() OVER () + 100)::TEXT, 3, '0') as name,
    '010-' || LPAD(FLOOR(RANDOM() * 9999)::TEXT, 4, '0') || '-' || LPAD(FLOOR(RANDOM() * 9999)::TEXT, 4, '0') as phone,
    37.557000 + (RANDOM() - 0.5) * 0.008 as lat,
    126.925000 + (RANDOM() - 0.5) * 0.008 as lng,
    '1144012400' as dong_code
FROM generate_series(1, 80);

-- 명동/중구 고객들 (70명)
INSERT INTO customers (name, phone, lat, lng, dong_code)
SELECT 
    '고객' || LPAD((ROW_NUMBER() OVER () + 180)::TEXT, 3, '0') as name,
    '010-' || LPAD(FLOOR(RANDOM() * 9999)::TEXT, 4, '0') || '-' || LPAD(FLOOR(RANDOM() * 9999)::TEXT, 4, '0') as phone,
    37.563000 + (RANDOM() - 0.5) * 0.006 as lat,
    126.983000 + (RANDOM() - 0.5) * 0.006 as lng,
    '1114010100' as dong_code
FROM generate_series(1, 70);

-- 강북구 고객들 (60명)
INSERT INTO customers (name, phone, lat, lng, dong_code)
SELECT 
    '고객' || LPAD((ROW_NUMBER() OVER () + 250)::TEXT, 3, '0') as name,
    '010-' || LPAD(FLOOR(RANDOM() * 9999)::TEXT, 4, '0') || '-' || LPAD(FLOOR(RANDOM() * 9999)::TEXT, 4, '0') as phone,
    37.639000 + (RANDOM() - 0.5) * 0.008 as lat,
    127.025000 + (RANDOM() - 0.5) * 0.008 as lng,
    '1130510100' as dong_code
FROM generate_series(1, 60);

-- 서초구 고객들 (50명)
INSERT INTO customers (name, phone, lat, lng, dong_code)
SELECT 
    '고객' || LPAD((ROW_NUMBER() OVER () + 310)::TEXT, 3, '0') as name,
    '010-' || LPAD(FLOOR(RANDOM() * 9999)::TEXT, 4, '0') || '-' || LPAD(FLOOR(RANDOM() * 9999)::TEXT, 4, '0') as phone,
    37.483000 + (RANDOM() - 0.5) * 0.01 as lat,
    127.032000 + (RANDOM() - 0.5) * 0.01 as lng,
    '1165010100' as dong_code
FROM generate_series(1, 50);

-- 송파구 고객들 (40명)
INSERT INTO customers (name, phone, lat, lng, dong_code)
SELECT 
    '고객' || LPAD((ROW_NUMBER() OVER () + 360)::TEXT, 3, '0') as name,
    '010-' || LPAD(FLOOR(RANDOM() * 9999)::TEXT, 4, '0') || '-' || LPAD(FLOOR(RANDOM() * 9999)::TEXT, 4, '0') as phone,
    37.514000 + (RANDOM() - 0.5) * 0.012 as lat,
    127.105000 + (RANDOM() - 0.5) * 0.012 as lng,
    '1171010100' as dong_code
FROM generate_series(1, 40);

-- 영등포구 고객들 (40명)
INSERT INTO customers (name, phone, lat, lng, dong_code)
SELECT 
    '고객' || LPAD((ROW_NUMBER() OVER () + 400)::TEXT, 3, '0') as name,
    '010-' || LPAD(FLOOR(RANDOM() * 9999)::TEXT, 4, '0') || '-' || LPAD(FLOOR(RANDOM() * 9999)::TEXT, 4, '0') as phone,
    37.526000 + (RANDOM() - 0.5) * 0.01 as lat,
    126.896000 + (RANDOM() - 0.5) * 0.01 as lng,
    '1156010100' as dong_code
FROM generate_series(1, 40);

-- 종로구 고객들 (30명)
INSERT INTO customers (name, phone, lat, lng, dong_code)
SELECT 
    '고객' || LPAD((ROW_NUMBER() OVER () + 440)::TEXT, 3, '0') as name,
    '010-' || LPAD(FLOOR(RANDOM() * 9999)::TEXT, 4, '0') || '-' || LPAD(FLOOR(RANDOM() * 9999)::TEXT, 4, '0') as phone,
    37.573000 + (RANDOM() - 0.5) * 0.008 as lat,
    126.979000 + (RANDOM() - 0.5) * 0.008 as lng,
    '1111010100' as dong_code
FROM generate_series(1, 30);

-- 성동구 고객들 (30명)
INSERT INTO customers (name, phone, lat, lng, dong_code)
SELECT 
    '고객' || LPAD((ROW_NUMBER() OVER () + 470)::TEXT, 3, '0') as name,
    '010-' || LPAD(FLOOR(RANDOM() * 9999)::TEXT, 4, '0') || '-' || LPAD(FLOOR(RANDOM() * 9999)::TEXT, 4, '0') as phone,
    37.550000 + (RANDOM() - 0.5) * 0.01 as lat,
    127.040000 + (RANDOM() - 0.5) * 0.01 as lng,
    '1120010100' as dong_code
FROM generate_series(1, 30);

-- 전화번호 중복 방지를 위한 업데이트
UPDATE customers 
SET phone = '010-' || LPAD(FLOOR(RANDOM() * 9999)::TEXT, 4, '0') || '-' || LPAD(FLOOR(RANDOM() * 9999)::TEXT, 4, '0')
WHERE id IN (
    SELECT id FROM customers 
    WHERE phone IN (
        SELECT phone FROM customers 
        GROUP BY phone 
        HAVING COUNT(*) > 1
    )
);

-- 위치 기반 타겟팅 데이터
INSERT INTO targeting_locations (company_id, name, center_lat, center_lng, radius_m, memo) VALUES
((SELECT id FROM companies WHERE name = 'KT Corporation'), '강남역 핫플', 37.498000, 127.027600, 5000, '강남역 기준 5km 반경'),
((SELECT id FROM companies WHERE name = 'SK Telecom'), '홍대 문화지구', 37.557000, 126.925000, 3000, '홍대입구 기준 3km 반경'),
((SELECT id FROM companies WHERE name = 'LG Uplus'), '명동 관광지', 37.563000, 126.983000, 2000, '명동 기준 2km 반경'),
((SELECT id FROM companies WHERE name = '삼성전자'), '강북 지역', 37.639000, 127.025000, 4000, '강북구청 기준 4km 반경'),
((SELECT id FROM companies WHERE name = '현대자동차'), '서울 전체', 37.566500, 126.978000, 15000, '서울시청 기준 15km 반경');

-- 캠페인 데이터
INSERT INTO campaigns (name, message, description, status, company_id, targeting_location_id) VALUES
('강남역 핫플 프로모션', '강남역 근처 고객 대상 특별 할인! 20% 할인 쿠폰을 받아보세요.', '강남역 근처 고객 대상 특별 할인 캠페인', 'ACTIVE', 
 (SELECT id FROM companies WHERE name = 'KT Corporation'), 
 (SELECT id FROM targeting_locations WHERE name = '강남역 핫플')),

('홍대 문화축제', '홍대입구 근처 고객 대상 문화 이벤트! 공연 티켓 50% 할인', '홍대입구 근처 고객 대상 문화 이벤트', 'ACTIVE',
 (SELECT id FROM companies WHERE name = 'SK Telecom'),
 (SELECT id FROM targeting_locations WHERE name = '홍대 문화지구')),

('명동 관광 특가', '명동 관광객 대상 특별 혜택! 쇼핑몰 할인권 증정', '명동 관광객 대상 특별 혜택', 'DRAFT',
 (SELECT id FROM companies WHERE name = 'LG Uplus'),
 (SELECT id FROM targeting_locations WHERE name = '명동 관광지')),

('강북 지역 이벤트', '강북 지역 주민 대상 특별 이벤트! 지역 상품권 증정', '강북 지역 주민 대상 특별 이벤트', 'PAUSED',
 (SELECT id FROM companies WHERE name = '삼성전자'),
 (SELECT id FROM targeting_locations WHERE name = '강북 지역')),

('서울 전체 프로모션', '서울 전체 고객 대상 대규모 프로모션! 다양한 혜택 제공', '서울 전체 고객 대상 대규모 프로모션', 'COMPLETED',
 (SELECT id FROM companies WHERE name = '현대자동차'),
 (SELECT id FROM targeting_locations WHERE name = '서울 전체'));

-- 배송 데이터 (샘플)
INSERT INTO deliveries (campaign_id, customer_id, message_text_sent, status, sent_at)
SELECT 
    c.id as campaign_id,
    cust.id as customer_id,
    c.message as message_text_sent,
    CASE 
        WHEN random() > 0.8 THEN 'FAILED'
        WHEN random() > 0.6 THEN 'PENDING'
        ELSE 'SUCCESS'
    END as status,
    CASE 
        WHEN random() > 0.6 THEN CURRENT_TIMESTAMP - INTERVAL '1 hour' * random() * 24
        ELSE NULL
    END as sent_at
FROM campaigns c
CROSS JOIN customers cust
WHERE random() < 0.3  -- 30% 확률로 배송 데이터 생성
LIMIT 50;

-- 10. 테이블 상태 확인 쿼리
SELECT 'companies' as table_name, COUNT(*) as record_count FROM companies
UNION ALL
SELECT 'customers' as table_name, COUNT(*) as record_count FROM customers
UNION ALL
SELECT 'campaigns' as table_name, COUNT(*) as record_count FROM campaigns
UNION ALL
SELECT 'targeting_locations' as table_name, COUNT(*) as record_count FROM targeting_locations
UNION ALL
SELECT 'deliveries' as table_name, COUNT(*) as record_count FROM deliveries
UNION ALL
SELECT 'qr_events' as table_name, COUNT(*) as record_count FROM qr_events
ORDER BY table_name;

-- 11. 유용한 뷰 생성

-- 캠페인 상세 정보 뷰
CREATE OR REPLACE VIEW campaign_details AS
SELECT 
    c.id,
    c.name as campaign_name,
    c.message,
    c.description,
    c.status,
    c.created_at,
    comp.name as company_name,
    comp.industry,
    tl.name as targeting_location_name,
    tl.center_lat,
    tl.center_lng,
    tl.radius_m
FROM campaigns c
LEFT JOIN companies comp ON c.company_id = comp.id
LEFT JOIN targeting_locations tl ON c.targeting_location_id = tl.id;

-- 배송 현황 뷰
CREATE OR REPLACE VIEW delivery_status AS
SELECT 
    d.id,
    c.name as campaign_name,
    cust.name as customer_name,
    cust.phone as customer_phone,
    d.message_text_sent,
    d.status,
    d.error_code,
    d.sent_at,
    d.created_at,
    comp.name as company_name
FROM deliveries d
JOIN campaigns c ON d.campaign_id = c.id
JOIN customers cust ON d.customer_id = cust.id
LEFT JOIN companies comp ON c.company_id = comp.id;

-- 고객 위치 기반 통계 뷰
CREATE OR REPLACE VIEW customer_location_stats AS
SELECT 
    dong_code,
    COUNT(*) as customer_count,
    AVG(lat) as avg_lat,
    AVG(lng) as avg_lng
FROM customers
WHERE dong_code IS NOT NULL
GROUP BY dong_code
ORDER BY customer_count DESC;

-- 12. 권한 설정 (필요시)
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO your_user;
-- GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO your_user;

COMMIT;
