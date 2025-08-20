import React, { useState } from 'react';
import { Plus, Search, Filter, Edit, Trash2, MapPin, Send, BarChart3, Calendar, Settings } from 'lucide-react';
import './Campaign.css';

interface Campaign {
  id: string;
  name: string;
  message: string;
  location: string;
  status: '진행중' | '완료' | '대기';
  sentCount: number;
  successRate: number;
  date: string;
}

const Campaign: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'list' | 'analysis'>('list');
  const [searchTerm, setSearchTerm] = useState('');
  const [filter, setFilter] = useState('전체');

  const campaigns: Campaign[] = [
    {
      id: '1',
      name: '여름 할인 이벤트',
      message: '시원한 여름! 전메뉴 20% 할인 이벤트! 지금 바로 방문하세요!',
      location: '강남 핫플레이스',
      status: '진행중',
      sentCount: 1420,
      successRate: 85.2,
      date: '2024-08-15'
    },
    {
      id: '2',
      name: '신메뉴 론칭',
      message: '새로운 시그니처 버거 출시! 첫 주문 무료배송 혜택까지!',
      location: '서울 오피스타운',
      status: '완료',
      sentCount: 890,
      successRate: 78.5,
      date: '2024-08-14'
    },
    {
      id: '3',
      name: '주말 특가 행사',
      message: '주말 한정! 모든 상품 30% 할인! 놓치면 후회할 기회!',
      location: '잠실 쇼핑몰',
      status: '대기',
      sentCount: 0,
      successRate: 0,
      date: '2024-08-16'
    }
  ];

  const getStatusColor = (status: string) => {
    switch (status) {
      case '진행중':
        return 'tag-success';
      case '완료':
        return 'tag-info';
      case '대기':
        return 'tag-warning';
      default:
        return 'tag-info';
    }
  };

  return (
    <div className="campaign-page">
      <div className="page-header">
        <div className="header-content">
          <div className="header-left">
            <h1 className="page-title">캠페인</h1>
          </div>
          <div className="header-right">
            <button className="btn btn-secondary">
              <Settings size={16} />
            </button>
          </div>
        </div>
      </div>

      <div className="page-content">
        <div className="campaign-tabs">
          <button
            className={`tab ${activeTab === 'list' ? 'active' : ''}`}
            onClick={() => setActiveTab('list')}
          >
            캠페인 목록
          </button>
          <button
            className={`tab ${activeTab === 'analysis' ? 'active' : ''}`}
            onClick={() => setActiveTab('analysis')}
          >
            성과 분석
          </button>
        </div>

        {activeTab === 'list' && (
          <div className="campaign-list-section">
            <div className="campaign-controls">
              <div className="search-filter">
                <div className="search-box">
                  <Search size={16} />
                  <input
                    type="text"
                    placeholder="캠페인 이름으로 검색..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="search-input"
                  />
                </div>
                <div className="filter-dropdown">
                  <Filter size={16} />
                  <select
                    value={filter}
                    onChange={(e) => setFilter(e.target.value)}
                    className="filter-select"
                  >
                    <option value="전체">전체</option>
                    <option value="진행중">진행중</option>
                    <option value="완료">완료</option>
                    <option value="대기">대기</option>
                  </select>
                </div>
              </div>
              <button className="btn btn-primary">
                <Plus size={16} />
                새 캠페인 생성
              </button>
            </div>

            <div className="space-y-6">
              {campaigns.map((campaign) => (
                <div key={campaign.id} className="bg-white rounded-xl border border-gray-200 shadow-md p-6 space-y-4">
                  {/* 상단 정보 섹션 */}
                  <div className="flex items-start justify-between">
                    {/* 좌측: 타이틀과 상태 뱃지 */}
                    <div className="flex items-center gap-2">
                      <h3 className="text-lg font-semibold text-gray-900">{campaign.name}</h3>
                      <span className="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-black text-white">
                        {campaign.status}
                      </span>
                    </div>
                    {/* 우측: 액션 버튼 */}
                    <div className="flex items-center gap-1">
                      {/* 편집 버튼 - 초안 상태에서만 표시 */}
                      {campaign.status === '대기' && (
                        <button className="inline-flex items-center px-2 py-1 text-xs font-medium text-gray-600 bg-gray-50 border border-gray-200 rounded-md hover:bg-gray-100 hover:text-gray-800 transition-colors" title="편집">
                          <Edit size={14} className="mr-1" />
                          편집
                        </button>
                      )}
                      <button className="inline-flex items-center px-2 py-1 text-xs font-medium text-red-600 bg-red-50 border border-red-200 rounded-md hover:bg-red-100 transition-colors" title="삭제">
                        <Trash2 size={14} className="mr-1" />
                        삭제
                      </button>
                    </div>
                  </div>
                  
                  {/* 캠페인 설명 박스 */}
                  <div className="bg-gray-100 rounded-lg py-3 px-4">
                    <p className="text-sm text-gray-700">
                      🌞 {campaign.message}
                    </p>
                  </div>
                  
                  {/* 하단 3열 정보 */}
                  <div className="grid grid-cols-3 gap-4">
                    {/* 왼쪽: 매장명과 지역 */}
                    <div className="flex items-start gap-2">
                      <MapPin size={16} className="text-gray-500 mt-0.5" />
                      <div>
                        <div className="font-medium text-gray-900 text-sm">{campaign.location}</div>
                        <div className="text-sm text-gray-500">강남구 역삼동</div>
                      </div>
                    </div>

                    {/* 중앙: 발송 건수 */}
                    <div className="flex items-start gap-2">
                      <Send size={16} className="text-gray-500 mt-0.5" />
                      <div className="text-sm text-gray-900">
                        {campaign.sentCount.toLocaleString()}건 발송
                      </div>
                    </div>

                    {/* 오른쪽: 성과율과 완료시간 */}
                    <div className="space-y-1">
                      <div className="flex items-start gap-2">
                        <BarChart3 size={16} className="text-gray-500 mt-0.5" />
                        <div className="text-sm text-gray-900">성과율 {campaign.successRate}%</div>
                      </div>
                      <div className="flex items-start gap-2">
                        <Calendar size={16} className="text-gray-500 mt-0.5" />
                        <div className="text-sm text-gray-500">{campaign.date}</div>
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {activeTab === 'analysis' && (
          <div className="analysis-section">
            <p>성과 분석 페이지는 추후 구현 예정입니다.</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default Campaign;
