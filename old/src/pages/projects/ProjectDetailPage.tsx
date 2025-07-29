import { useParams } from 'react-router-dom'

const ProjectDetailPage = () => {
  const { projectKey } = useParams()

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">프로젝트 {projectKey}</h1>
          <p className="mt-1 text-sm text-gray-600">
            프로젝트 상세 정보 및 이슈 관리
          </p>
        </div>
        <div className="flex space-x-3">
          <button className="btn btn-outline">설정</button>
          <button className="btn btn-primary">새 이슈 만들기</button>
        </div>
      </div>

      {/* Project Info */}
      <div className="card p-6">
        <h2 className="text-lg font-medium text-gray-900 mb-4">프로젝트 정보</h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div>
            <dt className="text-sm font-medium text-gray-500">상태</dt>
            <dd className="mt-1 text-sm text-gray-900">진행중</dd>
          </div>
          <div>
            <dt className="text-sm font-medium text-gray-500">생성일</dt>
            <dd className="mt-1 text-sm text-gray-900">2024년 1월 15일</dd>
          </div>
          <div>
            <dt className="text-sm font-medium text-gray-500">멤버</dt>
            <dd className="mt-1 text-sm text-gray-900">5명</dd>
          </div>
        </div>
      </div>

      {/* Issues Table */}
      <div className="card">
        <div className="px-6 py-4 border-b border-gray-200">
          <h3 className="text-lg font-medium text-gray-900">이슈 목록</h3>
        </div>
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  이슈
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  상태
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  담당자
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  우선순위
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  생성일
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {[1, 2, 3, 4, 5].map((i) => (
                <tr key={i} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div>
                      <div className="text-sm font-medium text-gray-900">
                        로그인 기능 구현 #{i}
                      </div>
                      <div className="text-sm text-gray-500">
                        사용자 로그인 기능을 구현합니다
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800">
                      진행중
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center">
                      <div className="h-8 w-8 rounded-full bg-gray-200 flex items-center justify-center">
                        <span className="text-xs font-medium text-gray-600">홍길동</span>
                      </div>
                      <div className="ml-3">
                        <div className="text-sm font-medium text-gray-900">홍길동</div>
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    높음
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    2024-01-15
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}

export default ProjectDetailPage 