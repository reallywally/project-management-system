import { useParams } from 'react-router-dom'

const KanbanBoardPage = () => {
  const { projectKey } = useParams()

  const columns = [
    { id: 'TODO', title: '할 일', color: 'bg-gray-100' },
    { id: 'IN_PROGRESS', title: '진행 중', color: 'bg-blue-100' },
    { id: 'REVIEW', title: '리뷰', color: 'bg-yellow-100' },
    { id: 'DONE', title: '완료', color: 'bg-green-100' }
  ]

  const sampleIssues = {
    TODO: [
      { id: 1, title: '로그인 UI 개선', type: 'TASK', priority: 'HIGH', assignee: '홍길동' },
      { id: 2, title: '비밀번호 찾기 기능', type: 'FEATURE', priority: 'MEDIUM', assignee: '김도현' }
    ],
    IN_PROGRESS: [
      { id: 3, title: 'API 문서 작성', type: 'TASK', priority: 'LOW', assignee: '이민수' },
      { id: 4, title: '데이터베이스 최적화', type: 'IMPROVEMENT', priority: 'HIGH', assignee: '박지영' }
    ],
    REVIEW: [
      { id: 5, title: '사용자 권한 관리', type: 'FEATURE', priority: 'MEDIUM', assignee: '정수현' }
    ],    
    DONE: [
      { id: 6, title: '프로젝트 초기 설정', type: 'TASK', priority: 'HIGH', assignee: '홍길동' },
      { id: 7, title: '기본 레이아웃 구성', type: 'TASK', priority: 'MEDIUM', assignee: '김도현' }
    ]
  }

  const getPriorityColor = (priority: string) => {
    switch (priority) {
      case 'HIGH': return 'text-red-600 bg-red-50 border-red-200'
      case 'MEDIUM': return 'text-yellow-600 bg-yellow-50 border-yellow-200'
      case 'LOW': return 'text-green-600 bg-green-50 border-green-200'
      default: return 'text-gray-600 bg-gray-50 border-gray-200'
    }
  }

  const getTypeIcon = (type: string) => {
    switch (type) {
      case 'FEATURE': return '✨'
      case 'BUG': return '🐛'
      case 'IMPROVEMENT': return '⚡'
      case 'TASK': return '📋'
      default: return '📋'
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">칸반 보드 - {projectKey}</h1>
          <p className="mt-1 text-sm text-gray-600">
            드래그 앤 드롭으로 이슈 상태를 변경할 수 있습니다.
          </p>
        </div>
        <button className="btn btn-primary">
          새 이슈 만들기
        </button>
      </div>

      {/* Kanban Board */}
      <div className="flex space-x-6 overflow-x-auto pb-4">
        {columns.map((column) => (
          <div key={column.id} className="flex-shrink-0 w-80">
            <div className={`${column.color} rounded-lg p-4`}>
              <div className="flex items-center justify-between mb-4">
                <h3 className="font-medium text-gray-900">{column.title}</h3>
                <span className="bg-white text-gray-600 text-sm px-2 py-1 rounded">
                  {sampleIssues[column.id as keyof typeof sampleIssues]?.length || 0}
                </span>
              </div>

              <div className="space-y-3">
                {sampleIssues[column.id as keyof typeof sampleIssues]?.map((issue) => (
                  <div
                    key={issue.id}
                    className="bg-white rounded-lg p-4 shadow-sm border border-gray-200 hover:shadow-md transition-shadow cursor-pointer"
                  >
                    <div className="flex items-start justify-between mb-2">
                      <span className="text-lg">{getTypeIcon(issue.type)}</span>
                      <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium border ${getPriorityColor(issue.priority)}`}>
                        {issue.priority}
                      </span>
                    </div>
                    
                    <h4 className="font-medium text-gray-900 mb-2 line-clamp-2">
                      {issue.title}
                    </h4>
                    
                    <div className="flex items-center justify-between text-sm text-gray-500">
                      <span>#{issue.id}</span>
                      <div className="flex items-center space-x-2">
                        <div className="w-6 h-6 bg-gray-200 rounded-full flex items-center justify-center">
                          <span className="text-xs font-medium">
                            {issue.assignee?.charAt(0)}
                          </span>
                        </div>
                        <span className="text-xs">{issue.assignee}</span>
                      </div>
                    </div>
                  </div>
                ))}

                {/* Add new issue button */}
                <button className="w-full border-2 border-dashed border-gray-300 rounded-lg p-4 text-gray-500 hover:border-gray-400 hover:text-gray-600 transition-colors">
                  + 이슈 추가
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Board Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div className="card p-4">
          <div className="text-2xl font-bold text-gray-900">
            {Object.values(sampleIssues).flat().length}
          </div>
          <div className="text-sm text-gray-600">전체 이슈</div>
        </div>
        <div className="card p-4">
          <div className="text-2xl font-bold text-blue-600">
            {sampleIssues.IN_PROGRESS.length}
          </div>
          <div className="text-sm text-gray-600">진행 중</div>
        </div>
        <div className="card p-4">
          <div className="text-2xl font-bold text-green-600">
            {sampleIssues.DONE.length}
          </div>
          <div className="text-sm text-gray-600">완료</div>
        </div>
        <div className="card p-4">
          <div className="text-2xl font-bold text-purple-600">
            {Math.round((sampleIssues.DONE.length / Object.values(sampleIssues).flat().length) * 100)}%
          </div>
          <div className="text-sm text-gray-600">완료율</div>
        </div>
      </div>
    </div>
  )
}

export default KanbanBoardPage 