const axios = require('axios');

const API_BASE_URL = 'http://localhost:8083/api';

// 샘플 회사 데이터
const companies = [
  {
    name: "KT 마케팅",
    address: "서울시 강남구",
    phone: "02-1234-5678",
    email: "marketing@kt.com"
  },
  {
    name: "스타벅스",
    address: "서울시 서초구",
    phone: "02-2345-6789",
    email: "contact@starbucks.co.kr"
  },
  {
    name: "올리브영",
    address: "서울시 마포구",
    phone: "02-3456-7890",
    email: "info@oliveyoung.co.kr"
  }
];

// 샘플 고객 데이터
const customers = [
  { name: "김철수", phone: "010-1234-5678", lat: 37.498, lng: 127.0276 },
  { name: "이영희", phone: "010-2345-6789", lat: 37.501, lng: 127.0250 },
  { name: "박민수", phone: "010-3456-7890", lat: 37.495, lng: 127.0300 },
  { name: "최지영", phone: "010-4567-8901", lat: 37.503, lng: 127.0220 },
  { name: "정현우", phone: "010-5678-9012", lat: 37.497, lng: 127.0280 },
  { name: "강미영", phone: "010-6789-0123", lat: 37.502, lng: 127.0240 },
  { name: "윤성민", phone: "010-7890-1234", lat: 37.496, lng: 127.0290 },
  { name: "임수진", phone: "010-8901-2345", lat: 37.504, lng: 127.0210 },
  { name: "한동현", phone: "010-9012-3456", lat: 37.499, lng: 127.0260 },
  { name: "송예진", phone: "010-0123-4567", lat: 37.500, lng: 127.0255 }
];

// 샘플 타겟팅 위치 데이터
const targetingLocations = [
  {
    name: "강남구 강남대로",
    centerLat: 37.498,
    centerLng: 127.0276,
    radiusM: 2000,
    memo: "강남역 주변 상권"
  },
  {
    name: "서초구 서초대로",
    centerLat: 37.501,
    centerLng: 127.0250,
    radiusM: 1500,
    memo: "서초역 주변 상권"
  },
  {
    name: "마포구 홍대로",
    centerLat: 37.495,
    centerLng: 127.0300,
    radiusM: 1800,
    memo: "홍대입구역 주변 상권"
  },
  {
    name: "종로구 종로",
    centerLat: 37.503,
    centerLng: 127.0220,
    radiusM: 1200,
    memo: "종로3가역 주변 상권"
  },
  {
    name: "중구 명동",
    centerLat: 37.497,
    centerLng: 127.0280,
    radiusM: 1000,
    memo: "명동 상권"
  }
];

// 샘플 캠페인 데이터
const campaigns = [
  {
    name: "강남구 할인 이벤트",
    message: "🔥 강남역 주변 매장 20% 할인 이벤트! 지금 바로 방문하세요!",
    targetingLocationId: null, // 타겟팅 생성 후 설정
    status: "DRAFT"
  },
  {
    name: "서초구 신규 오픈",
    message: "🎉 서초구 신규 매장 오픈 기념 특별 할인! 오픈 이벤트에 참여하세요!",
    targetingLocationId: null,
    status: "DRAFT"
  },
  {
    name: "마포구 시즌 프로모션",
    message: "🍂 가을 시즌 특별 프로모션! 홍대 주변 매장에서 특별한 혜택을 만나보세요!",
    targetingLocationId: null,
    status: "DRAFT"
  },
  {
    name: "종로구 특별 할인",
    message: "🏛️ 종로3가역 주변 매장 특별 할인! 전통과 현대가 만나는 곳에서 쇼핑하세요!",
    targetingLocationId: null,
    status: "DRAFT"
  },
  {
    name: "중구 명동 특가",
    message: "🛍️ 명동 상권 특가 세일! 최고의 쇼핑 경험을 제공합니다!",
    targetingLocationId: null,
    status: "DRAFT"
  }
];

async function createCompany(companyData) {
  try {
    const response = await axios.post(`${API_BASE_URL}/companies`, companyData);
    console.log(`✅ 회사 생성 성공: ${companyData.name}`);
    return response.data.data.id;
  } catch (error) {
    console.error(`❌ 회사 생성 실패: ${companyData.name}`, error.response?.data || error.message);
    return null;
  }
}

async function createCustomer(customerData) {
  try {
    const response = await axios.post(`${API_BASE_URL}/customers`, customerData);
    console.log(`✅ 고객 생성 성공: ${customerData.name}`);
    return response.data.data.id;
  } catch (error) {
    console.error(`❌ 고객 생성 실패: ${customerData.name}`, error.response?.data || error.message);
    return null;
  }
}

async function createTargetingLocation(targetingData) {
  try {
    const response = await axios.post(`${API_BASE_URL}/targeting-locations`, targetingData);
    console.log(`✅ 타겟팅 위치 생성 성공: ${targetingData.name}`);
    return response.data.data.id;
  } catch (error) {
    console.error(`❌ 타겟팅 위치 생성 실패: ${targetingData.name}`, error.response?.data || error.message);
    return null;
  }
}

async function createCampaign(campaignData) {
  try {
    const response = await axios.post(`${API_BASE_URL}/campaigns`, campaignData);
    console.log(`✅ 캠페인 생성 성공: ${campaignData.name}`);
    return response.data.data.id;
  } catch (error) {
    console.error(`❌ 캠페인 생성 실패: ${campaignData.name}`, error.response?.data || error.message);
    return null;
  }
}

async function createSampleData() {
  console.log('🚀 샘플 데이터 생성 시작...\n');

  // 1. 회사 생성
  console.log('📊 회사 데이터 생성 중...');
  const companyIds = [];
  for (const company of companies) {
    const companyId = await createCompany(company);
    if (companyId) {
      companyIds.push(companyId);
    }
  }
  console.log(`✅ ${companyIds.length}개 회사 생성 완료\n`);

  // 2. 고객 생성
  console.log('👥 고객 데이터 생성 중...');
  const customerIds = [];
  for (const customer of customers) {
    const customerId = await createCustomer(customer);
    if (customerId) {
      customerIds.push(customerId);
    }
  }
  console.log(`✅ ${customerIds.length}개 고객 생성 완료\n`);

  // 3. 타겟팅 위치 생성
  console.log('🎯 타겟팅 위치 데이터 생성 중...');
  const targetingIds = [];
  for (const targeting of targetingLocations) {
    const targetingId = await createTargetingLocation(targeting);
    if (targetingId) {
      targetingIds.push(targetingId);
    }
  }
  console.log(`✅ ${targetingIds.length}개 타겟팅 위치 생성 완료\n`);

  // 4. 캠페인 생성 (타겟팅 ID 연결)
  console.log('📢 캠페인 데이터 생성 중...');
  const campaignIds = [];
  for (let i = 0; i < campaigns.length; i++) {
    const campaign = { ...campaigns[i] };
    if (targetingIds[i]) {
      campaign.targetingLocationId = targetingIds[i];
    }
    const campaignId = await createCampaign(campaign);
    if (campaignId) {
      campaignIds.push(campaignId);
    }
  }
  console.log(`✅ ${campaignIds.length}개 캠페인 생성 완료\n`);

  console.log('🎉 샘플 데이터 생성 완료!');
  console.log(`📊 생성된 데이터:`);
  console.log(`   - 회사: ${companyIds.length}개`);
  console.log(`   - 고객: ${customerIds.length}개`);
  console.log(`   - 타겟팅 위치: ${targetingIds.length}개`);
  console.log(`   - 캠페인: ${campaignIds.length}개`);
}

// 스크립트 실행
createSampleData().catch(console.error);
