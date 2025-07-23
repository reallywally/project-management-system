import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuthStore } from '@/stores/authStore'
import { useEffect } from 'react'

// Components
import LoginPage from '@/pages/auth/LoginPage'
import RegisterPage from '@/pages/auth/RegisterPage'
import ForgotPasswordPage from '@/pages/auth/ForgotPasswordPage'
import ResetPasswordPage from '@/pages/auth/ResetPasswordPage'
import EmailVerificationPage from '@/pages/auth/EmailVerificationPage'

// Protected pages
import DashboardPage from '@/pages/DashboardPage'
import ProjectsPage from '@/pages/projects/ProjectsPage'
import ProjectDetailPage from '@/pages/projects/ProjectDetailPage'
import KanbanBoardPage from '@/pages/projects/KanbanBoardPage'
import IssueDetailPage from '@/pages/issues/IssueDetailPage'
import ProfilePage from '@/pages/ProfilePage'

// Layout
import MainLayout from '@/components/layout/MainLayout'
import AuthLayout from '@/components/layout/AuthLayout'

// Loading component
import LoadingSpinner from '@/components/ui/LoadingSpinner'

function App() {
  const { isAuthenticated, isLoading, initializeAuth } = useAuthStore()

  useEffect(() => {
    initializeAuth()
  }, [initializeAuth])

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <LoadingSpinner size="lg" />
      </div>
    )
  }

  return (
    <Routes>
      {/* Public Routes */}
      <Route path="/auth/*" element={
        isAuthenticated ? <Navigate to="/dashboard" replace /> : <AuthRoutes />
      } />
      
      {/* Protected Routes */}
      <Route path="/*" element={
        isAuthenticated ? <ProtectedRoutes /> : <Navigate to="/auth/login" replace />
      } />
    </Routes>
  )
}

function AuthRoutes() {
  return (
    <AuthLayout>
      <Routes>
        <Route path="login" element={<LoginPage />} />
        <Route path="register" element={<RegisterPage />} />
        <Route path="forgot-password" element={<ForgotPasswordPage />} />
        <Route path="reset-password" element={<ResetPasswordPage />} />
        <Route path="verify-email" element={<EmailVerificationPage />} />
        <Route path="*" element={<Navigate to="/auth/login" replace />} />
      </Routes>
    </AuthLayout>
  )
}

function ProtectedRoutes() {
  return (
    <MainLayout>
      <Routes>
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        <Route path="/dashboard" element={<DashboardPage />} />
        
        {/* Projects */}
        <Route path="/projects" element={<ProjectsPage />} />
        <Route path="/projects/:projectKey" element={<ProjectDetailPage />} />
        <Route path="/projects/:projectKey/board" element={<KanbanBoardPage />} />
        
        {/* Issues */}
        <Route path="/issues/:issueId" element={<IssueDetailPage />} />
        
        {/* Profile */}
        <Route path="/profile" element={<ProfilePage />} />
        
        {/* Fallback */}
        <Route path="*" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </MainLayout>
  )
}

export default App 