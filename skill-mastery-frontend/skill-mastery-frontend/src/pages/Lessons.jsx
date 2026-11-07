import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Alert, Box, Button, Grid, InputAdornment, Menu, MenuItem, TextField, Typography } from '@mui/material';
import SearchRoundedIcon from '@mui/icons-material/SearchRounded';
import AddRoundedIcon from '@mui/icons-material/AddRounded';
import { lessonApi } from '../api/lessonApi';
import { errorMessage } from '../api/http';
import LessonCard from '../components/LessonCard';
import EmptyState from '../components/EmptyState';
export default function Lessons() {
 const [lessons,setLessons]=useState([]); const [loading,setLoading]=useState(true); const [error,setError]=useState(''); const [query,setQuery]=useState(''); const [selected,setSelected]=useState(null); const [anchor,setAnchor]=useState(null); const [deleting,setDeleting]=useState(false); const navigate=useNavigate();
 const refresh=()=>lessonApi.list().then(setLessons).catch(e=>setError(errorMessage(e))).finally(()=>setLoading(false)); useEffect(()=>{refresh();},[]);
 const filtered=useMemo(()=>lessons.filter(l=>`${l.topic||''} ${l.subject||''} ${l.classNumber||''}`.toLowerCase().includes(query.toLowerCase())),[lessons,query]);
 const remove=async()=>{if(!selected?.id)return; if(!window.confirm(`Delete “${selected.topic || 'this lesson'}”? This cannot be undone.`))return; setDeleting(true); try{await lessonApi.remove(selected.id);setAnchor(null);setSelected(null);refresh();}catch(e){setError(errorMessage(e));}finally{setDeleting(false);}};
 return <><Box display="flex" justifyContent="space-between" alignItems={{xs:'flex-start',sm:'center'}} flexDirection={{xs:'column',sm:'row'}} gap={2} mb={3}><Box><Typography variant="h4">My lesson plans</Typography><Typography color="text.secondary" mt={.7}>Manage and revisit your saved teaching resources.</Typography></Box><Button variant="contained" startIcon={<AddRoundedIcon/>} onClick={()=>navigate('/generate')}>New lesson</Button></Box>{error&&<Alert severity="error" sx={{mb:2}}>{error}</Alert>}<TextField placeholder="Search by topic, subject or class…" value={query} onChange={e=>setQuery(e.target.value)} sx={{maxWidth:480,mb:3}} InputProps={{startAdornment:<InputAdornment position="start"><SearchRoundedIcon color="action"/></InputAdornment>}}/>{loading?<Typography color="text.secondary">Loading lesson plans…</Typography>:filtered.length?<Grid container spacing={2}>{filtered.map(l=><Grid item xs={12} sm={6} lg={4} key={l.id}><LessonCard lesson={l} onOpen={()=>navigate(`/lessons/${l.id}`)} onMenu={el=>{setSelected(l);setAnchor(el);}}/></Grid>)}</Grid>:<EmptyState title={query?'No matching lessons':'Your library is empty'} description={query?'Try another search term.':'Generate and save a lesson plan to see it here.'} action="Generate lesson" onAction={()=>navigate('/generate')}/>}<Menu anchorEl={anchor} open={Boolean(anchor)} onClose={()=>setAnchor(null)}><MenuItem onClick={()=>{setAnchor(null);navigate(`/lessons/${selected?.id}`);}}>Open lesson</MenuItem><MenuItem disabled={deleting} onClick={remove} sx={{color:'error.main'}}>{deleting?'Deleting…':'Delete lesson'}</MenuItem></Menu></>;
}
