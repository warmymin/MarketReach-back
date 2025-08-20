import React from 'react';

const DeliveryStatus: React.FC = () => {
  return (
    <div className="delivery-status-page">
      <div className="page-header">
        <div className="header-content">
          <div className="header-left">
            <h1 className="page-title">발송 현황</h1>
            <p className="page-subtitle">실시간 발송 현황을 모니터링하세요</p>
          </div>
        </div>
      </div>

      <div className="page-content">
        <div className="grid grid-4">
          <div className="card">
            <h3 className="card-title">총 발송</h3>
            <div className="metric">15,420</div>
            <div className="metric-change">+0%</div>
          </div>
          <div className="card">
            <h3 className="card-title">성공</h3>
            <div className="metric">12,680</div>
            <div className="metric-change positive">+12%</div>
          </div>
          <div className="card">
            <h3 className="card-title">실패</h3>
            <div className="metric">890</div>
            <div className="metric-change negative">-5%</div>
          </div>
          <div className="card">
            <h3 className="card-title">대기중</h3>
            <div className="metric">1,850</div>
            <div className="metric-change warning">+15%</div>
          </div>
        </div>

        <div className="card">
          <h3 className="card-title">실시간 발송 현황</h3>
          <p>실시간 발송 현황 차트가 여기에 표시됩니다.</p>
        </div>
      </div>
    </div>
  );
};

export default DeliveryStatus;
