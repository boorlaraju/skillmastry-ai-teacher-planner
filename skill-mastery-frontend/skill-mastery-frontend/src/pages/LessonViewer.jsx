import { useState } from 'react';
import { Accordion, AccordionDetails, AccordionSummary, Box, Chip, Divider, Grid, List, ListItem, ListItemText, Paper, Tab, Tabs, Typography } from '@mui/material';
import ExpandMoreRoundedIcon from '@mui/icons-material/ExpandMoreRounded';
import Section from '../components/Section';
import { safeArray, titleCase } from '../utils/format';
function BulletList({ items }) { return <List dense>{safeArray(items).map((item, i) => <ListItem key={i} sx={{ display: 'list-item', ml: 2, py: .2 }}><ListItemText primary={typeof item === 'string' ? item : item?.title || item?.name || JSON.stringify(item)}/></ListItem>)}</List>; }

function Questions({ bank }) {
  const groups = [
    { key: 'multipleChoice', label: 'Multiple choice' },
    { key: 'shortAnswer', label: 'Short answer' },
    { key: 'longAnswer', label: 'Long answer' },
    { key: 'applicationQuestions', label: 'Application questions' }
  ];

  return (
    <>
      {groups.map((group) => (
        <Box key={group.key} mb={2}>
          <Typography fontWeight={750} mb={1}>
            {group.label}
          </Typography>

          {safeArray(bank?.[group.key]).length > 0 ? (
            safeArray(bank[group.key]).map((q, i) => (
              <Accordion
                key={i}
                disableGutters
                sx={{
                  mb: 1,
                  border: '1px solid #e9edf5',
                  borderRadius: '10px !important',
                  '&:before': { display: 'none' }
                }}
              >
                <AccordionSummary
                  expandIcon={<ExpandMoreRoundedIcon />}
                >
                  <Typography fontWeight={600}>
                    {i + 1}. {
                      typeof q === 'string'
                        ? q
                        : q?.question || 'Untitled question'
                    }
                  </Typography>
                </AccordionSummary>

                <AccordionDetails>
                  {q?.options && (
                    <List dense>
                      {safeArray(q.options).map((op, j) => (
                        <ListItem key={j}>
                          <ListItemText
                            primary={`${String.fromCharCode(65 + j)}. ${op}`}
                          />
                        </ListItem>
                      ))}
                    </List>
                  )}

                  {q?.correctAnswer && (
                    <Typography
                      variant="body2"
                      color="success.main"
                      fontWeight={700}
                    >
                      Answer: {q.correctAnswer}
                    </Typography>
                  )}

                  {q?.explanation && (
                    <Typography
                      variant="body2"
                      color="text.secondary"
                      mt={1}
                    >
                      {q.explanation}
                    </Typography>
                  )}
                </AccordionDetails>
              </Accordion>
            ))
          ) : (
            <Typography variant="body2" color="text.secondary">
              No questions provided.
            </Typography>
          )}
        </Box>
      ))}
    </>
  );
}

export default function LessonViewer({ lesson, preview = false }) {
 const [tab, setTab] = useState(0); 
 const d = lesson?.detailedContent || {}; 
 const activities = safeArray(d.teachingActivities?.length ? d.teachingActivities : lesson?.teachingActivities);
 return <Paper sx={{ overflow: 'hidden', mb: 3 }}><Box sx={{ p: { xs: 2, md: 3 }, background: 'linear-gradient(110deg,#f0f3ff,#f8faff)' }}><Box display="flex" gap={1} flexWrap="wrap" mb={1}><Chip label={`Class ${lesson.classNumber ?? '—'}`} color="primary" size="small"/><Chip label={lesson.subject || 'Subject'} size="small"/><Chip label={`${lesson.duration || 45} minutes`} size="small"/></Box><Typography variant="h5">{lesson.topic || 'Untitled lesson'}</Typography><Typography color="text.secondary" mt={1}>{d.topicDescription || lesson.introduction || 'AI-generated lesson plan'}</Typography></Box><Tabs value={tab} onChange={(_, v) => setTab(v)} variant="scrollable" scrollButtons="auto" sx={{ px: 2, borderBottom: '1px solid #e9edf5' }}>{['Overview', 'Teaching plan', 'Activities & experiments', 'Assessment', 'Resources'].map(x => <Tab key={x} label={x}/>)}</Tabs><Box sx={{ p: { xs: 2, md: 3 } }}>
 {tab === 0 && <><Section title="Learning objectives"><BulletList items={lesson.learningObjectives}/></Section><Section title="Prerequisites"><BulletList items={d.prerequisites}/></Section><Section title="Key concepts">{safeArray(d.keyConcepts).length ? safeArray(d.keyConcepts).map((x,i) => <Box key={i} mb={2}><Typography fontWeight={750}>{x.title || x.concept || `Concept ${i+1}`}</Typography><Typography color="text.secondary" variant="body2">{x.description || x.explanation || ''}</Typography></Box>) : <Typography color="text.secondary">No key concepts were returned.</Typography>}</Section><Section title="Lesson introduction"><Typography whiteSpace="pre-wrap">{lesson.introduction || '—'}</Typography></Section><Section title="Detailed explanation"><Typography whiteSpace="pre-wrap">{lesson.explanation || '—'}</Typography></Section>{d.equations?.length > 0 && <Section title="Equations"><BulletList items={d.equations.map(x => typeof x === 'string' ? x : `${x.name || x.title || 'Equation'}: ${x.formula || x.equation || ''} ${x.description || ''}`)}/></Section>}</>}
 {tab === 1 && <Section title="Teaching sequence" subtitle="A structured, time-based classroom flow.">{activities.length ? activities.map((a,i) => <Box key={i} sx={{ display: 'grid', gridTemplateColumns: '94px 1fr', gap: 2, pb: 2, mb: 2, borderBottom: i === activities.length-1 ? 0 : '1px solid #edf0f6' }}><Box><Chip color="primary" variant="outlined" size="small" label={`${a.durationMinutes || a.duration || '—'} min`}/></Box><Box><Typography fontWeight={750}>{a.title || a.step || `Activity ${i+1}`}</Typography><Typography color="text.secondary" variant="body2" mt={.5}>{a.description || a.instructions || (typeof a === 'string' ? a : '')}</Typography></Box></Box>) : <BulletList items={lesson.teachingActivities}/>}</Section>}
 {tab === 2 && <><Section title="Practical activity"><Typography whiteSpace="pre-wrap">{lesson.practicalActivity || 'No practical activity provided.'}</Typography></Section>{safeArray(d.experiments).map((exp,i) => <Section key={i} title={exp.title || `Experiment ${i+1}`}><Typography>{exp.objective || ''}</Typography><Typography fontWeight={700} mt={2}>Materials</Typography><BulletList items={exp.materials}/><Typography fontWeight={700}>Procedure</Typography><BulletList items={exp.procedure}/>{exp.observation && <Typography mt={1}><b>Observation:</b> {exp.observation}</Typography>}{exp.conclusion && <Typography mt={1}><b>Conclusion:</b> {exp.conclusion}</Typography>}</Section>)}<Section title="Examples"><BulletList items={d.examples?.map(x => typeof x === 'string' ? x : `${x.title || x.example || ''}: ${x.description || x.explanation || ''}`)}/></Section></>}
 {tab === 3 && <><Section title="Assessment questions"><BulletList items={lesson.assessmentQuestions}/></Section><Section title="Question bank"><Questions bank={d.questionBank}/></Section><Section title="Assessment rubric">{safeArray(d.assessmentRubric).length ? <Grid container spacing={1}>{d.assessmentRubric.map((r,i)=><Grid item xs={12} sm={6} key={i}><Paper variant="outlined" sx={{ p: 2, height: '100%' }}><Typography fontWeight={750}>{r.criterion || r.title || `Criterion ${i+1}`}</Typography><Typography color="text.secondary" variant="body2" mt={.5}>{r.description}</Typography><Chip sx={{ mt: 1 }} size="small" label={`${r.marks ?? r.score ?? '—'} marks`}/></Paper></Grid>)}</Grid> : <Typography color="text.secondary">No rubric provided.</Typography>}</Section><Section title="Homework"><Typography whiteSpace="pre-wrap">{lesson.homework || 'No homework provided.'}</Typography></Section></>}
 {tab === 4 && <><Section title="Visual aids" subtitle="These are AI image prompts, not generated image files.">{safeArray(d.visualAids).length ? <Grid container spacing={2}>{d.visualAids.map((v,i)=><Grid item xs={12} md={6} key={i}><Paper variant="outlined" sx={{ p: 2, height: '100%' }}><Typography fontWeight={750}>{v.title || `Visual aid ${i+1}`}</Typography><Typography variant="body2" color="text.secondary" mt={1}>{v.altText || v.imagePrompt || ''}</Typography>{v.imagePrompt && <Typography variant="caption" display="block" mt={1} sx={{ bgcolor: '#f6f8fc', p: 1, borderRadius: 1 }}>Image prompt: {v.imagePrompt}</Typography>}</Paper></Grid>)}</Grid> : <Typography color="text.secondary">No visual aids provided.</Typography>}</Section><Section title="References"><BulletList items={d.references}/></Section></>}
 </Box></Paper>;
}
