import { useState } from 'react';
import { Link as RouterLink, useLocation, useNavigate } from 'react-router-dom';
import { Alert, Box, Button, Container, Link, Paper, Stack, TextField, Typography } from '@mui/material';
import AutoAwesomeRoundedIcon from '@mui/icons-material/AutoAwesomeRounded';
import { useAuth } from '../auth/AuthContext';
import { errorMessage } from '../api/http';
import Brand from '../components/Brand';
export default function Login() {
 const [email, setEmail] = useState('');
 const [password, setPassword] = useState(''); 
 const [error, setError] = useState(''); const { login, loading } = useAuth(); 
 const navigate = useNavigate(); const location = useLocation();
 const submit = async e => { e.preventDefault(); 
    setError('');
     try { 
        await login({ email, password });
         navigate(location.state?.from?.pathname || '/', { replace: true }); 
        } 
         catch (err) { 
            setError(errorMessage(err)); 
        } };
 return <Box className="auth-bg" minHeight="100vh" display="grid" alignItems="center" py={4}><Container maxWidth="sm"><Paper sx={{ p: { xs: 3, sm: 5 }, borderRadius: 4 }}><Brand/><Box mt={4} mb={3}><Typography variant="h4">Welcome back</Typography><Typography color="text.secondary" mt={1}>Sign in to continue building better lessons.</Typography></Box>{error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}<Stack component="form" spacing={2.2} onSubmit={submit}><TextField label="Email address" type="email" value={email} onChange={e => setEmail(e.target.value)} required autoComplete="email"/><TextField label="Password" type="password" value={password} onChange={e => setPassword(e.target.value)} required autoComplete="current-password"/><Button type="submit" variant="contained" size="large" disabled={loading}>{loading ? 'Signing in…' : 'Sign in'}</Button></Stack><Typography textAlign="center" color="text.secondary" mt={3}>New to Skill Mastery? <Link component={RouterLink} to="/register" underline="hover" fontWeight={700}>Create an account</Link></Typography></Paper><Typography textAlign="center" color="text.secondary" variant="caption" mt={3}>© {new Date().getFullYear()} Skill Mastery Private Limited</Typography></Container></Box>;
}
