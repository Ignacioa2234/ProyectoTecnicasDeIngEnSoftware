import http from '../http-common';
class ReportService {
  generate(s,e,t){ return http.post('/reports/generate',{ start:s,end:e,reportType:t }); }
  schedule(s,e)  { return http.get('/reports/schedule',{ params:{ start:s,end:e }}); }
  getAll(t)      { return http.get(t?`/reports?type=${t}`:'/reports'); }
}
export default new ReportService();
