import axios from 'axios';
import { API_BASE_URL, TOKEN_KEY } from './config';

export const http = axios.create({ baseURL: API_BASE_URL, timeout: 100000, headers: { 'Content-Type': 'application/json' } });
http.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY);
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});
http.interceptors.response.use((response) => response, (error) => {
  if (error.response?.status === 401) {
    // AuthContext owns the user-facing logout flow; this only prevents stale requests.
    localStorage.removeItem(TOKEN_KEY);
  }
  return Promise.reject(error);
});
export function errorMessage(error) {
  return error?.response?.data?.message || error?.response?.data?.error || error?.message || 'Something went wrong. Please try again.';
}
