// src/services/client.service.js
import axios from 'axios';

const API_URL = '/api/clients';

class ClientService {
  // Trae todos los clientes
  getAll() {
    return axios.get(API_URL);
  }

  // Trae un cliente por su ID
  get(id) {
    return axios.get(`${API_URL}/${id}`);
  }

  // Crea un nuevo cliente
  create(data) {
    return axios.post(API_URL, data);
  }

  // Actualiza un cliente existente
  update(id, data) {
    return axios.put(`${API_URL}/${id}`, data);
  }

  // Elimina un cliente por ID
  delete(id) {
    return axios.delete(`${API_URL}/${id}`);
  }
}

export default new ClientService();
