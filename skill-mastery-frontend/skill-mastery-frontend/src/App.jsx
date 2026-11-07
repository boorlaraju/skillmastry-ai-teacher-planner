import { Navigate, Route, Routes } from 'react-router-dom';
import ProtectedRoute from './components/ProtectedRoute';
import DashboardLayout from './layouts/DashboardLayout';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import GenerateLesson from './pages/GenerateLesson';
import Lessons from './pages/Lessons';
import LessonDetails from './pages/LessonDetails';
export default function App(){return <Routes><Route path="/login" element={<Login/>}/><Route path="/register" element={<Register/>}/><Route element={<ProtectedRoute/>}><Route element={<DashboardLayout/>}><Route path="/" element={<Dashboard/>}/><Route path="/generate" element={<GenerateLesson/>}/><Route path="/lessons" element={<Lessons/>}/><Route path="/lessons/:id" element={<LessonDetails/>}/></Route></Route><Route path="*" element={<Navigate to="/" replace/>}/></Routes>;}
