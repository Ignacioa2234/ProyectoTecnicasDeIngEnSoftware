import React, { useState, useEffect } from 'react';
import ReservationService from '../services/reservation.service';
import VoucherService     from '../services/voucher.service';
import ClientService      from '../services/client.service';

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
  const [user, setUser]             = useState(null);
  const [newClient, setNewClient]   = useState({ name: '', email: '', birthDate: '', password: '' });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isConfirming, setIsConfirming] = useState(false);

  useEffect(() => {
    const raw = localStorage.getItem('currentUser');
    if (raw) {
      const u = JSON.parse(raw);
      setUser(u);
      setForm(f => ({
        ...f,
        participants: [{ name: u.name, email: u.email, birthDate: u.birthDate?.slice(0,10) || '' }]
      }));
    }
  }, []);

  const handleChange = e => { const { name, value } = e.target; setForm(f => ({ ...f, [name]: value })); };
  const handleParticipantChange = (i, e) => {
    const { name, value } = e.target;
    setForm(f => {
      const parts = [...f.participants];
      parts[i] = { ...parts[i], [name]: value };
      return { ...f, participants: parts };
    });
  };
  const handleNewClientChange = e => { const { name, value } = e.target; setNewClient(c => ({ ...c, [name]: value })); };
  const addParticipant = () => setForm(f => ({ ...f, participants: [...f.participants, { name:'', email:'', birthDate:'' }] }));
  const removeParticipant = i => setForm(f => {
    const parts = f.participants.filter((_, idx) => idx !== i);
    return { ...f, participants: parts.length ? parts : [{ name:'', email:'', birthDate:'' }] };
  });

  const hourOptions = Array.from({ length: 15 }, (_, i) => {
    const h = 8 + i;
    return `${h.toString().padStart(2,'0')}:00`;
  });

  const handleSubmit = async e => {
    e.preventDefault();
    setIsSubmitting(true);
    let clientToUse = user;
    if (!user) {
      const { name, email, birthDate, password } = newClient;
      if (!name || !email || !birthDate || !password) {
        setMessage({ type:'danger', text:'Completa todos los datos del cliente.' });
        setIsSubmitting(false);
        return;
      }
      try { const resClient = await ClientService.create({ name, email, birthDate, password }); clientToUse = resClient.data; }
      catch (err) { console.error(err); setMessage({ type:'danger', text: err.response?.data?.message || 'Error al crear cliente.' }); setIsSubmitting(false); return; }
    }

    const timestamp = Date.now();
    const reservationDateTime = `${form.reservationDate}T${form.reservationTime}`;
    const payload = {
      reservationCode: `RES${timestamp}`,
      reservationDateTime,
      maxLapsOrTime: Number(form.maxLapsOrTime),
      peopleCount:   Number(form.peopleCount),
      assignedKarts: [],
      client:        { id: clientToUse.id },
      groupEmails:   form.participants.map(p => p.email),
      participants:  form.participants.map(p => ({ name: p.name, email: p.email, birthDate: p.birthDate }))
    };

    try {
      const res = await ReservationService.create(payload);
      if (!res.data?.id) { setMessage({ type:'danger', text:'ID inválido en reserva.' }); }
      else { setCreatedRes(res.data); setMessage({ type:'info', text:'Reserva creada. Confirma voucher.' }); }
    } catch (err) {
      console.error(err);
      setMessage({ type:'danger', text: err.response?.data?.message || 'Error al crear reserva.' });
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleConfirmVoucher = async () => {
    setIsConfirming(true);
    if (!createdRes?.id) {
      setMessage({ type:'danger', text:'Crea reserva primero.' });
      setIsConfirming(false);
      return;
    }
    try {
      await VoucherService.create({ reservation: { id: createdRes.id } });
      setMessage({ type:'success', text:`Voucher generado.` });
      setCreatedRes(null);
      setForm(initialForm);
      if (!user) setNewClient({ name:'', email:'', birthDate:'', password:'' });
    } catch (err) {
      console.error(err);
      setMessage({ type:'danger', text: err.response?.data?.message || 'Error al generar voucher.' });
    } finally {
      setIsConfirming(false);
    }
  };

  const handleReset = () => {
    setForm(initialForm);
    setNewClient({ name:'', email:'', birthDate:'', password:'' });
    setMessage(null);
    setCreatedRes(null);
  };

  return (
    <div className='container mt-4'>

      {message && (
        <div className={`alert alert-${message.type}`} role='alert'>
          {message.text}
        </div>
      )}

      <form onSubmit={handleSubmit}>
        {!user && (
          <>
            <h5>Datos del nuevo Cliente!</h5>
            <div className='row g-3 mb-4'>
              <div className='col-md-3'>
                <label className='form-label'>Nombre</label>
                <input type='text' className='form-control' name='name' value={newClient.name} onChange={handleNewClientChange} required />
              </div>
              <div className='col-md-3'>
                <label className='form-label'>Email</label>
                <input type='email' className='form-control' name='email' value={newClient.email} onChange={handleNewClientChange} required />
              </div>
              <div className='col-md-3'>
                <label className='form-label'>Fecha de Nacimiento</label>
                <input type='date' className='form-control' name='birthDate' value={newClient.birthDate} onChange={handleNewClientChange} required />
              </div>
              <div className='col-md-3'>
                <label className='form-label'>Contraseña</label>
                <input type='password' className='form-control' name='password' value={newClient.password} onChange={handleNewClientChange} required />
              </div>
            </div>
            <hr />
          </>
        )}

        <h4 className='mb-3'>Crear Reserva</h4>
        <div className='row g-3 mb-4'>
          <div className='col-md-4'>
            <label className='form-label'>Fecha de Reserva</label>
            <input type='date' className='form-control' name='reservationDate' value={form.reservationDate} onChange={handleChange} required />
          </div>
          <div className='col-md-4'>
            <label className='form-label'>Hora de Reserva</label>
            <select className='form-select' name='reservationTime' value={form.reservationTime} onChange={handleChange} required>
              <option value=''>Seleccione hora</option>
              {hourOptions.map(t => <option key={t} value={t}>{t}</option>)}
            </select>
          </div>
          <div className='col-md-4'>
            <label className='form-label'>Tarifa</label>
            <select className='form-select' name='maxLapsOrTime' value={form.maxLapsOrTime} onChange={handleChange} required>
              <option value=''>Seleccione tarifa</option>
              <option value='10'>10 vueltas / máx 10 min</option>
              <option value='15'>15 vueltas / máx 15 min</option>
              <option value='20'>20 vueltas / máx 20 min</option>
            </select>
          </div>
          <div className='col-md-4'>
            <label className='form-label'># Personas</label>
            <input type='number' className='form-control' name='peopleCount' value={form.peopleCount} onChange={handleChange} required />
          </div>
        </div>

        <hr className='my-4' />
        <h5>Participantes</h5>
        {form.participants.map((p, i) => (
          <div className='row g-3 mb-3 align-items-end' key={i}>
            <div className='col-md-4'>
              <label className='form-label'>Nombre</label>
              <input type='text' className='form-control' name='name' value={p.name} onChange={e => handleParticipantChange(i, e)} required />
            </div>
            <div className='col-md-4'>
              <label className='form-label'>Email</label>
              <input type='email' className='form-control' name='email' value={p.email} onChange={e => handleParticipantChange(i, e)} required />
            </div>
            <div className='col-md-3'>
              <label className='form-label'>Fecha de Nacimiento</label>
              <input type='date' className='form-control' name='birthDate' value={p.birthDate} onChange={e => handleParticipantChange(i, e)} required />
            </div>
            <div className='col-md-1'>
              <button type='button' className='btn btn-outline-danger' onClick={() => removeParticipant(i)}>Eliminar</button>
            </div>
          </div>
        ))}
        <button type='button' className='btn btn-primary mb-3' onClick={addParticipant}>+ Añadir participante</button>

        <div className='mt-4'>
          <button type='submit' className='btn btn-primary' disabled={isSubmitting}>
            {isSubmitting && <span className='spinner-border spinner-border-sm me-2' role='status' aria-hidden='true'></span>}
            Reservar
          </button>
          <button type='button' className='btn btn-secondary ms-2' onClick={handleReset}>Cancelar reserva</button>
        </div>
      </form>

      {createdRes && (
        <div className='mt-3'>
          <button className='btn btn-success' onClick={handleConfirmVoucher} disabled={isConfirming}>
            {isConfirming && <span className='spinner-border spinner-border-sm me-2' role='status' aria-hidden='true'></span>}
            Confirmar Reserva y Generar Voucher
          </button>
        </div>
      )}
    </div>
  );
}
