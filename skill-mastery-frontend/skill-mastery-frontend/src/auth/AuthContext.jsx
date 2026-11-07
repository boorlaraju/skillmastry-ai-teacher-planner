import { createContext, useContext, useMemo, useState } from 'react';
import { loginRequest, registerRequest, extractToken } from '../api/authApi';
import { TOKEN_KEY } from '../api/config';

const AuthContext = createContext(null);
export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem(TOKEN_KEY));
  const [user, setUser] = useState(() => { try { return JSON.parse(localStorage.getItem('sm_user') || 'null'); } catch { return null; } });
  const [loading, setLoading] = useState(false);
  const acceptAuth = (data, fallbackEmail) => {
    const nextToken = extractToken(data);
    if (!nextToken) throw new Error('Login succeeded but no JWT was found. Update extractToken() in src/api/authApi.js to match your backend response.');
    const nextUser = data?.user || data?.teacher || data?.data?.user || { email: fallbackEmail, name: data?.name || fallbackEmail?.split('@')[0] || 'Teacher' };
    localStorage.setItem(TOKEN_KEY, nextToken); localStorage.setItem('sm_user', JSON.stringify(nextUser));
    setToken(nextToken); setUser(nextUser);
  };
  const login = async (credentials) => { setLoading(true); try { const data = await loginRequest(credentials); acceptAuth(data, credentials.email); } finally { setLoading(false); } };
  const register = async (details) => { setLoading(true); try { const data = await registerRequest(details); if (extractToken(data)) acceptAuth(data, details.email); else await login({ email: details.email, password: details.password }); } finally { setLoading(false); } };
  const logout = () => { localStorage.removeItem(TOKEN_KEY); localStorage.removeItem('sm_user'); setToken(null); setUser(null); };
  const value = useMemo(() => ({ token, user, loading, isAuthenticated: Boolean(token), login, register, logout }), [token, user, loading]);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
export function useAuth() { const value = useContext(AuthContext); if (!value) throw new Error('useAuth must be used inside AuthProvider'); return value; }
