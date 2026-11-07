export const titleCase = (value = '') => String(value).replace(/([A-Z])/g, ' $1').replace(/^./, c => c.toUpperCase());
export const safeArray = value => Array.isArray(value) ? value : [];
export const formatDate = value => value ? new Date(value).toLocaleDateString(undefined, { day: 'numeric', month: 'short', year: 'numeric' }) : 'Not saved yet';
export const lessonTitle = lesson => lesson?.topic || 'Untitled lesson';
