import React from 'react';

const Analysis: React.FC = () => {
  return (
    <div className="analysis-page">
      <div className="page-header">
        <div className="header-content">
          <div className="header-left">
            <h1 className="page-title">분석</h1>
            <p className="page-subtitle">마케팅 성과와 고객 행동을 분석합니다</p>
          </div>
        </div>
      </div>

      <div className="page-content">
        <div className="grid grid-4">
          <div className="card">
            <h3 className="card-title">ROI</h3>
            <div className="metric">320%</div>
            <div className="metric-change positive">+45%</div>
          </div>
          <div className="card">
            <h3 className="card-title">전환율</h3>
            <div className="metric">12.5%</div>
            <div className="metric-change positive">+8%</div>
          </div>
          <div className="card">
            <h3 className="card-title">방문율</h3>
            <div className="metric">24.8%</div>
            <div className="metric-change negative">-15%</div>
          </div>
          <div className="card">
            <h3 className="card-title">구매율</h3>
            <div className="metric">8.7%</div>
            <div className="metric-change positive">+12%</div>
          </div>
        </div>

        <div className="card">
          <h3 className="card-title">성과 분석</h3>
          <p>성과 분석 차트가 여기에 표시됩니다.</p>
        </div>
      </div>
    </div>
  );
};

export default Analysis;
