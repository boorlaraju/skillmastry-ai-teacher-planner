import { Box, Typography, Button } from '@mui/material';
import AutoStoriesOutlinedIcon from '@mui/icons-material/AutoStoriesOutlined';
export default function EmptyState({ title = 'No lesson plans yet', description = 'Create your first AI-powered lesson plan to get started.', action, onAction }) {
 return <Box sx={{ py: 7, px: 2, textAlign: 'center', border: '1px dashed #d7deeb', borderRadius: 3, bgcolor: '#fbfcff' }}><AutoStoriesOutlinedIcon sx={{ fontSize: 42, color: '#9aa8c2', mb: 1.5 }}/><Typography variant="h6">{title}</Typography><Typography color="text.secondary" sx={{ mt: .5, mb: 2.5 }}>{description}</Typography>{action && <Button variant="contained" onClick={onAction}>{action}</Button>}</Box>;
}
