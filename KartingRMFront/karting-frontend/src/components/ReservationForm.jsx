// src/components/ReservationForm.jsx
import React, { useState, useEffect } from 'react';
import ReservationService from '../services/reservation.service';
import VoucherService     from '../services/voucher.service';

const initialForm = {
  reservationDate: '',
  reservationTime: '',
  maxLapsOrTime: '',
  peopleCount: '',
  participants: [
    { name: '', email: '', birthDate: '' }
  ]
};

export default function ReservationForm() {
  const [form, setForm]             = useState(initialForm);
  const [message, setMessage]       = useState(null);
  const [createdRes, setCreatedRes] = useState(null);

  // 1) Al montar: precargamos al cliente logueado en la primera fila
  useEffect(() => {
    const raw = localStorage.getItem('currentUser');
    if (!raw) return;
    const user = JSON.parse(raw);
    setForm(f => ({
      ...f,
      participants: [{
        name:      user.name,
        email:     user.email,
        birthDate: user.birthDate?.slice(0,10) || ''
      }]
    }));
  }, []);

  // Actualiza campos simples del formulario
  const handleChange = e => {
    const { name, value } = e.target;
    setForm(f => ({ ...f, [name]: value }));
  };

  // Actualiza un participante en particular
  const handleParticipantChange = (idx, e) => {
    const { name, value } = e.target;
    setForm(f => {
      const parts = [...f.participants];
      parts[idx] = { ...parts[idx], [name]: value };
      return { ...f, participants: parts };
    });
  };

  // Añade una fila de participante adicional
  const addParticipant = () => {
    setForm(f => ({
      ...f,
      participants: [...f.participants, { name:'', email:'', birthDate: '' }]
    }));
  };

  // Genera opciones de hora de 08:00 a 22:00
  const hourOptions = Array.from({ length: 15 }, (_, i) => {
    const h = 8 + i;
    return `${h.toString().padStart(2,'0')}:00`;
  });

  // Envía el formulario para crear la reserva
  const handleSubmit = e => {
    e.preventDefault();
    const timestamp = Date.now();
    const reservationDateTime = `${form.reservationDate}T${form.reservationTime}`;
    const user = JSON.parse(localStorage.getItem('currentUser'));

    const payload = {
      reservationCode: `RES${timestamp}`,
      reservationDateTime,
      maxLapsOrTime:  Number(form.maxLapsOrTime),
      peopleCount:    Number(form.peopleCount),
      assignedKarts:  [],                 // el backend asigna automáticamente
      client:         { id: user.id },    // cliente logueado
      groupEmails:    form.participants.map(p => p.email),
      participants:   form.participants.map(p => ({
        name:      p.name,
        email:     p.email,
        birthDate: p.birthDate
      }))
    };

    ReservationService.create(payload)
      .then(res => {
        setCreatedRes(res.data);
        setMessage({
          type: 'info',
          text: 'Reserva creada. Ahora confirma para generar el voucher.'
        });
      })
      .catch(err => {
        console.error(err);
        const data = err.response?.data;
        const txt  = data?.message
                   || (typeof data === 'object'
                       ? JSON.stringify(data)
                       : data)
                   || 'Error al crear la reserva.';
        setMessage({ type: 'danger', text: txt });
      });
  };

  // Genera el voucher tras confirmar
  const handleConfirmVoucher = () => {
    VoucherService.createVoucher({ reservation: { id: createdRes.id } })
      .then(res => {
        setMessage({
          type: 'success',
          text: `Voucher ${res.data.voucherCode} generado y enviado.`
        });
        setCreatedRes(null);
        setForm(initialForm);
      })
      .catch(err => {
        console.error(err);
        const data = err.response?.data;
        const txt  = data?.message
                   || (typeof data === 'object'
                       ? JSON.stringify(data)
                       : data)
                   || 'Error al generar el voucher.';
        setMessage({ type: 'danger', text: txt });
      });
  };

  return (
    <div className="container mt-4">
      <h2>Crear Reserva</h2>

      {message && (
        <div className={`alert alert-${message.type}`} role="alert">
          {message.text}
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div className="row g-3">
          {/* Fecha de Reserva */}
          <div className="col-md-4">
            <label className="form-label">Fecha de Reserva</label>
            <input
              type="date"
              className="form-control"
              name="reservationDate"
              value={form.reservationDate}
              onChange={handleChange}
              required
            />
          </div>
          {/* Hora de Reserva */}
          <div className="col-md-4">
            <label className="form-label">Hora de Reserva</label>
            <select
              className="form-select"
              name="reservationTime"
              value={form.reservationTime}
              onChange={handleChange}
              required
            >
              <option value="">Seleccione hora</option>
              {hourOptions.map(t => (
                <option key={t} value={t}>{t}</option>
              ))}
            </select>
          </div>
          {/* Tarifa */}
          <div className="col-md-4">
            <label className="form-label">Tarifa</label>
            <select
              className="form-select"
              name="maxLapsOrTime"
              value={form.maxLapsOrTime}
              onChange={handleChange}
              required
            >
              <option value="">Seleccione tarifa</option>
              <option value="10">10 vueltas / máx 10 min</option>
              <option value="15">15 vueltas / máx 15 min</option>
              <option value="20">20 vueltas / máx 20 min</option>
            </select>
          </div>
          {/* Número de Personas */}
          <div className="col-md-4">
            <label className="form-label"># Personas</label>
            <input
              type="number"
              className="form-control"
              name="peopleCount"
              value={form.peopleCount}
              onChange={handleChange}
              required
            />
          </div>
        </div>

        <hr className="my-4" />
        <h5>Participantes</h5>

        {form.participants.map((p, idx) => (
          <div className="row g-3 mb-3" key={idx}>
            <div className="col-md-4">
              <label className="form-label">Nombre</label>
              <input
                type="text"
                className="form-control"
                name="name"
                value={p.name}
                onChange={e => handleParticipantChange(idx, e)}
                required
              />
            </div>
            <div className="col-md-4">
              <label className="form-label">Email</label>
              <input
                type="email"
                className="form-control"
                name="email"
                value={p.email}
                onChange={e => handleParticipantChange(idx, e)}
                required
              />
            </div>
            <div className="col-md-4">
              <label className="form-label">Fecha de Nacimiento</label>
              <input
                type="date"
                className="form-control"
                name="birthDate"
                value={p.birthDate}
                onChange={e => handleParticipantChange(idx, e)}
                required
              />
            </div>
          </div>
        ))}

        <button
          type="button"
          className="btn btn-link"
          onClick={addParticipant}
        >
          + Añadir participante
        </button>

        <div className="mt-4">
          <button type="submit" className="btn btn-primary">
            Crear Reserva
          </button>
        </div>
      </form>

      {createdRes && (
        <div className="mt-3">
          <button
            className="btn btn-success"
            onClick={handleConfirmVoucher}
          >
            Confirmar Reserva y Generar Voucher
          </button>
        </div>
      )}
    </div>
  );
}
