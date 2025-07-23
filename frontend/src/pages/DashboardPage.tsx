import { useAuthStore } from '@/stores/authStore'

const DashboardPage = () => {
  const { user } = useAuthStore()

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">
          안녕하세요, {user?.nickname || user?.name}님! 👋
        </h1>
        <p className="mt-1 text-sm text-gray-600">
          프로젝트 관리 시스템 대시보드입니다.
        </p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
        <div className="card p-5">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <div className="w-8 h-8 bg-indigo-500 rounded-md flex items-center justify-center">
                <span className="text-white font-semibold">P</span>
              </div>
            </div>
            <div className="ml-4">
              <dt className="text-sm font-medium text-gray-500 truncate">내 프로젝트</dt>
              <dd className="text-lg font-medium text-gray-900">5</dd>
            </div>
          </div>
        </div>

        <div className="card p-5">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <div className="w-8 h-8 bg-green-500 rounded-md flex items-center justify-center">
                <span className="text-white font-semibold">I</span>
              </div>
            </div>
            <div className="ml-4">
              <dt className="text-sm font-medium text-gray-500 truncate">할당된 이슈</dt>
              <dd className="text-lg font-medium text-gray-900">12</dd>
            </div>
          </div>
        </div>

        <div className="card p-5">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <div className="w-8 h-8 bg-yellow-500 rounded-md flex items-center justify-center">
                <span className="text-white font-semibold">R</span>
              </div>
            </div>
            <div className="ml-4">
              <dt className="text-sm font-medium text-gray-500 truncate">진행 중인 이슈</dt>
              <dd className="text-lg font-medium text-gray-900">8</dd>
            </div>
          </div>
        </div>

        <div className="card p-5">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <div className="w-8 h-8 bg-red-500 rounded-md flex items-center justify-center">
                <span className="text-white font-semibold">D</span>
              </div>
            </div>
            <div className="ml-4">
              <dt className="text-sm font-medium text-gray-500 truncate">마감 임박</dt>
              <dd className="text-lg font-medium text-gray-900">3</dd>
            </div>
          </div>
        </div>
      </div>

      {/* Recent Activity */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="card p-6">
          <h3 className="text-lg font-medium text-gray-900 mb-4">최근 활동</h3>
          <div className="space-y-4">
            <div className="flex space-x-3">
              <div className="flex-shrink-0">
                <div className="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center">
                  <span className="text-blue-600 text-sm font-medium">JS</span>
                </div>
              </div>
              <div className="min-w-0 flex-1">
                <p className="text-sm text-gray-900">
                  <span className="font-medium">홍길동</span>이 이슈 
                  <span className="font-medium">PMS-123</span>을 생성했습니다.
                </p>
                <p className="text-xs text-gray-500">2시간 전</p>
              </div>
            </div>

            <div className="flex space-x-3">
              <div className="flex-shrink-0">
                <div className="w-8 h-8 bg-green-100 rounded-full flex items-center justify-center">
                  <span className="text-green-600 text-sm font-medium">KD</span>
                </div>
              </div>
              <div className="min-w-0 flex-1">
                <p className="text-sm text-gray-900">
                  <span className="font-medium">김도현</span>이 이슈 
                  <span className="font-medium">PMS-122</span>를 완료했습니다.
                </p>
                <p className="text-xs text-gray-500">4시간 전</p>
              </div>
            </div>

            <div className="flex space-x-3">
              <div className="flex-shrink-0">
                <div className="w-8 h-8 bg-purple-100 rounded-full flex items-center justify-center">
                  <span className="text-purple-600 text-sm font-medium">LM</span>
                </div>
              </div>
              <div className="min-w-0 flex-1">
                <p className="text-sm text-gray-900">
                  <span className="font-medium">이민수</span>가 프로젝트 
                  <span className="font-medium">웹 애플리케이션</span>에 댓글을 달았습니다.
                </p>
                <p className="text-xs text-gray-500">1일 전</p>
              </div>
            </div>
          </div>
        </div>

        <div className="card p-6">
          <h3 className="text-lg font-medium text-gray-900 mb-4">나의 할 일</h3>
          <div className="space-y-3">
            <div className="flex items-center space-x-3">
              <input type="checkbox" className="h-4 w-4 text-indigo-600" />
              <span className="text-sm text-gray-900">로그인 기능 구현</span>
              <span className="text-xs text-red-600">내일 마감</span>
            </div>
            <div className="flex items-center space-x-3">
              <input type="checkbox" className="h-4 w-4 text-indigo-600" />
              <span className="text-sm text-gray-900">API 문서 작성</span>
              <span className="text-xs text-yellow-600">이번 주</span>
            </div>
            <div className="flex items-center space-x-3">
              <input type="checkbox" checked className="h-4 w-4 text-indigo-600" />
              <span className="text-sm text-gray-500 line-through">데이터베이스 설계</span>
              <span className="text-xs text-green-600">완료</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default DashboardPage 