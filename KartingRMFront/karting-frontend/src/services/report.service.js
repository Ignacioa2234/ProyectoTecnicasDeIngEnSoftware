import http from '../http-common';

class ReportService {
  getLapsTime(start, end) {
    return http.get('/reports/laps-time', {
      params: { start, end }
    });
  }

  getGroupSize(start, end) {
    return http.get('/reports/group-size', {
      params: { start, end }
    });
  }
}

export default new ReportService();
