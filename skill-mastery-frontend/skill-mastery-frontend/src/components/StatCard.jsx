import { Card, CardContent, Box, Typography } from '@mui/material';
export default function StatCard({ label, value, caption, icon, tint = '#eef2ff' }) {
  return <Card>
          <CardContent sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: 2 }}>
          <Box>
            <Typography color="text.secondary" variant="body2" fontWeight={600}>{label}
            </Typography>
            <Typography variant="h4" sx={{ mt: 1 }}>{value}</Typography>
            <Typography variant="caption" color="text.secondary">{caption}</Typography>
            </Box>
            <Box sx={{ bgcolor: tint, color: 'primary.main', width: 44, height: 44, borderRadius: 3, display: 'grid', placeItems: 'center' }}>{icon}</Box>
          </CardContent>
        </Card>;
}
