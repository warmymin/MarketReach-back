-- 고객 테이블에서 company_id 컬럼 제거
-- 고객은 서울 전체의 공용 데이터로 변경

-- 1. 기존 customers 테이블 백업 (선택사항)
-- CREATE TABLE customers_backup AS SELECT * FROM customers;

-- 2. company_id 컬럼 제거
ALTER TABLE customers DROP COLUMN IF EXISTS company_id;

-- 3. phone 컬럼을 unique로 변경 (고객 식별용)
ALTER TABLE customers ADD CONSTRAINT customers_phone_unique UNIQUE (phone);

-- 4. 기존 데이터 확인
SELECT 
    id,
    name,
    phone,
    lat,
    lng,
    dong_code,
    created_at
FROM customers 
ORDER BY created_at DESC;

-- 5. 고객 수 확인
SELECT COUNT(*) as total_customers FROM customers;
