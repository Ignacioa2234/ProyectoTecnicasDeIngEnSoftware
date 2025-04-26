// src/App.jsx
import React from 'react';
import { Routes, Route, Link } from 'react-router-dom';
import Login from './components/Login';
import ReservationForm from './components/ReservationForm';

export default function App() {
  return (
    <>
      <nav className="navbar navbar-expand-lg navbar-light bg-light">
        <Link className="navbar-brand" to="/">KartingApp</Link>
        <div className="navbar-nav">
          <Link className="nav-link" to="/">Crear Reserva</Link>
          <Link className="nav-link" to="/login">Login</Link>
        </div>
      </nav>

      <div className="container mt-4">
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/"       element={<ReservationForm />} />
          {/* futuras rutas aquí */}
        </Routes>
      </div>
    </>
  );
}
