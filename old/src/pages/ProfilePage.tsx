import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Camera, Mail, Phone, Calendar, Key } from 'lucide-react'
import { useAuthStore } from '@/stores/authStore'
import { getInitials } from '@/lib/utils'
import LoadingSpinner from '@/components/ui/LoadingSpinner'
import toast from 'react-hot-toast'

const profileSchema = z.object({
  name: z.string().min(2, '이름은 최소 2자 이상이어야 합니다'),
  nickname: z.string().min(2, '닉네임은 최소 2자 이상이어야 합니다'),
  phone: z.string().optional(),
  email: z.string().email('올바른 이메일 주소를 입력해주세요')
})

type ProfileFormData = z.infer<typeof profileSchema>

const ProfilePage = () => {
  const { user, setUser } = useAuthStore()
  const [isEditing, setIsEditing] = useState(false)
  const [isLoading, setIsLoading] = useState(false)
  const [activeTab, setActiveTab] = useState('profile')

  const {
    register,
    handleSubmit,
    formState: { errors },
    reset
  } = useForm<ProfileFormData>({
    resolver: zodResolver(profileSchema),
    defaultValues: {
      name: user?.name || '',
      nickname: user?.nickname || '',
      phone: user?.phone || '',
      email: user?.email || ''
    }
  })

  const onSubmit = async (data: ProfileFormData) => {
    try {
      setIsLoading(true)
      // API 호출 시뮬레이션
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      // 사용자 정보 업데이트 (실제로는 API 호출)
      if (user) {
        setUser({
          ...user,
          name: data.name,
          nickname: data.nickname,
          phone: data.phone,
          email: data.email
        })
      }
      
      setIsEditing(false)
      toast.success('프로필이 업데이트되었습니다.')
    } catch (error) {
      toast.error('프로필 업데이트에 실패했습니다.')
    } finally {
      setIsLoading(false)
    }
  }

  const handleCancel = () => {
    reset()
    setIsEditing(false)
  }

  const tabs = [
    { id: 'profile', name: '프로필', icon: Mail },
    { id: 'security', name: '보안', icon: Key },
    { id: 'activity', name: '활동 내역', icon: Calendar }
  ]

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">프로필 설정</h1>
        <p className="mt-1 text-sm text-gray-600">
          계정 정보를 관리하고 보안 설정을 변경할 수 있습니다.
        </p>
      </div>

      {/* Tab Navigation */}
      <div className="border-b border-gray-200">
        <nav className="-mb-px flex space-x-8">
          {tabs.map((tab) => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              className={`${
                activeTab === tab.id
                  ? 'border-indigo-500 text-indigo-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              } whitespace-nowrap py-2 px-1 border-b-2 font-medium text-sm flex items-center space-x-2`}
            >
              <tab.icon className="h-4 w-4" />
              <span>{tab.name}</span>
            </button>
          ))}
        </nav>
      </div>

      {/* Profile Tab */}
      {activeTab === 'profile' && (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2">
            <div className="card p-6">
              <div className="flex justify-between items-center mb-6">
                <h2 className="text-lg font-medium text-gray-900">기본 정보</h2>
                <div className="flex space-x-3">
                  {isEditing ? (
                    <>
                      <button
                        type="button"
                        onClick={handleCancel}
                        className="btn btn-outline"
                        disabled={isLoading}
                      >
                        취소
                      </button>
                      <button
                        onClick={handleSubmit(onSubmit)}
                        className="btn btn-primary flex items-center space-x-2"
                        disabled={isLoading}
                      >
                        {isLoading && <LoadingSpinner size="sm" />}
                        <span>저장</span>
                      </button>
                    </>
                  ) : (
                    <button
                      onClick={() => setIsEditing(true)}
                      className="btn btn-primary"
                    >
                      편집
                    </button>
                  )}
                </div>
              </div>

              <form className="space-y-6">
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
                  <div>
                    <label className="block text-sm font-medium text-gray-700">
                      이름 *
                    </label>
                    <input
                      {...register('name')}
                      type="text"
                      disabled={!isEditing}
                      className="input mt-1 w-full"
                    />
                    {errors.name && (
                      <p className="mt-1 text-sm text-red-600">{errors.name.message}</p>
                    )}
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700">
                      닉네임 *
                    </label>
                    <input
                      {...register('nickname')}
                      type="text"
                      disabled={!isEditing}
                      className="input mt-1 w-full"
                    />
                    {errors.nickname && (
                      <p className="mt-1 text-sm text-red-600">{errors.nickname.message}</p>
                    )}
                  </div>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700">
                    이메일 *
                  </label>
                  <input
                    {...register('email')}
                    type="email"
                    disabled={!isEditing}
                    className="input mt-1 w-full"
                  />
                  {errors.email && (
                    <p className="mt-1 text-sm text-red-600">{errors.email.message}</p>
                  )}
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700">
                    전화번호
                  </label>
                  <input
                    {...register('phone')}
                    type="tel"
                    disabled={!isEditing}
                    className="input mt-1 w-full"
                    placeholder="선택사항"
                  />
                  {errors.phone && (
                    <p className="mt-1 text-sm text-red-600">{errors.phone.message}</p>
                  )}
                </div>
              </form>
            </div>
          </div>

          {/* Profile Sidebar */}
          <div className="space-y-6">
            {/* Avatar */}
            <div className="card p-6 text-center">
              <div className="relative inline-block">
                <div className="w-24 h-24 bg-indigo-500 rounded-full flex items-center justify-center text-white text-2xl font-bold mx-auto">
                  {getInitials(user?.name || 'User')}
                </div>
                <button className="absolute bottom-0 right-0 bg-white rounded-full p-2 shadow-lg border border-gray-200 hover:bg-gray-50">
                  <Camera className="h-4 w-4 text-gray-600" />
                </button>
              </div>
              <h3 className="mt-4 font-medium text-gray-900">{user?.nickname || user?.name}</h3>
              <p className="text-sm text-gray-500">{user?.email}</p>
              <div className="mt-4 flex items-center justify-center space-x-2">
                <Mail className="h-4 w-4 text-gray-400" />
                <span className={`text-xs px-2 py-1 rounded-full ${
                  user?.emailVerified 
                    ? 'bg-green-100 text-green-800' 
                    : 'bg-red-100 text-red-800'
                }`}>
                  {user?.emailVerified ? '인증됨' : '미인증'}
                </span>
              </div>
            </div>

            {/* Account Info */}
            <div className="card p-6">
              <h3 className="font-medium text-gray-900 mb-4">계정 정보</h3>
              <div className="space-y-3 text-sm">
                <div className="flex justify-between">
                  <span className="text-gray-600">가입일:</span>
                  <span className="text-gray-900">2024-01-15</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">마지막 로그인:</span>
                  <span className="text-gray-900">방금 전</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">권한:</span>
                  <span className="text-gray-900">사용자</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Security Tab */}
      {activeTab === 'security' && (
        <div className="card p-6">
          <h2 className="text-lg font-medium text-gray-900 mb-6">보안 설정</h2>
          <div className="space-y-6">
            <div className="border border-gray-200 rounded-lg p-4">
              <div className="flex justify-between items-start">
                <div>
                  <h3 className="font-medium text-gray-900">비밀번호</h3>
                  <p className="text-sm text-gray-600 mt-1">계정 보안을 위해 정기적으로 비밀번호를 변경하세요.</p>
                </div>
                <button className="btn btn-outline">변경</button>
              </div>
            </div>

            <div className="border border-gray-200 rounded-lg p-4">
              <div className="flex justify-between items-start">
                <div>
                  <h3 className="font-medium text-gray-900">이중 인증 (2FA)</h3>
                  <p className="text-sm text-gray-600 mt-1">추가 보안을 위해 이중 인증을 활성화하세요.</p>
                </div>
                <button className="btn btn-outline">설정</button>
              </div>
            </div>

            <div className="border border-gray-200 rounded-lg p-4">
              <div className="flex justify-between items-start">
                <div>
                  <h3 className="font-medium text-gray-900">활성 세션</h3>
                  <p className="text-sm text-gray-600 mt-1">다른 기기에서 로그인된 세션을 관리합니다.</p>
                </div>
                <button className="btn btn-outline">관리</button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Activity Tab */}
      {activeTab === 'activity' && (
        <div className="card p-6">
          <h2 className="text-lg font-medium text-gray-900 mb-6">활동 내역</h2>
          <div className="space-y-4">
            {[
              { action: '프로필 정보 수정', time: '2시간 전', ip: '192.168.1.100' },
              { action: '로그인', time: '4시간 전', ip: '192.168.1.100' },
              { action: '비밀번호 변경', time: '1일 전', ip: '192.168.1.100' },
              { action: '이슈 생성', time: '2일 전', ip: '192.168.1.100' },
              { action: '프로젝트 참여', time: '3일 전', ip: '192.168.1.100' }
            ].map((activity, index) => (
              <div key={index} className="flex justify-between items-center py-3 border-b border-gray-100 last:border-b-0">
                <div>
                  <div className="font-medium text-gray-900">{activity.action}</div>
                  <div className="text-sm text-gray-500">IP: {activity.ip}</div>
                </div>
                <div className="text-sm text-gray-500">{activity.time}</div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}

export default ProfilePage 