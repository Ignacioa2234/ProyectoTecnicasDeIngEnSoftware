import http from '../http-common';

class ReservationService {
  getAll() {
    return http.get('/reservations');
  }
  get(id) {
    return http.get(`/reservations/${id}`);
  }
  create(data) {
    return http.post('/reservations', data);
  }
  update(id, data) {
    return http.put(`/reservations/${id}`, data);
  }
  delete(id) {
    return http.delete(`/reservations/${id}`);
  }
  getBetween(start, end) {
    return http.get('/reservations', {
      params: { start, end }
    });
  }
}

export default new ReservationService();
