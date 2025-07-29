import { useState, useEffect } from 'react'
import { Link, useSearchParams, useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Eye, EyeOff } from 'lucide-react'
import { authApi } from '@/api/authApi'
import LoadingSpinner from '@/components/ui/LoadingSpinner'
import toast from 'react-hot-toast'

const resetPasswordSchema = z.object({
  newPassword: z.string().min(8, '비밀번호는 최소 8자 이상이어야 합니다'),
  confirmPassword: z.string()
}).refine((data) => data.newPassword === data.confirmPassword, {
  message: '비밀번호가 일치하지 않습니다',
  path: ['confirmPassword']
})

type ResetPasswordFormData = z.infer<typeof resetPasswordSchema>

const ResetPasswordPage = () => {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const [showPassword, setShowPassword] = useState(false)
  const [showConfirmPassword, setShowConfirmPassword] = useState(false)
  const [isLoading, setIsLoading] = useState(false)
  
  const token = searchParams.get('token')

  const {
    register,
    handleSubmit,
    formState: { errors }
  } = useForm<ResetPasswordFormData>({
    resolver: zodResolver(resetPasswordSchema)
  })

  useEffect(() => {
    if (!token) {
      toast.error('잘못된 접근입니다.')
      navigate('/auth/login')
    }
  }, [token, navigate])

  const onSubmit = async (data: ResetPasswordFormData) => {
    if (!token) return

    try {
      setIsLoading(true)
      await authApi.resetPassword(token, data.newPassword)
      toast.success('비밀번호가 재설정되었습니다.')
      navigate('/auth/login')
    } catch (error: any) {
      toast.error(error.message || '비밀번호 재설정에 실패했습니다.')
    } finally {
      setIsLoading(false)
    }
  }

  if (!token) {
    return null
  }

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-center text-3xl font-extrabold text-gray-900">
          새 비밀번호 설정
        </h2>
        <p className="mt-2 text-center text-sm text-gray-600">
          새로운 비밀번호를 입력해주세요.
        </p>
      </div>

      <form className="space-y-6" onSubmit={handleSubmit(onSubmit)}>
        <div>
          <label htmlFor="newPassword" className="block text-sm font-medium text-gray-700">
            새 비밀번호
          </label>
          <div className="mt-1 relative">
            <input
              {...register('newPassword')}
              type={showPassword ? 'text' : 'password'}
              autoComplete="new-password"
              className="input w-full pr-10"
              placeholder="최소 8자 이상"
            />
            <button
              type="button"
              className="absolute inset-y-0 right-0 pr-3 flex items-center"
              onClick={() => setShowPassword(!showPassword)}
            >
              {showPassword ? (
                <EyeOff className="h-5 w-5 text-gray-400" />
              ) : (
                <Eye className="h-5 w-5 text-gray-400" />
              )}
            </button>
            {errors.newPassword && (
              <p className="mt-1 text-sm text-red-600">{errors.newPassword.message}</p>
            )}
          </div>
        </div>

        <div>
          <label htmlFor="confirmPassword" className="block text-sm font-medium text-gray-700">
            비밀번호 확인
          </label>
          <div className="mt-1 relative">
            <input
              {...register('confirmPassword')}
              type={showConfirmPassword ? 'text' : 'password'}
              autoComplete="new-password"
              className="input w-full pr-10"
              placeholder="비밀번호를 다시 입력하세요"
            />
            <button
              type="button"
              className="absolute inset-y-0 right-0 pr-3 flex items-center"
              onClick={() => setShowConfirmPassword(!showConfirmPassword)}
            >
              {showConfirmPassword ? (
                <EyeOff className="h-5 w-5 text-gray-400" />
              ) : (
                <Eye className="h-5 w-5 text-gray-400" />
              )}
            </button>
            {errors.confirmPassword && (
              <p className="mt-1 text-sm text-red-600">{errors.confirmPassword.message}</p>
            )}
          </div>
        </div>

        <div>
          <button
            type="submit"
            disabled={isLoading}
            className="btn btn-primary w-full flex justify-center items-center space-x-2"
          >
            {isLoading && <LoadingSpinner size="sm" />}
            <span>비밀번호 변경</span>
          </button>
        </div>
      </form>

      <div className="text-center">
        <Link to="/auth/login" className="text-sm text-indigo-600 hover:text-indigo-500">
          로그인 페이지로 돌아가기
        </Link>
      </div>
    </div>
  )
}

export default ResetPasswordPage 