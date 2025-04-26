// src/services/voucher.service.js
import axios from 'axios';

/** Base para vouchers: POST /api/vouchers/... */
const API_URL = '/api/vouchers';

class VoucherService {
  /**
   * Crea un voucher a partir de la reserva
   * @param {{ reservation: { id: number } }} payload
   */
  createVoucher(payload) {
    return axios.post(`${API_URL}/create-voucher`, payload);
  }

  /** Alias “create” por compatibilidad */
  create(payload) {
    return this.createVoucher(payload);
  }

  /** Recupera todos los vouchers generados */
  getAll() {
    return axios.get(`${API_URL}/retrieve-voucher`);
  }

  /** Recupera un voucher por id */
  get(id) {
    return axios.get(`${API_URL}/${id}`);
  }

  /**
   * Envía el voucher por correo a un destinatario
   * @param {number} voucherId 
   * @param {string} recipientEmail 
   */
  sendEmail(voucherId, recipientEmail) {
    return axios.post(
      `${API_URL}/send-email`,
      null,
      { params: { voucherId, recipientEmail } }
    );
  }
}

export default new VoucherService();
