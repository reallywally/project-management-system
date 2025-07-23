import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { authApi } from '@/api/authApi'
import LoadingSpinner from '@/components/ui/LoadingSpinner'
import toast from 'react-hot-toast'

const forgotPasswordSchema = z.object({
  email: z.string().email('올바른 이메일 주소를 입력해주세요')
})

type ForgotPasswordFormData = z.infer<typeof forgotPasswordSchema>

const ForgotPasswordPage = () => {
  const [isLoading, setIsLoading] = useState(false)
  const [isSubmitted, setIsSubmitted] = useState(false)

  const {
    register,
    handleSubmit,
    formState: { errors }
  } = useForm<ForgotPasswordFormData>({
    resolver: zodResolver(forgotPasswordSchema)
  })

  const onSubmit = async (data: ForgotPasswordFormData) => {
    try {
      setIsLoading(true)
      await authApi.forgotPassword(data.email)
      setIsSubmitted(true)
      toast.success('비밀번호 재설정 이메일을 발송했습니다.')
    } catch (error: any) {
      toast.error(error.message || '이메일 발송에 실패했습니다.')
    } finally {
      setIsLoading(false)
    }
  }

  if (isSubmitted) {
    return (
      <div className="text-center space-y-4">
        <h2 className="text-2xl font-bold text-gray-900">이메일을 확인해주세요</h2>
        <p className="text-gray-600">
          비밀번호 재설정 링크를 이메일로 발송했습니다.
          <br />
          이메일을 확인하고 링크를 클릭해주세요.
        </p>
        <Link to="/auth/login" className="btn btn-outline">
          로그인 페이지로 돌아가기
        </Link>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-center text-3xl font-extrabold text-gray-900">
          비밀번호 찾기
        </h2>
        <p className="mt-2 text-center text-sm text-gray-600">
          가입할 때 사용한 이메일 주소를 입력해주세요.
        </p>
      </div>

      <form className="space-y-6" onSubmit={handleSubmit(onSubmit)}>
        <div>
          <label htmlFor="email" className="block text-sm font-medium text-gray-700">
            이메일
          </label>
          <div className="mt-1">
            <input
              {...register('email')}
              type="email"
              autoComplete="email"
              className="input w-full"
              placeholder="name@company.com"
            />
            {errors.email && (
              <p className="mt-1 text-sm text-red-600">{errors.email.message}</p>
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
            <span>재설정 링크 보내기</span>
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

export default ForgotPasswordPage 