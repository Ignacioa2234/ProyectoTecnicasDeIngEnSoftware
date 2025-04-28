import React, { useState } from 'react';
import ReportService from '../services/report.service';
import './Reports.css';

export default function LapsTimeReport() {
  const [month, setMonth] = useState('');
  const [reports, setReports] = useState([]);

  const handleGenerate = async e => {
    e.preventDefault();
    const [year, mon] = month.split('-');
    const start = `${year}-${mon}-01T00:00:00`;
    // JS: monthIndex = mon-1, day 0 of next month → last day of current
    const lastDay = new Date(year, Number(mon), 0).getDate();
    const end = `${year}-${mon}-${String(lastDay).padStart(2,'0')}T23:59:59`;

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
        <label>Mes:</label>
        <input
          type="month"
          value={month}
          onChange={e => setMonth(e.target.value)}
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
