import React from 'react';
import { NavLink } from 'react-router-dom';
import { 
  Home, 
  Target, 
  MapPin, 
  Send, 
  BarChart3, 
  Users, 
  Settings 
} from 'lucide-react';
import './Sidebar.css';

const Sidebar: React.FC = () => {
  const menuItems = [
    { path: '/dashboard', icon: Home, label: '대시보드' },
    { path: '/campaign', icon: Target, label: '캠페인' },
    { path: '/targeting', icon: MapPin, label: '타겟팅' },
    { path: '/delivery-status', icon: Send, label: '발송 현황' },
    { path: '/analysis', icon: BarChart3, label: '분석' },
    { path: '/customer', icon: Users, label: '고객' },
    { path: '/settings', icon: Settings, label: '설정' },
  ];

  return (
    <div className="sidebar">
      <div className="sidebar-header">
        <div className="logo">
          <div className="logo-icon">KT</div>
          <div className="logo-text">
            <div className="logo-title">KT MarketReach</div>
            <div className="logo-subtitle">위치 기반 마케팅</div>
          </div>
        </div>
      </div>
      
      <nav className="sidebar-nav">
        {menuItems.map((item) => {
          const Icon = item.icon;
          return (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) => 
                `nav-item ${isActive ? 'active' : ''}`
              }
            >
              <Icon size={20} />
              <span>{item.label}</span>
            </NavLink>
          );
        })}
      </nav>
    </div>
  );
};

export default Sidebar;
