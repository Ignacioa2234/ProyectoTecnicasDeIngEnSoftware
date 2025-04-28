// src/components/LapsTimeReport.jsx
import React, { useState } from 'react';
import ReportService from '../services/report.service';
import './Reports.css';

export default function LapsTimeReport() {
  const [startDate, setStartDate]   = useState('');
  const [endDate, setEndDate]       = useState('');
  const [reports, setReports]       = useState([]);

  const handleGenerate = async e => {
    e.preventDefault();
    const start = `${startDate}T00:00:00`;
    const end   = `${endDate}T23:59:59`;

    try {
      const res = await ReportService.getLapsTimeReport(start, end);
      setReports(res.data);
    } catch {
      setReports([]);
    }
  };

  return (
    <div className="reports-container">
      <h2>Reporte Ingresos por Vueltas/Tiempo</h2>
      <form onSubmit={handleGenerate} className="report-form">
        <label>Desde:</label>
        <input
          type="date"
          value={startDate}
          onChange={e => setStartDate(e.target.value)}
          required
        />
        <label>Hasta:</label>
        <input
          type="date"
          value={endDate}
          onChange={e => setEndDate(e.target.value)}
          required
        />
        <button type="submit">Generar</button>
      </form>

      <table className="report-table">
        <thead>
          <tr>
            <th>Mes</th>
            <th>Ingresos</th>
            <th>Reservas</th>
          </tr>
        </thead>
        <tbody>
          {reports.map(r => (
            <tr key={r.aggregationKey}>
              <td>{r.monthName}</td>
              <td>{r.totalIncome}</td>
              <td>{r.reservationCount}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
