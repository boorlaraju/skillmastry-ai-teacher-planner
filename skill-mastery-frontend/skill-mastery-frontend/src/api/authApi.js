import { http } from './http';
import { LOGIN_PATH, REGISTER_PATH } from './config';

// If your backend uses different DTO field names, update only these payload mappings.
export async function loginRequest({ email, password }) {
  const { data } = await http.post(LOGIN_PATH, { email, password });
  return data;
}
export async function registerRequest({ fullName, email, password }) {
  const { data } = await http.post(REGISTER_PATH, { name: fullName, fullName, email, password, role: 'TEACHER' });
  return data;
}
export function extractToken(data) {
  return data?.token || data?.accessToken || data?.jwt || data?.data?.token || data?.data?.accessToken || null;
}
