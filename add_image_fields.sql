-- 기존 campaigns 테이블에 이미지 필드 추가
-- PostgreSQL 마이그레이션 스크립트

-- 1. image_url 필드 추가
ALTER TABLE campaigns ADD COLUMN IF NOT EXISTS image_url TEXT;

-- 2. image_alt 필드 추가
ALTER TABLE campaigns ADD COLUMN IF NOT EXISTS image_alt TEXT;

-- 3. 변경사항 확인
SELECT column_name, data_type, is_nullable 
FROM information_schema.columns 
WHERE table_name = 'campaigns' 
AND column_name IN ('image_url', 'image_alt')
ORDER BY column_name;
