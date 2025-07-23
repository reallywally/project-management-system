import apiClient, { ApiResponse, extractApiData, handleApiError } from './client'
import type { LoginCredentials, RegisterData, AuthResponse, RefreshTokenResponse, User } from '@/types/auth'

export const authApi = {
  // Login
  login: async (credentials: LoginCredentials): Promise<AuthResponse> => {
    try {
      const response = await apiClient.post<ApiResponse<AuthResponse>>('/auth/login', credentials)
      return extractApiData<AuthResponse>(response)
    } catch (error: any) {
      return handleApiError(error)
    }
  },

  // Register
  register: async (data: RegisterData): Promise<{ user: User; message: string }> => {
    try {
      const response = await apiClient.post<ApiResponse<{ user: User; message: string }>>('/auth/register', data)
      return extractApiData<{ user: User; message: string }>(response)
    } catch (error: any) {
      return handleApiError(error)
    }
  },

  // Refresh token
  refreshToken: async (refreshToken: string): Promise<RefreshTokenResponse> => {
    try {
      const response = await apiClient.post<ApiResponse<RefreshTokenResponse>>('/auth/refresh', {
        refreshToken
      })
      return extractApiData<RefreshTokenResponse>(response)
    } catch (error: any) {
      return handleApiError(error)
    }
  },

  // Logout
  logout: async (): Promise<void> => {
    try {
      await apiClient.post('/auth/logout')
    } catch (error: any) {
      return handleApiError(error)
    }
  },

  // Forgot password
  forgotPassword: async (email: string): Promise<{ message: string }> => {
    try {
      const response = await apiClient.post<ApiResponse<{ message: string }>>('/auth/forgot-password', {
        email
      })
      return extractApiData<{ message: string }>(response)
    } catch (error: any) {
      return handleApiError(error)
    }
  },

  // Reset password
  resetPassword: async (token: string, newPassword: string): Promise<{ message: string }> => {
    try {
      const response = await apiClient.post<ApiResponse<{ message: string }>>('/auth/reset-password', {
        token,
        newPassword
      })
      return extractApiData<{ message: string }>(response)
    } catch (error: any) {
      return handleApiError(error)
    }
  },

  // Verify email
  verifyEmail: async (token: string): Promise<{ message: string }> => {
    try {
      const response = await apiClient.get<ApiResponse<{ message: string }>>(`/auth/verify-email?token=${token}`)
      return extractApiData<{ message: string }>(response)
    } catch (error: any) {
      return handleApiError(error)
    }
  }
} 