import http from '../http-common';

class ReportService {
  getLapsTimeReport(start, end) {
    return http.get(`/reports/laps-time?start=${start}&end=${end}`);
  }
  getPeopleCountReport(start, end) {
    return http.get(`/reports/group-size?start=${start}&end=${end}`);
  }
}

export default new ReportService();
