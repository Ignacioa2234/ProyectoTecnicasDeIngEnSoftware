// src/App.jsx
import React from 'react'
import { Routes, Route, Link } from 'react-router-dom'
import Login from './components/Login'
import ReservationForm from './components/ReservationForm'
import Reports from './components/Reports'
import WeeklyRack from './components/WeeklyRack'

export default function App() {
  return (
    <>
      <nav className="navbar navbar-expand-lg navbar-light bg-light">
        <Link className="navbar-brand" to="/">KartingApp</Link>
        <div className="navbar-nav">
          <Link className="nav-link" to="/">Crear Reserva</Link>
          <Link className="nav-link" to="/login">Login</Link>
          <Link className="nav-link" to="/reports">Reportes</Link>
          <Link className="nav-link" to="/rack">Rack Semanal</Link>
        </div>
      </nav>

      <div className="container mt-4">
        <Routes>
          <Route path="/"       element={<ReservationForm />} />
          <Route path="/login"  element={<Login />} />
          <Route path="/reports" element={<Reports />} />
          <Route path="/rack"    element={<WeeklyRack />} />
        </Routes>
      </div>
    </>
  )
}
