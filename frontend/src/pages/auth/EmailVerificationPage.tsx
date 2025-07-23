import { useState, useEffect } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { authApi } from '@/api/authApi'
import LoadingSpinner from '@/components/ui/LoadingSpinner'
import { CheckCircle, XCircle } from 'lucide-react'

const EmailVerificationPage = () => {
  const [searchParams] = useSearchParams()
  const [status, setStatus] = useState<'loading' | 'success' | 'error'>('loading')
  const [message, setMessage] = useState('')
  
  const token = searchParams.get('token')

  useEffect(() => {
    const verifyEmail = async () => {
      if (!token) {
        setStatus('error')
        setMessage('인증 토큰이 없습니다.')
        return
      }

      try {
        const response = await authApi.verifyEmail(token)
        setStatus('success')
        setMessage(response.message || '이메일이 성공적으로 인증되었습니다.')
      } catch (error: any) {
        setStatus('error')
        setMessage(error.message || '이메일 인증에 실패했습니다.')
      }
    }

    verifyEmail()
  }, [token])

  if (status === 'loading') {
    return (
      <div className="text-center space-y-4">
        <LoadingSpinner size="lg" />
        <h2 className="text-2xl font-bold text-gray-900">이메일 인증 중...</h2>
        <p className="text-gray-600">잠시만 기다려주세요.</p>
      </div>
    )
  }

  if (status === 'success') {
    return (
      <div className="text-center space-y-4">
        <CheckCircle className="h-16 w-16 text-green-500 mx-auto" />
        <h2 className="text-2xl font-bold text-gray-900">인증 완료!</h2>
        <p className="text-gray-600">{message}</p>
        <Link to="/auth/login" className="btn btn-primary">
          로그인하기
        </Link>
      </div>
    )
  }

  return (
    <div className="text-center space-y-4">
      <XCircle className="h-16 w-16 text-red-500 mx-auto" />
      <h2 className="text-2xl font-bold text-gray-900">인증 실패</h2>
      <p className="text-gray-600">{message}</p>
      <Link to="/auth/login" className="btn btn-outline">
        로그인 페이지로 돌아가기
      </Link>
    </div>
  )
}

export default EmailVerificationPage 