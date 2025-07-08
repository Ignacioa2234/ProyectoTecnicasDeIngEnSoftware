// src/App.jsx
import React from 'react'
import { Routes, Route, Link } from 'react-router-dom'
import ReservationForm from './components/ReservationForm'
import WeeklyRack from './components/WeeklyRack'
import LapsTimeReport from './components/LapsTimeReport'
import PeopleCountReport from './components/PeopleCountReport'

export default function App() {
  return (
    <>
      <nav className="navbar navbar-expand-lg navbar-light bg-light">
        <Link className="navbar-brand" to="/">KartingApp</Link>
        <div className="navbar-nav">
          <Link className="nav-link" to="/">Crear Reserva</Link>
          <Link className="nav-link" to="/rack">Rack Semanal</Link>
          <Link className="nav-link" to="/reports/laps">Reporte Vueltas/Tiempo</Link>
          <Link className="nav-link" to="/reports/groups">Reporte # Personas</Link>
        </div>
      </nav>

      <div className="container mt-4">
        <Routes>
          <Route path="/"                   element={<ReservationForm />} />
          <Route path="/rack"               element={<WeeklyRack />} />
          <Route path="/reports/laps"       element={<LapsTimeReport />} />
          <Route path="/reports/groups"     element={<PeopleCountReport />} />
        </Routes>
      </div>
    </>
  )
}
