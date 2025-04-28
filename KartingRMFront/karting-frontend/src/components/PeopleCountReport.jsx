// src/components/PeopleCountReport.jsx
import React, { useState, useEffect } from 'react';
import { eachMonthOfInterval, format, parseISO, startOfMonth, endOfMonth } from 'date-fns';
import reportService from '../services/report.service';

export default function PeopleCountReport() {
  const [months, setMonths] = useState([]);
  const [data, setData]     = useState({}); // { aggregationKey: { YYYY-MM: totalIncome, TOTAL: total } }

  const [startMonth, setStartMonth] = useState(() => format(new Date(), 'yyyy-MM'));
  const [endMonth, setEndMonth]     = useState(() => format(new Date(), 'yyyy-MM'));

  useEffect(() => {
    const start = startOfMonth(parseISO(startMonth + '-01'));
    const end   = endOfMonth(parseISO(endMonth + '-01'));
    const interval = eachMonthOfInterval({ start, end });
    setMonths(interval.map(d => format(d, 'yyyy-MM')));

    reportService.getGroupSizeReport(
      start.toISOString(),
      end.toISOString()
    ).then(list => {
      const table = {};
      list.forEach(r => {
        const key = r.aggregationKey;
        const m   = format(parseISO(r.monthName + '-01'), 'yyyy-MM');
        if (!table[key]) table[key] = {};
        table[key][m] = r.totalIncome;
      });
      Object.keys(table).forEach(key => {
        table[key].TOTAL = months.reduce(
          (sum, m) => sum + (table[key][m] || 0),
          0
        );
      });
      setData(table);
    });
  }, [startMonth, endMonth]);

  return (
    <div>
      <h2>Reporte por Número de Personas</h2>

      <div className="row mb-3">
        <div className="col">
          <label>Mes Inicio</label>
          <input
            type="month"
            className="form-control"
            value={startMonth}
            onChange={e => setStartMonth(e.target.value)}
          />
        </div>
        <div className="col">
          <label>Mes Fin</label>
          <input
            type="month"
            className="form-control"
            value={endMonth}
            onChange={e => setEndMonth(e.target.value)}
          />
        </div>
      </div>

      <table className="table table-bordered">
        <thead>
          <tr>
            <th>Número de personas</th>
            {months.map(m => (
              <th key={m}>{format(parseISO(m + '-01'), 'LLLL yyyy')}</th>
            ))}
            <th>TOTAL</th>
          </tr>
        </thead>
        <tbody>
          {Object.entries(data).map(([key, row]) => (
            <tr key={key}>
              <td>{key}</td>
              {months.map(m => (
                <td key={m}>
                  {new Intl.NumberFormat('es-CL').format(row[m] || 0)}
                </td>
              ))}
              <td>
                {new Intl.NumberFormat('es-CL').format(row.TOTAL || 0)}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
