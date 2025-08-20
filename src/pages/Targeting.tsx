import React, { useState } from 'react';
import { Plus, Search, Filter, Edit, Trash2, MapPin, Users, Calendar } from 'lucide-react';

interface Targeting {
  id: string;
  name: string;
  location: string;
  radius: string;
  estimatedReach: number;
  campaignsInUse: number;
  lastModified: string;
}

const Targeting: React.FC = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [filter, setFilter] = useState('전체');

  const targetings: Targeting[] = [
    {
      id: '1',
      name: '강남 핫플레이스',
      location: '강남구 역삼동',
      radius: '2.0km',
      estimatedReach: 2840,
      campaignsInUse: 2,
      lastModified: '2024-08-15'
    },
    {
      id: '2',
      name: '서초 오피스타운',
      location: '서초구 서초동',
      radius: '1.5km',
      estimatedReach: 2210,
      campaignsInUse: 1,
      lastModified: '2024-08-14'
    },
    {
      id: '3',
      name: '잠실 쇼핑몰',
      location: '송파구 잠실동',
      radius: '3.0km',
      estimatedReach: 1850,
      campaignsInUse: 0,
      lastModified: '2024-08-13'
    },
    {
      id: '4',
      name: '홍대 상권',
      location: '마포구 홍대입구',
      radius: '1.0km',
      estimatedReach: 1920,
      campaignsInUse: 3,
      lastModified: '2024-08-12'
    },
    {
      id: '5',
      name: '종로 전통시장',
      location: '종로구 종로',
      radius: '800m',
      estimatedReach: 1560,
      campaignsInUse: 0,
      lastModified: '2024-08-10'
    }
  ];

  return (
    <div className="targeting-page">
      <div className="page-header">
        <div className="header-content">
          <div className="header-left">
            <h1 className="page-title">타겟팅</h1>
            <p className="page-subtitle">재사용 가능한 위치 타겟팅을 관리합니다</p>
          </div>
          <div className="header-right">
            <button className="btn btn-primary">
              <Plus size={16} />
              새 타겟팅
            </button>
          </div>
        </div>
      </div>

      <div className="page-content">
        <div className="targeting-controls">
          <div className="search-filter">
            <div className="search-box">
              <Search size={16} />
              <input
                type="text"
                placeholder="타겟팅 이름 또는 위치 검색..."
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
                <option value="사용중">사용중</option>
                <option value="미사용">미사용</option>
              </select>
            </div>
          </div>
        </div>

        <div className="card">
          <div className="card-header">
            <h3 className="card-title">타겟팅 목록</h3>
            <span className="count">총 {targetings.length}개의 타겟팅</span>
          </div>
          
          <table className="table">
            <thead>
              <tr>
                <th>이름</th>
                <th>중심위치</th>
                <th>반경</th>
                <th>예상 도달</th>
                <th>사용 중 캠페인</th>
                <th>최근 수정</th>
                <th>액션</th>
              </tr>
            </thead>
            <tbody>
              {targetings.map((targeting) => (
                <tr key={targeting.id}>
                  <td>
                    <div className="targeting-name">
                      <MapPin size={14} />
                      <span>{targeting.name}</span>
                    </div>
                  </td>
                  <td>{targeting.location}</td>
                  <td>{targeting.radius}</td>
                  <td>{targeting.estimatedReach.toLocaleString()}명</td>
                  <td>
                    {targeting.campaignsInUse > 0 ? (
                      <span className="tag tag-info">{targeting.campaignsInUse}개</span>
                    ) : (
                      <span className="tag tag-warning">미사용</span>
                    )}
                  </td>
                  <td>{targeting.lastModified}</td>
                  <td>
                    <div className="action-buttons">
                      <button className="action-btn">
                        <Edit size={14} />
                      </button>
                      <button className="action-btn">
                        <Trash2 size={14} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default Targeting;
