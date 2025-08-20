import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Sidebar from './components/Sidebar';
import Dashboard from './pages/Dashboard';
import Campaign from './pages/Campaign';
import Targeting from './pages/Targeting';
import DeliveryStatus from './pages/DeliveryStatus';
import Analysis from './pages/Analysis';
import Customer from './pages/Customer';
import Settings from './pages/Settings';
import './App.css';

function App() {
  return (
    <Router>
      <div className="app">
        <Sidebar />
        <div className="main-content">
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/campaign" element={<Campaign />} />
            <Route path="/targeting" element={<Targeting />} />
            <Route path="/delivery-status" element={<DeliveryStatus />} />
            <Route path="/analysis" element={<Analysis />} />
            <Route path="/customer" element={<Customer />} />
            <Route path="/settings" element={<Settings />} />
          </Routes>
        </div>
      </div>
    </Router>
  );
}

export default App;
