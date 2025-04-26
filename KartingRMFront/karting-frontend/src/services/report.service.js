// src/services/report.service.js
import axios from 'axios';

const API_URL = '/api/reports';

class ReportService {
  // Genera un reporte con parámetros
  generate(start, end, type) {
    return axios.post(`${API_URL}/generate`, {
      start,
      end,
      reportType: type
    });
  }

  // Obtiene el calendario de reportes generados
  schedule(start, end) {
    return axios.get(`${API_URL}/schedule`, {
      params: { start, end }
    });
  }

  // Trae todos los reportes (o por tipo)
  getAll(type) {
    const url = type
      ? `${API_URL}?type=${type}`
      : API_URL;
    return axios.get(url);
  }
}

export default new ReportService();
