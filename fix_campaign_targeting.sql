-- 기존 캠페인들을 타겟팅 위치와 연결하는 스크립트

-- 1. 강남역 핫플 프로모션 캠페인을 강남역 핫플 타겟팅과 연결
UPDATE campaigns 
SET targeting_location_id = '04d785e1-b422-4b60-a362-c0aa11dec957'
WHERE id = '60566b16-cacd-486b-9e1f-e877636ebeb4' 
AND name = '강남역 핫플 프로모션';

-- 2. 홍대 문화축제 캠페인을 홍대 문화지구 타겟팅과 연결
UPDATE campaigns 
SET targeting_location_id = 'c69c20e6-b090-4756-8351-d76846220597'
WHERE id = 'c131219c-6fd0-4d8c-9cb7-787be52865f3' 
AND name = '홍대 문화축제';

-- 3. 테스트 캠페인을 테스트 타겟팅과 연결
UPDATE campaigns 
SET targeting_location_id = '74d686be-32fd-4eb4-9dc4-03dc0cb32608'
WHERE id = '6a25e5e6-a68b-48bf-8878-a0393c0772fa' 
AND name = '테스트 캠페인';

-- 4. 여름 할인 이벤트~~ 캠페인을 강남역 핫플 타겟팅과 연결
UPDATE campaigns 
SET targeting_location_id = '04d785e1-b422-4b60-a362-c0aa11dec957'
WHERE id = '741a1277-56dd-4c23-83ba-546b4b9bbc71' 
AND name = '여름 할인 이벤트~~';

-- 5. 여름 할인 이벤트트트 캠페인을 강남역 핫플 타겟팅과 연결
UPDATE campaigns 
SET targeting_location_id = '04d785e1-b422-4b60-a362-c0aa11dec957'
WHERE id = 'b76daad8-3048-41a5-a0ea-252fbf613f30' 
AND name = '여름 할인 이벤트트트';

-- 6. 여름 할인 이벤트 캠페인을 강남역 핫플입니다 타겟팅과 연결
UPDATE campaigns 
SET targeting_location_id = '7bb476b8-95c7-413e-9156-637acadca224'
WHERE id = '527454e7-4bb3-4ceb-bd50-2509bc3160d6' 
AND name = '여름 할인 이벤트';

-- 상태를 DRAFT로 변경 (발송 가능하도록)
UPDATE campaigns 
SET status = 'DRAFT' 
WHERE status IN ('ACTIVE', 'SCHEDULED', 'FAILED');

-- 결과 확인
SELECT 
    c.id,
    c.name,
    c.status,
    c.targeting_location_id,
    tl.name as targeting_location_name
FROM campaigns c
LEFT JOIN targeting_locations tl ON c.targeting_location_id = tl.id
ORDER BY c.created_at DESC;
