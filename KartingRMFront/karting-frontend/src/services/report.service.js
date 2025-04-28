// src/services/report.service.js
import http from '../http-common';

class ReportService {
  getLapsTimeReport(start, end) {
    return http.get('/reports/laps-time', { params: { start, end } });
  }
  getPeopleCountReport(start, end) {
    return http.get('/reports/group-size', { params: { start, end } });
  }
}

export default new ReportService();
