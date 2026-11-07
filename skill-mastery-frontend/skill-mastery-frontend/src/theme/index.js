import { createTheme } from '@mui/material/styles';
export const theme = createTheme({
  palette: { mode: 'light', primary: { main: '#3157d5', dark: '#2342ae' }, secondary: { main: '#0f9f83' }, background: { default: '#f5f7fb', paper: '#ffffff' }, text: { primary: '#172033', secondary: '#68738a' }, success: { main: '#16866f' }, warning: { main: '#d28a25' } },
  typography: { fontFamily: 'Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif', h4: { fontWeight: 800, letterSpacing: '-.04em' }, h5: { fontWeight: 750, letterSpacing: '-.025em' }, h6: { fontWeight: 700 }, button: { textTransform: 'none', fontWeight: 700, borderRadius: 10 } },
  shape: { borderRadius: 14 }, components: { MuiButton: { styleOverrides: { root: { borderRadius: 10, padding: '9px 16px' } } }, MuiPaper: { styleOverrides: { rounded: { borderRadius: 16 } } }, MuiCard: { styleOverrides: { root: { border: '1px solid #e9edf5', boxShadow: '0 5px 22px rgba(28,45,90,.045)' } } }, MuiTextField: { defaultProps: { size: 'medium', fullWidth: true } } }
});
