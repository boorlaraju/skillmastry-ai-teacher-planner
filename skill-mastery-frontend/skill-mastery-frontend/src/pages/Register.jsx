import { useState } from 'react';
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import { Alert, Box, Button, Container, Link, Paper, Stack, TextField, Typography } from '@mui/material';
import { useAuth } from '../auth/AuthContext';
import { errorMessage } from '../api/http';
import Brand from '../components/Brand';
export default function Register() {
 const [form, setForm] = useState({ fullName: '', email: '', password: '', confirmPassword: '' }); const [error, setError] = useState(''); const { register, loading } = useAuth(); const navigate = useNavigate();
 const change = key => e => setForm(v => ({ ...v, [key]: e.target.value }));
 const submit = async e => { e.preventDefault(); setError(''); if (form.password.length < 8) return setError('Use a password with at least 8 characters.'); if (form.password !== form.confirmPassword) return setError('Passwords do not match.'); try { await register(form); navigate('/'); } catch (err) { setError(errorMessage(err)); } };
 return <Box className="auth-bg" minHeight="100vh" display="grid" alignItems="center" py={4}><Container maxWidth="sm"><Paper sx={{ p: { xs: 3, sm: 5 }, borderRadius: 4 }}><Brand/><Box mt={4} mb={3}><Typography variant="h4">Create your account</Typography><Typography color="text.secondary" mt={1}>Set up your teacher workspace.</Typography></Box>{error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}<Stack component="form" spacing={2} onSubmit={submit}><TextField label="Full name" value={form.fullName} onChange={change('fullName')} required autoComplete="name"/><TextField label="Email address" type="email" value={form.email} onChange={change('email')} required autoComplete="email"/><TextField label="Password" type="password" value={form.password} onChange={change('password')} required autoComplete="new-password"/><TextField label="Confirm password" type="password" value={form.confirmPassword} onChange={change('confirmPassword')} required autoComplete="new-password"/><Button type="submit" variant="contained" size="large" disabled={loading}>{loading ? 'Creating account…' : 'Create account'}</Button></Stack><Typography textAlign="center" color="text.secondary" mt={3}>Already registered? <Link component={RouterLink} to="/login" underline="hover" fontWeight={700}>Sign in</Link></Typography></Paper></Container></Box>;
}
