import React from 'react';

const Dashboard: React.FC = () => {
  return (
    <div className="dashboard-page">
      <div className="page-header">
        <div className="header-content">
          <div className="header-left">
            <h1 className="page-title">대시보드</h1>
            <p className="page-subtitle">마케팅 성과를 한눈에 확인하세요</p>
          </div>
        </div>
      </div>

      <div className="page-content">
        <div className="grid grid-4">
          <div className="card">
            <h3 className="card-title">총 캠페인</h3>
            <div className="metric">24</div>
            <div className="metric-change positive">+12%</div>
          </div>
          <div className="card">
            <h3 className="card-title">활성 캠페인</h3>
            <div className="metric">8</div>
            <div className="metric-change positive">+3%</div>
          </div>
          <div className="card">
            <h3 className="card-title">총 발송</h3>
            <div className="metric">15,420</div>
            <div className="metric-change positive">+8%</div>
          </div>
          <div className="card">
            <h3 className="card-title">성공률</h3>
            <div className="metric">85.2%</div>
            <div className="metric-change positive">+2.1%</div>
          </div>
        </div>

        <div className="card">
          <h3 className="card-title">최근 캠페인</h3>
          <p>최근 캠페인 목록이 여기에 표시됩니다.</p>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
