// src/services/report.service.js
import http from '../http-common';

class ReportService {
  generateReport(start, end, type) {
    return http.post('/reports/generate', { start, end, reportType: type });
  }
  generateLapsTimeReport(start, end) {
    return this.generateReport(start, end, 'laps');
  }
  generatePeopleCountReport(start, end) {
    return this.generateReport(start, end, 'group-size');
  }
}

export default new ReportService();
