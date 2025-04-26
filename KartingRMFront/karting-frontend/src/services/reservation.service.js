import http from '../http-common';
class ReservationService {
  getAll()      { return http.get('/reservations'); }
  get(id)      { return http.get(`/reservations/${id}`); }
  create(d)    { return http.post('/reservations', d); }
  update(i, d) { return http.put(`/reservations/${i}`, d); }
  delete(i)    { return http.delete(`/reservations/${i}`); }
}
export default new ReservationService();
