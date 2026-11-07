import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Alert, Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, Grid, TextField, Typography } from '@mui/material';
import ArrowBackRoundedIcon from '@mui/icons-material/ArrowBackRounded';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import { lessonApi } from '../api/lessonApi';
import { errorMessage } from '../api/http';
import LessonViewer from './LessonViewer';
const editableFields = ['topic', 'subject', 'classNumber', 'duration', 'language', 'introduction', 'explanation', 'practicalActivity', 'homework'];
export default function LessonDetails(){
 const {id}=useParams(); const [lesson,setLesson]=useState(null); const [error,setError]=useState(''); const [loading,setLoading]=useState(true); const [editOpen,setEditOpen]=useState(false); const [saving,setSaving]=useState(false); const [draft,setDraft]=useState({}); const navigate=useNavigate();
 const load=()=>lessonApi.get(id).then(setLesson).catch(e=>setError(errorMessage(e))).finally(()=>setLoading(false)); useEffect(()=>{load();},[id]);
 const openEdit=()=>{setDraft(Object.fromEntries(editableFields.map(k=>[k,lesson?.[k] ?? (k==='classNumber'?10:k==='duration'?45:'')])));setEditOpen(true);};
 const save=async()=>{setSaving(true);setError('');try{const payload={...lesson,...draft,classNumber:Number(draft.classNumber),duration:Number(draft.duration)};const updated=await lessonApi.update(id,payload);setLesson(updated || payload);setEditOpen(false);}catch(e){setError(errorMessage(e));}finally{setSaving(false);}};
 const field=(key)=> <Grid item xs={12} sm={['introduction','explanation','practicalActivity','homework'].includes(key)?12:6} key={key}><TextField label={key.replace(/[A-Z]/g,m=>` ${m}`).replace(/^./,m=>m.toUpperCase())} value={draft[key] ?? ''} onChange={e=>setDraft(v=>({...v,[key]:e.target.value}))} multiline={['introduction','explanation','practicalActivity','homework'].includes(key)} minRows={['introduction','explanation','practicalActivity','homework'].includes(key)?3:1} type={['classNumber','duration'].includes(key)?'number':'text'}/></Grid>;
 return <><Button startIcon={<ArrowBackRoundedIcon/>} onClick={()=>navigate('/lessons')} sx={{mb:2}}>Back to lessons</Button>{loading&&<Typography>Loading lesson…</Typography>}{error&&<Alert severity="error" sx={{mb:2}}>{error}</Alert>}{lesson&&<><Box display="flex" justifyContent="space-between" alignItems="center" gap={2} mb={2}><Box><Typography variant="h4">Lesson details</Typography><Typography color="text.secondary">Saved lesson plan</Typography></Box><Button variant="outlined" startIcon={<EditOutlinedIcon/>} onClick={openEdit}>Edit lesson</Button></Box><LessonViewer lesson={lesson}/><Dialog open={editOpen} onClose={()=>setEditOpen(false)} fullWidth maxWidth="md"><DialogTitle>Edit lesson plan</DialogTitle><DialogContent><Typography color="text.secondary" variant="body2" mb={2}>Update the lesson fields below. Existing detailed AI content is preserved.</Typography><Grid container spacing={2}>{editableFields.map(field)}</Grid></DialogContent><DialogActions sx={{p:2}}><Button onClick={()=>setEditOpen(false)}>Cancel</Button><Button variant="contained" onClick={save} disabled={saving}>{saving?'Saving…':'Save changes'}</Button></DialogActions></Dialog></>}</>;
}
