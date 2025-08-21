-- 캠페인 상태 컬럼 업데이트 마이그레이션 스크립트

-- 1. CampaignStatus enum 타입 생성
DO $$ BEGIN
    CREATE TYPE campaign_status AS ENUM ('DRAFT', 'SENDING', 'COMPLETED', 'PAUSED', 'CANCELLED');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- 2. 기존 status 값들을 임시로 백업
ALTER TABLE campaigns ADD COLUMN IF NOT EXISTS status_backup VARCHAR(255);

-- 3. 기존 status 값을 백업 컬럼으로 복사
UPDATE campaigns SET status_backup = status WHERE status IS NOT NULL;

-- 4. 기존 status 컬럼 삭제
ALTER TABLE campaigns DROP COLUMN IF EXISTS status;

-- 5. 새로운 enum 타입의 status 컬럼 추가
ALTER TABLE campaigns ADD COLUMN status campaign_status DEFAULT 'DRAFT';

-- 6. 백업된 데이터를 새로운 enum 타입으로 변환하여 복원
UPDATE campaigns 
SET status = CASE 
    WHEN status_backup = 'ACTIVE' THEN 'SENDING'::campaign_status
    WHEN status_backup = 'INACTIVE' THEN 'DRAFT'::campaign_status
    WHEN status_backup = 'COMPLETED' THEN 'COMPLETED'::campaign_status
    WHEN status_backup = 'PAUSED' THEN 'PAUSED'::campaign_status
    WHEN status_backup = 'CANCELLED' THEN 'CANCELLED'::campaign_status
    ELSE 'DRAFT'::campaign_status
END
WHERE status_backup IS NOT NULL;

-- 7. 백업 컬럼 삭제
ALTER TABLE campaigns DROP COLUMN IF EXISTS status_backup;

-- 8. 상태 확인
SELECT id, name, status FROM campaigns LIMIT 5;
