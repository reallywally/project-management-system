import axios, { AxiosInstance, AxiosError } from 'axios'
import { useAuthStore } from '@/stores/authStore'
// import toast from 'react-hot-toast'

const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1'

// Create axios instance
const apiClient: AxiosInstance = axios.create({
  baseURL: BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Request interceptor to add auth token
apiClient.interceptors.request.use(
  (config) => {
    const token = useAuthStore.getState().accessToken
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor to handle token refresh
apiClient.interceptors.response.use(
  (response) => {
    return response
  },
  async (error: AxiosError) => {
    const originalRequest = error.config as any
    
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true
      
      try {
        await useAuthStore.getState().refreshAuth()
        
        const newToken = useAuthStore.getState().accessToken
        if (newToken && originalRequest.headers) {
          originalRequest.headers.Authorization = `Bearer ${newToken}`
        }
        
        return apiClient(originalRequest)
      } catch (refreshError) {
        useAuthStore.getState().clearAuth()
        window.location.href = '/auth/login'
        return Promise.reject(refreshError)
      }
    }
    
    // Handle other errors
    const errorMessage = error.response?.data?.error?.message || 
                        error.response?.data?.message || 
                        error.message || 
                        'An unexpected error occurred'
    
    // Don't show error toast for certain endpoints
    const silentEndpoints = ['/auth/refresh', '/auth/verify-email']
    const shouldShowToast = !silentEndpoints.some(endpoint => 
      originalRequest?.url?.includes(endpoint)
    )
    
    if (shouldShowToast && error.response?.status !== 401) {
      // toast.error(errorMessage)
      console.error(errorMessage)
    }
    
    return Promise.reject(error)
  }
)

export default apiClient

// Generic API response type
export interface ApiResponse<T = any> {
  success: boolean
  data?: T
  message?: string
  error?: {
    code: string
    message: string
    details?: string
  }
  timestamp: string
}

// Generic API error type
export interface ApiError {
  code: string
  message: string
  details?: string
}

// Helper function to extract data from API response
export const extractApiData = <T>(response: any): T => {
  return response.data?.data || response.data
}

// Helper function to handle API errors
export const handleApiError = (error: AxiosError): never => {
  const apiError: ApiError = {
    code: error.response?.data?.error?.code || 'UNKNOWN_ERROR',
    message: error.response?.data?.error?.message || 
             error.response?.data?.message || 
             error.message || 
             'An unexpected error occurred',
    details: error.response?.data?.error?.details
  }
  
  throw apiError
} 