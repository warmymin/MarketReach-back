import React from 'react';

const Customer: React.FC = () => {
  return (
    <div className="customer-page">
      <div className="page-header">
        <div className="header-content">
          <div className="header-left">
            <h1 className="page-title">고객</h1>
            <p className="page-subtitle">고객 정보를 관리하고 분석합니다</p>
          </div>
        </div>
      </div>

      <div className="page-content">
        <div className="card">
          <h3 className="card-title">고객 관리</h3>
          <p>고객 관리 기능이 여기에 구현됩니다.</p>
        </div>
      </div>
    </div>
  );
};

export default Customer;
