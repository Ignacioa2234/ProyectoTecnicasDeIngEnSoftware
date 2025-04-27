// src/App.jsx
import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import Login from './components/Login';
import ReservationForm from './components/ReservationForm';
import WeeklyRack from './components/WeeklyRack';
import Reports from './components/Reports';

export default function App() {
  return (
    <Router>
      <nav className="navbar navbar-expand-lg navbar-light bg-light">
        <div className="container-fluid">
          <Link className="navbar-brand" to="/">KartingApp</Link>
          <div className="navbar-nav">
            <Link className="nav-link" to="/">Crear Reserva</Link>
            <Link className="nav-link" to="/weekly-rack">Rack Semanal</Link>
            <Link className="nav-link" to="/reports">Reportes</Link>
            <Link className="nav-link" to="/login">Login</Link>
          </div>
        </div>
      </nav>

      <div className="container mt-4">
        <Routes>
          <Route path="/" element={<ReservationForm />} />
          <Route path="/weekly-rack" element={<WeeklyRack />} />
          <Route path="/reports" element={<Reports />} />
          <Route path="/login" element={<Login />} />
        </Routes>
      </div>
    </Router>
  );
}
