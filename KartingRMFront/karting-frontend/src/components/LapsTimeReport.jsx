// src/components/LapsTimeReport.jsx
import React, { useState } from 'react';
import ReportService from '../services/report.service';
import './Reports.css';

export default function LapsTimeReport() {
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate]     = useState('');
  const [reports, setReports]     = useState([]);

  const handleGenerate = async e => {
    e.preventDefault();
    const start = `${startDate}T00:00:00`;
    const end   = `${endDate}T23:59:59`;
    try {
      const res = await ReportService.generateLapsTimeReport(start, end);
      setReports(res.data);
    } catch {
      setReports([]);
    }
  };

  // formatea "YYYY-MM-DD" → "Mes Año"
  const formatMonthYear = iso => {
    const d = new Date(iso);
    return d.toLocaleString('default', { month: 'long', year: 'numeric' });
  };

  // extrae meses y categorías únicas
  const months  = [...new Set(reports.map(r => r.monthName))];
  const cats    = [...new Set(reports.map(r => r.aggregationKey))]
                    .sort((a,b) => Number(a) - Number(b));

  // construye un mapa cat → mes → ingreso
  const dataMap = {};
  reports.forEach(r => {
    dataMap[r.aggregationKey] = dataMap[r.aggregationKey] || {};
    dataMap[r.aggregationKey][r.monthName] = r.totalIncome;
  });

  // totales por fila y columna
  const rowTotals = {};
  cats.forEach(c => {
    rowTotals[c] = months.reduce((sum,m) => sum + (dataMap[c][m]||0), 0);
  });
  const colTotals = {};
  months.forEach(m => {
    colTotals[m] = cats.reduce((sum,c) => sum + (dataMap[c][m]||0), 0);
  });
  const grandTotal = cats.reduce((sum,c) => sum + rowTotals[c], 0);

  return (
    <div className="reports-container">
      <h2>Reporte Ingresos por Vueltas/Tiempo</h2>
      <form onSubmit={handleGenerate} className="report-form">
        <label>Desde:</label>
        <input type="date" value={startDate} onChange={e => setStartDate(e.target.value)} required />
        <label>Hasta:</label>
        <input type="date" value={endDate}   onChange={e => setEndDate(e.target.value)}   required />
        <button type="submit">Generar</button>
      </form>

      {reports.length > 0 && (
        <>
          <table className="report-dates">
            <tbody>
              <tr><td>Inicio</td><td colSpan={months.length + 1}>{formatMonthYear(startDate)}</td></tr>
              <tr><td>Fin</td>   <td colSpan={months.length + 1}>{formatMonthYear(endDate)}</td></tr>
            </tbody>
          </table>

          <table className="report-table">
            <thead>
              <tr>
                <th>Número de vueltas o tiempo máximo permitido</th>
                {months.map(m => <th key={m}>{m}</th>)}
                <th>TOTAL</th>
              </tr>
            </thead>
            <tbody>
              {cats.map(cat => (
                <tr key={cat}>
                  <td>{cat}</td>
                  {months.map(m => <td key={m}>{dataMap[cat][m] || 0}</td>)}
                  <td>{rowTotals[cat]}</td>
                </tr>
              ))}
              <tr className="total-row">
                <td><strong>TOTAL</strong></td>
                {months.map(m => <td key={m}><strong>{colTotals[m]}</strong></td>)}
                <td><strong>{grandTotal}</strong></td>
              </tr>
            </tbody>
          </table>
        </>
      )}
    </div>
  );
}
