import { Box, Typography } from '@mui/material';
import AutoAwesomeRoundedIcon from '@mui/icons-material/AutoAwesomeRounded';
export default function Brand({ compact = false, light = false }) {
  return <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.2, minWidth: 0 }}>
    <Box sx={{ width: 40, height: 40, borderRadius: 2.5, bgcolor: light ? 'rgba(255,255,255,.16)' : 'primary.main', color: '#fff', display: 'grid', placeItems: 'center', flexShrink: 0 }}><AutoAwesomeRoundedIcon /></Box>
    {!compact && <Box sx={{ minWidth: 0 }}><Typography fontWeight={850} color={light ? '#fff' : 'text.primary'} lineHeight={1.15}>Skill Mastery</Typography><Typography variant="caption" color={light ? 'rgba(255,255,255,.65)' : 'text.secondary'}>AI Lesson Planner</Typography></Box>}
  </Box>;
}
