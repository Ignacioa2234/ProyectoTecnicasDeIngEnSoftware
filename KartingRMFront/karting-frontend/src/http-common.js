// src/http-common.js
import axios from 'axios';

export default axios.create({
  baseURL: 'http://4.201.152.46:8010/api',
  headers: {
    'Content-type': 'application/json'
  }
});
