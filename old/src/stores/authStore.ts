import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import { authApi } from '@/api/authApi'
import type { User, LoginCredentials, RegisterData } from '@/types/auth'

interface AuthState {
  user: User | null
  accessToken: string | null
  refreshToken: string | null
  isAuthenticated: boolean
  isLoading: boolean
}

interface AuthActions {
  login: (credentials: LoginCredentials) => Promise<void>
  register: (data: RegisterData) => Promise<void>
  logout: () => Promise<void>
  refreshAuth: () => Promise<void>
  initializeAuth: () => void
  setUser: (user: User) => void
  clearAuth: () => void
}

type AuthStore = AuthState & AuthActions

export const useAuthStore = create<AuthStore>()(
  persist(
    (set, get) => ({
      // State
      user: null,
      accessToken: null,
      refreshToken: null,
      isAuthenticated: false,
      isLoading: true,

      // Actions
      login: async (credentials: LoginCredentials) => {
        try {
          set({ isLoading: true })
          
          const response = await authApi.login(credentials)
          
          set({
            user: response.user,
            accessToken: response.accessToken,
            refreshToken: response.refreshToken,
            isAuthenticated: true,
            isLoading: false
          })
        } catch (error) {
          set({ isLoading: false })
          throw error
        }
      },

      register: async (data: RegisterData) => {
        try {
          set({ isLoading: true })
          
          await authApi.register(data)
          
          set({ isLoading: false })
        } catch (error) {
          set({ isLoading: false })
          throw error
        }
      },

      logout: async () => {
        try {
          const { accessToken } = get()
          
          if (accessToken) {
            await authApi.logout()
          }
        } catch (error) {
          console.error('Logout error:', error)
        } finally {
          get().clearAuth()
        }
      },

      refreshAuth: async () => {
        try {
          const { refreshToken } = get()
          
          if (!refreshToken) {
            throw new Error('No refresh token')
          }

          const response = await authApi.refreshToken(refreshToken)
          
          set({
            accessToken: response.accessToken,
            refreshToken: response.refreshToken,
            isAuthenticated: true
          })
        } catch (error) {
          get().clearAuth()
          throw error
        }
      },

      initializeAuth: () => {
        const { accessToken, refreshToken } = get()
        
        if (accessToken && refreshToken) {
          // Validate tokens and refresh if needed
          get().refreshAuth().catch(() => {
            get().clearAuth()
          })
        } else {
          set({ isLoading: false })
        }
      },

      setUser: (user: User) => {
        set({ user })
      },

      clearAuth: () => {
        set({
          user: null,
          accessToken: null,
          refreshToken: null,
          isAuthenticated: false,
          isLoading: false
        })
      }
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({
        accessToken: state.accessToken,
        refreshToken: state.refreshToken,
        user: state.user,
      }),
    }
  )
) 