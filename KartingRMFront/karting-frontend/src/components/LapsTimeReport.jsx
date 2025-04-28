import React, { useState } from 'react';
import ReportService from '../services/report.service';
import './Reports.css';

export default function LapsTimeReport() {
  const [month, setMonth] = useState('');
  const [report, setReport] = useState([]);

  const generar = () => {
    if (!month) return;
    // month viene en formato "YYYY-MM"
    const [y, m] = month.split('-').map(Number);
    // start = primer día del mes
    const start = `${y}-${String(m).padStart(2,'0')}-01`;
    // end = último día del tercer mes (mes +2)
    const endMonthIndex = m - 1 + 2;
    const endYear = y + Math.floor(endMonthIndex / 12);
    const endMonth = (endMonthIndex % 12) + 1;
    const lastDay = new Date(endYear, endMonth, 0).getDate();
    const end = `${endYear}-${String(endMonth).padStart(2,'0')}-${String(lastDay).padStart(2,'0')}`;

    ReportService.getLapsTimeReport(start, end)
      .then(r => setReport(r.data))
      .catch(err => console.error(err));
  };

  return (
    <div>
      <h2>Reporte Ingresos por Vueltas/Tiempo (3 meses)</h2>
      <div className="report-filters">
        <input
          type="month"
          value={month}
          onChange={e => setMonth(e.target.value)}
        />
        <button onClick={generar}>Generar</button>
      </div>

      {report.length > 0 && (
        <table className="report-table">
          <thead>
            <tr>
              <th>Tarifa</th>
              {report.map((r, i) =>
                <th key={i}>{r.month}</th>
              )}
              <th>Total</th>
            </tr>
          </thead>
          <tbody>
            {/*
              Asumo que tu ReportEntity viene con:
                - category (10,15,20 vueltas)
                - month (p.ej "Abril")
                - amount
              y que el back ya devuelve 3 filas distintas, una por tarifa.
            */}
            {report.reduce((acc, curr) => {
              let row = acc.find(r => r.category === curr.category);
              if (!row) {
                row = { category: curr.category, values: [], total: 0 };
                acc.push(row);
              }
              row.values.push(curr.amount);
              row.total += curr.amount;
              return acc;
            }, []).map((row, idx) => (
              <tr key={idx}>
                <td>{row.category}</td>
                {row.values.map((v, j) =>
                  <td key={j}>{v.toLocaleString()}</td>
                )}
                <td>{row.total.toLocaleString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
