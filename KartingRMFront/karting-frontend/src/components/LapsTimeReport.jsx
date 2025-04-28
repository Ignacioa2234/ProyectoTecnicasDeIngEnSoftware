// src/components/LapsTimeReport.jsx
import React, { useState } from 'react';
import ReportService from '../services/report.service';
import './Reports.css';

export default function LapsTimeReport() {
  const [monthStart, setMonthStart] = useState('');
  const [monthEnd, setMonthEnd]     = useState('');
  const [rows, setRows]             = useState([]);

  const handleSubmit = async e => {
    e.preventDefault();
    const [y1, m1] = monthStart.split('-');
    const [y2, m2] = monthEnd.split('-');
    const start = new Date(y1, m1 - 1, 1).toISOString();
    const lastDay = new Date(y2, m2, 0).getDate();
    const end = new Date(y2, m2 - 1, lastDay).toISOString();

    try {
      const res = await ReportService.getLapsTime(start, end);
      setRows(res.data);
    } catch (err) {
      console.error(err);
      alert('Error al generar reporte');
    }
  };

  return (
    <div className="report-container">
      <h2>Ingresos por Vueltas / Tiempo</h2>
      <form className="report-form" onSubmit={handleSubmit}>
        <div>
          <label>Desde (mes/año)</label>
          <input
            type="month"
            value={monthStart}
            onChange={e => setMonthStart(e.target.value)}
            required
          />
        </div>
        <div>
          <label>Hasta (mes/año)</label>
          <input
            type="month"
            value={monthEnd}
            onChange={e => setMonthEnd(e.target.value)}
            required
          />
        </div>
        <button type="submit">Generar</button>
      </form>

      {rows.length > 0 && (
        <table className="report-table">
          <thead>
            <tr>
              <th>Tarifa</th>
              {rows[0].values.map((_, i) =>
                <th key={i}>{rows[0].labels[i]}</th>
              )}
              <th>Total</th>
            </tr>
          </thead>
          <tbody>
            {rows.map(row => (
              <tr key={row.category}>
                <td>{row.category}</td>
                {row.values.map((v, i) => <td key={i}>{v}</td>)}
                <td>{row.total}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
