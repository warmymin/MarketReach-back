import React from 'react';

const Settings: React.FC = () => {
  return (
    <div className="settings-page">
      <div className="page-header">
        <div className="header-content">
          <div className="header-left">
            <h1 className="page-title">설정</h1>
            <p className="page-subtitle">시스템 설정을 관리합니다</p>
          </div>
        </div>
      </div>

      <div className="page-content">
        <div className="card">
          <h3 className="card-title">시스템 설정</h3>
          <p>시스템 설정 기능이 여기에 구현됩니다.</p>
        </div>
      </div>
    </div>
  );
};

export default Settings;
