import { http } from './http';

export const lessonApi = {
  async generate(payload) { const { data } = await http.post('/lessons/generate', payload); return data; },
  async create(payload) { const { data } = await http.post('/lessons', payload); return data; },
  async list() { const { data } = await http.get('/lessons'); return Array.isArray(data) ? data : (data?.content || data?.lessons || data?.data || []); },
  async get(id) { const { data } = await http.get(`/lessons/${id}`); return data; },
  async update(id, payload) { const { data } = await http.put(`/lessons/${id}`, payload); return data; },
  async remove(id) { await http.delete(`/lessons/${id}`); }
};
