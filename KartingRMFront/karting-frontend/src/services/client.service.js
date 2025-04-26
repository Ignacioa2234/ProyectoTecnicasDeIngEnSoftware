import http from '../http-common';
class ClientService {
  getAll()    { return http.get('/clients'); }
  get(id)    { return http.get(`/clients/${id}`); }
  create(d)  { return http.post('/clients', d); }
  update(i,d){ return http.put(`/clients/${i}`, d); }
  delete(i)  { return http.delete(`/clients/${i}`); }
}
export default new ClientService();
