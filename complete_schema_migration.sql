-- KT MarketReach 완전 스키마 마이그레이션 스크립트
-- 기존 테이블들을 모두 삭제하고 새로운 스키마로 재생성

-- 1. 기존 테이블들 삭제 (순서 주의: 외래키 의존성 때문에)
DROP TABLE IF EXISTS deliveries CASCADE;
DROP TABLE IF EXISTS qr_events CASCADE;
DROP TABLE IF EXISTS targeting_locations CASCADE;
DROP TABLE IF EXISTS customers CASCADE;
DROP TABLE IF EXISTS campaigns CASCADE;
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
    phone VARCHAR(255) NOT NULL,
    lat DOUBLE PRECISION NOT NULL,
    lng DOUBLE PRECISION NOT NULL,
    dong_code VARCHAR(255),
    company_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_customers_company 
        FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE
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
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    status VARCHAR(255),
    scheduled_at TIMESTAMP,
    company_id UUID NOT NULL,
    targeting_location_id UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_campaigns_company 
        FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE,
    CONSTRAINT fk_campaigns_targeting_location 
        FOREIGN KEY (targeting_location_id) REFERENCES targeting_locations(id) ON DELETE SET NULL
);

-- 6. deliveries 테이블 생성
CREATE TABLE deliveries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL,
    targeting_location_id UUID,
    status VARCHAR(20) NOT NULL CHECK (status IN ('SUCCESS', 'FAIL', 'PENDING')),
    message TEXT,
    error_code VARCHAR(100),
    delivered_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_deliveries_customer 
        FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    CONSTRAINT fk_deliveries_targeting_location 
        FOREIGN KEY (targeting_location_id) REFERENCES targeting_locations(id) ON DELETE SET NULL
);

-- 7. qr_events 테이블 생성
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

-- 8. 인덱스 생성
CREATE INDEX idx_customers_company_id ON customers(company_id);
CREATE INDEX idx_customers_location ON customers(lat, lng);
CREATE INDEX idx_campaigns_company_id ON campaigns(company_id);
CREATE INDEX idx_targeting_locations_company_id ON targeting_locations(company_id);
CREATE INDEX idx_deliveries_customer_id ON deliveries(customer_id);
CREATE INDEX idx_deliveries_targeting_location_id ON deliveries(targeting_location_id);
CREATE INDEX idx_deliveries_status ON deliveries(status);
CREATE INDEX idx_deliveries_created_at ON deliveries(created_at);
CREATE INDEX idx_qr_events_customer_id ON qr_events(customer_id);
CREATE INDEX idx_qr_events_campaign_id ON qr_events(campaign_id);
CREATE INDEX idx_qr_events_delivery_id ON qr_events(delivery_id);

-- 9. 샘플 데이터 삽입
-- 회사 데이터
INSERT INTO companies (name, industry, business_number, address, phone, email) VALUES
('KT Corporation', 'IT', '123-45-67890', '서울특별시 강남구 테헤란로 152', '02-1234-5678', 'contact@kt.com'),
('SK Telecom', 'IT', '987-65-43210', '서울특별시 중구 을지로 65', '02-9876-5432', 'contact@sk.com');

-- 고객 데이터 (위치 정보 포함)
INSERT INTO customers (name, phone, lat, lng, dong_code, company_id) VALUES
('김철수', '010-1111-1111', 37.498000, 127.027600, '1168010100', (SELECT id FROM companies WHERE name = 'KT Corporation')),
('이영희', '010-1111-1112', 37.497500, 127.028000, '1168010100', (SELECT id FROM companies WHERE name = 'KT Corporation')),
('박민수', '010-1111-1113', 37.498500, 127.027000, '1168010100', (SELECT id FROM companies WHERE name = 'KT Corporation')),
('최지영', '010-2222-2221', 37.557000, 126.925000, '1144012400', (SELECT id FROM companies WHERE name = 'KT Corporation')),
('정수민', '010-2222-2222', 37.556500, 126.925500, '1144012400', (SELECT id FROM companies WHERE name = 'KT Corporation')),
('한미영', '010-3333-3331', 37.555000, 126.936000, '1144012400', (SELECT id FROM companies WHERE name = 'SK Telecom')),
('송태호', '010-3333-3332', 37.555500, 126.936500, '1144012400', (SELECT id FROM companies WHERE name = 'SK Telecom'));

-- 캠페인 데이터
INSERT INTO campaigns (name, message, description, start_date, end_date, status, company_id, targeting_location_id) VALUES
('강남역 핫플 프로모션', '강남역 근처 고객 대상 특별 할인', '강남역 근처 고객 대상 특별 할인', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30 days', 'ACTIVE', (SELECT id FROM companies WHERE name = 'KT Corporation'), (SELECT id FROM targeting_locations WHERE name = '강남역 핫플')),
('홍대 문화축제', '홍대입구 근처 고객 대상 문화 이벤트', '홍대입구 근처 고객 대상 문화 이벤트', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '60 days', 'ACTIVE', (SELECT id FROM companies WHERE name = 'SK Telecom'), (SELECT id FROM targeting_locations WHERE name = '홍대 문화지구'));

-- 위치 기반 타겟팅 데이터
INSERT INTO targeting_locations (company_id, name, center_lat, center_lng, radius_m, memo) VALUES
((SELECT id FROM companies WHERE name = 'KT Corporation'), '강남역 핫플', 37.498000, 127.027600, 5000, '강남역 기준 5km 반경'),
((SELECT id FROM companies WHERE name = 'SK Telecom'), '홍대 문화지구', 37.557000, 126.925000, 3000, '홍대입구 기준 3km 반경');

-- 배송 데이터
INSERT INTO deliveries (customer_id, targeting_location_id, status, message, created_at, delivered_at)
SELECT 
    c.id as customer_id,
    tl.id as targeting_location_id,
    'SUCCESS' as status,
    '안녕하세요! 특별한 혜택을 확인해보세요.' as message,
    CURRENT_TIMESTAMP as created_at,
    CURRENT_TIMESTAMP + INTERVAL '5 minutes' as delivered_at
FROM customers c
CROSS JOIN targeting_locations tl
LIMIT 10;

-- 10. 테이블 상태 확인
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
SELECT 'qr_events' as table_name, COUNT(*) as record_count FROM qr_events;
