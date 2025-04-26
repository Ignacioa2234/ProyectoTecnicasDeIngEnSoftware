import axios from 'axios';

console.log('API_URL =', import.meta.env.VITE_API_URL);

export default axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  headers: { 'Content-Type': 'application/json' }
});
