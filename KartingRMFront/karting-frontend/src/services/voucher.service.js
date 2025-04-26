import http from '../http-common';
class VoucherService {
  createVoucher(p) { return http.post('/vouchers/create-voucher', p); }
  create(p)        { return this.createVoucher(p); }
  getAll()         { return http.get('/vouchers/retrieve-voucher'); }
  get(id)          { return http.get(`/vouchers/${id}`); }
  sendEmail(id,e)  { 
    return http.post('/vouchers/send-email', null, { params:{ voucherId:id, recipientEmail:e }});
  }
}
export default new VoucherService();
