// src/components/PeopleCountReport.jsx
import React, { useState } from 'react';
import ReportService from '../services/report.service';
import './Reports.css';

export default function PeopleCountReport() {
  const [start, setStart] = useState('');
  const [end, setEnd] = useState('');
  const [data, setData] = useState([]);

  const handleSubmit = e => {
    e.preventDefault();
    ReportService.getPeopleCountReport(start, end)
      .then(res => setData(res.data))
      .catch(console.error);
  };

  const { months, rows, grandTotal } = (() => {
    const sizes = ['1-2', '3-5', '6-10', '11-15'];
    const ms = [...new Set(data.map(d => d.month))].sort();
    const rs = sizes.map(sz => {
      const vals = ms.map(m => {
        const r = data.find(d => d.size === sz && d.month === m);
        return r ? r.amount : 0;
      });
      return { sz, vals, total: vals.reduce((a,b)=>a+b,0) };
    });
    const gt = rs.reduce((s,r)=>s+r.total,0);
    return { months: ms, rows: rs, grandTotal: gt };
  })();

  return (
    <div className="reports-container">
      <h2>Reporte Ingresos por # Personas</h2>
      <form onSubmit={handleSubmit} className="reports-form">
        <input type="date" value={start} onChange={e=>setStart(e.target.value)} required/>
        <input type="date" value={end}   onChange={e=>setEnd(e.target.value)}   required/>
        <button type="submit">Generar</button>
      </form>
      {data.length > 0 && (
        <table className="reports-table">
          <thead>
            <tr>
              <th># Personas</th>
              {months.map(m => <th key={m}>{m}</th>)}
              <th>TOTAL</th>
            </tr>
          </thead>
          <tbody>
            {rows.map(r => (
              <tr key={r.sz}>
                <td>{`${r.sz} personas`}</td>
                {r.vals.map((v,i) => <td key={i}>{v.toLocaleString()}</td>)}
                <td>{r.total.toLocaleString()}</td>
              </tr>
            ))}
            <tr>
              <td>TOTAL</td>
              {months.map((_,i) => {
                const col = rows.reduce((s,r)=>s + r.vals[i],0);
                return <td key={i}>{col.toLocaleString()}</td>;
              })}
              <td>{grandTotal.toLocaleString()}</td>
            </tr>
          </tbody>
        </table>
      )}
    </div>
  );
}
