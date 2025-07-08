// src/components/LapsTimeReport.jsx
import React, { useState } from 'react';
import ReportService from '../services/report.service';
import './Reports.css';

export default function LapsTimeReport() {
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate]     = useState('');
  const [reports, setReports]     = useState([]);

  // Estados extra para heurísticas
  const [isLoading, setIsLoading]     = useState(false);
  const [formError, setFormError]     = useState('');
  const [hasSearched, setHasSearched] = useState(false);

  const handleGenerate = async e => {
    e.preventDefault();
    setHasSearched(true);

    // Validaciones inline (Prevención de errores)
    if (!startDate || !endDate) {
      setFormError('Ambas fechas son obligatorias.');
      return;
    }
    if (startDate > endDate) {
      setFormError('La fecha “Desde” debe ser anterior a “Hasta”.');
      return;
    }

    setFormError('');
    setIsLoading(true);
    try {
      const start = `${startDate}T00:00:00`;
      const end   = `${endDate}T23:59:59`;
      const res   = await ReportService.generateLapsTimeReport(start, end);
      setReports(res.data);
    } catch (err) {
      console.error(err);
      setFormError('Error al generar el reporte. Intente nuevamente.');
      setReports([]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleReset = () => {
    setStartDate('');
    setEndDate('');
    setReports([]);
    setFormError('');
    setHasSearched(false);
  };

  const formatMonthYear = iso => {
    const d = new Date(iso);
    return d.toLocaleDateString('es-CL', {
      month: 'long',
      year: 'numeric'
    });
  };

  const months = [...new Set(reports.map(r => r.monthName))];
  const cats   = [...new Set(reports.map(r => r.aggregationKey))]
                   .sort((a, b) => Number(a) - Number(b));

  const dataMap = {};
  reports.forEach(r => {
    if (!dataMap[r.aggregationKey]) dataMap[r.aggregationKey] = {};
    dataMap[r.aggregationKey][r.monthName] = r.totalIncome;
  });

  const rowTotals = {};
  cats.forEach(c =>
    rowTotals[c] = months.reduce(
      (sum, m) => sum + (dataMap[c][m] || 0),
      0
    )
  );

  const colTotals = {};
  months.forEach(m =>
    colTotals[m] = cats.reduce(
      (sum, c) => sum + (dataMap[c][m] || 0),
      0
    )
  );

  const grandTotal = cats.reduce((sum, c) => sum + rowTotals[c], 0);

  return (
    <div className="reports-container">
      <h2>Reporte de ingresos por número de personas</h2>

      <form onSubmit={handleGenerate} className="report-form">
        <label htmlFor="startDate">Desde:</label>
        <input
          id="startDate"
          type="date"
          value={startDate}
          onChange={e => setStartDate(e.target.value)}
          disabled={isLoading}
          required
        />

        <label htmlFor="endDate">Hasta:</label>
        <input
          id="endDate"
          type="date"
          value={endDate}
          onChange={e => setEndDate(e.target.value)}
          disabled={isLoading}
          required
        />

        <button type="submit" disabled={isLoading}>
          {isLoading ? 'Cargando…' : 'Generar'}
        </button>

        <button
          type="button"
          onClick={handleReset}
          disabled={isLoading}
        >
          Limpiar
        </button>

        {formError && (
          <div className="form-error">{formError}</div>
        )}
      </form>

      {hasSearched && !isLoading && !formError && reports.length === 0 && (
        <div className="alert alert-info">
          No hay reservas en el periodo seleccionado
        </div>
      )}

      {reports.length > 0 && (
        <>
          <table className="report-dates">
            <tbody>
              <tr>
                <td>Inicio</td>
                <td colSpan={months.length + 1}>
                  {formatMonthYear(startDate)}
                </td>
              </tr>
              <tr>
                <td>Fin</td>
                <td colSpan={months.length + 1}>
                  {formatMonthYear(endDate)}
                </td>
              </tr>
            </tbody>
          </table>

          <table className="report-table">
            <thead>
              <tr>
                <th>Número de vueltas o tiempo máximo permitido</th>
                {months.map(m => (
                  <th key={m}>{m}</th>
                ))}
                <th>TOTAL</th>
              </tr>
            </thead>
            <tbody>
              {cats.map(cat => (
                <tr key={cat}>
                  <td>{cat}</td>
                  {months.map(m => (
                    <td key={m}>{dataMap[cat][m] || 0}</td>
                  ))}
                  <td>{rowTotals[cat]}</td>
                </tr>
              ))}
              <tr className="total-row">
                <td><strong>TOTAL</strong></td>
                {months.map(m => (
                  <td key={m}><strong>{colTotals[m]}</strong></td>
                ))}
                <td><strong>{grandTotal}</strong></td>
              </tr>
            </tbody>
          </table>
        </>
      )}
    </div>
  );
}
