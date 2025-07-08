import React, { useState, useEffect } from 'react';
import { Calendar, Views, dateFnsLocalizer } from 'react-big-calendar';
import {
  format,
  parse,
  startOfWeek,
  getDay,
  addDays
} from 'date-fns';
import es from 'date-fns/locale/es';
import ReservationService from '../services/reservation.service';
import 'react-big-calendar/lib/css/react-big-calendar.css';
import './WeeklyRack.css';

const locales = { es };

const localizer = dateFnsLocalizer({
  format,
  parse,
  startOfWeek: () => startOfWeek(new Date(), { weekStartsOn: 1 }),
  getDay,
  locales,
  formats: {
    // Cabecera de columna: día y abreviatura
    dayHeaderFormat: 'EEE dd/MM',
    // Rango que aparece en el toolbar
    dayRangeHeaderFormat: ({ start, end }, culture, local) =>
      `${local.format(start, 'd MMMM', culture)} – ${local.format(end, 'd MMMM yyyy', culture)}`,
    // Para el calculito interno de la celda de día
    dayFormat: 'dd',
    // Mes y año en cabecera de vista de mes (aunque aquí usamos solo semana)
    monthHeaderFormat: 'MMMM yyyy',
    // Día completo cuando se necesite
    weekdayFormat: 'EEEE'
  }
});

export default function WeeklyRack() {
  const [events, setEvents]     = useState([]);
  const [date, setDate]         = useState(new Date());
  const [isLoading, setLoading] = useState(false);
  const [error, setError]       = useState(null);

  const loadEvents = async (start, end) => {
    setLoading(true);
    setError(null);
    try {
      const res = await ReservationService.getBetween(
        start.toISOString(),
        end.toISOString()
      );
      const evs = res.data.map(r => ({
        title:  r.client.name,
        start:  new Date(r.reservationDateTime),
        end:    new Date(
                  new Date(r.reservationDateTime).getTime() +
                  r.maxLapsOrTime * 60000
                ),
        allDay: false
      }));
      setEvents(evs);
    } catch (err) {
      console.error(err);
      setError('No se pudieron cargar las reservas.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const start = startOfWeek(date, { weekStartsOn: 1 });
    const end   = addDays(start, 6);
    loadEvents(start, end);
  }, [date]);

  const handleNavigate = newDate => setDate(newDate);
  const goToCurrentWeek = () => setDate(new Date());
  const refresh = () => {
    const start = startOfWeek(date, { weekStartsOn: 1 });
    const end   = addDays(start, 6);
    loadEvents(start, end);
  };

  // Toolbar personalizado con nuestro label en español
  const CustomToolbar = ({ onNavigate, label }) => (
    <div className="rbc-toolbar-custom">
      <div className="btn-group">
        <button
          className="btn btn-outline-primary btn-sm"
          onClick={() => onNavigate('PREV')}
        >
          Anterior
        </button>
        <button
          className="btn btn-outline-primary btn-sm"
          onClick={() => { onNavigate('TODAY'); goToCurrentWeek(); }}
        >
          Hoy
        </button>
        <button
          className="btn btn-outline-primary btn-sm"
          onClick={() => onNavigate('NEXT')}
        >
          Siguiente
        </button>
      </div>
      <span className="rbc-toolbar-label">{label}</span>
      <div className="btn-group">
        <button
          className="btn btn-outline-secondary btn-sm"
          onClick={refresh}
        >
          Refrescar
        </button>
        <button
          className="btn btn-secondary btn-sm"
          onClick={goToCurrentWeek}
        >
          Cancelar
        </button>
      </div>
    </div>
  );

  return (
    <div className="weekly-rack container mt-4">
      {error && (
        <div className="alert alert-danger d-flex justify-content-between align-items-center">
          <span>{error}</span>
          <button className="btn btn-link" onClick={refresh}>
            Reintentar
          </button>
        </div>
      )}

      {isLoading ? (
        <div className="d-flex justify-content-center my-4">
          <div className="spinner-border" role="status" />
        </div>
      ) : (
        <Calendar
          localizer={localizer}
          events={events}
          defaultView={Views.WEEK}
          views={[Views.WEEK]}
          step={60}
          showMultiDayTimes
          date={date}
          onNavigate={handleNavigate}
          components={{ toolbar: CustomToolbar }}
          style={{ height: '75vh' }}
        />
      )}
    </div>
  );
}
