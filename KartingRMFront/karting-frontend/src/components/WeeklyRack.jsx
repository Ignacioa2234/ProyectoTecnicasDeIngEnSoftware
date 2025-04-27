// src/components/WeeklyRack.jsx
import React, { useState, useEffect } from 'react';
import { Calendar, Views, dateFnsLocalizer } from 'react-big-calendar';
import { format, parse, startOfWeek, getDay } from 'date-fns';
import es from 'date-fns/locale/es';
import ReservationService from '../services/reservation.service';
import 'react-big-calendar/lib/css/react-big-calendar.css';
import './WeeklyRack.css';

const locales = { 'es': es };
const localizer = dateFnsLocalizer({
  format, parse,
  startOfWeek: () => startOfWeek(new Date(), { weekStartsOn: 1 }),
  getDay,
  locales
});

export default function WeeklyRack() {
  const [events, setEvents] = useState([]);
  const [date, setDate]     = useState(new Date());

  // Cuando cambias la semana, recarga desde el back
  const loadEvents = (start, end) => {
    ReservationService.getBetween(start.toISOString(), end.toISOString())
      .then(res => {
        const evs = res.data.map(r => ({
          title: r.client.name,         // o cualquier info que quieras mostrar
          start: new Date(r.reservationDateTime),
          end:   new Date(
            new Date(r.reservationDateTime)
              .getTime() + r.maxLapsOrTime * 60000
          ),                              // duración aproximada
          allDay: false
        }));
        setEvents(evs);
      })
      .catch(console.error);
  };

  useEffect(() => {
    const start = startOfWeek(date, { weekStartsOn: 1 });
    const end   = new Date(start.getTime() + 7 * 24 * 3600 * 1000);
    loadEvents(start, end);
  }, [date]);

  return (
    <div className="weekly-rack">
      <Calendar
        localizer={localizer}
        events={events}
        defaultView={Views.WEEK}
        views={[Views.WEEK]}
        step={60}
        showMultiDayTimes
        date={date}
        onNavigate={setDate}
        style={{ height: '80vh' }}
        messages={{
          today:    'Hoy',
          previous: '<',
          next:     '>'
        }}
      />
    </div>
  );
}
