import { useParams } from 'react-router-dom'
import { Clock, User, Tag, MessageSquare, Paperclip } from 'lucide-react'

const IssueDetailPage = () => {
  const { issueId } = useParams()

  const sampleIssue = {
    id: issueId,
    title: '로그인 기능 구현',
    description: `사용자가 이메일과 비밀번호로 로그인할 수 있는 기능을 구현해야 합니다.

**요구사항:**
- 이메일 형식 검증
- 비밀번호 최소 8자 이상
- 로그인 실패 시 적절한 에러 메시지 표시
- 로그인 성공 시 대시보드로 리다이렉트

**기술 스택:**
- React
- TypeScript
- React Hook Form
- Zod 검증`,
    status: 'IN_PROGRESS',
    priority: 'HIGH',
    type: 'FEATURE',
    reporter: '홍길동',
    assignee: '김도현',
    createdAt: '2024-01-15',
    updatedAt: '2024-01-16',
    dueDate: '2024-01-20',
    labels: ['frontend', 'authentication'],
    comments: [
      {
        id: 1,
        author: '홍길동',
        content: '이슈가 생성되었습니다. 우선순위를 높음으로 설정했습니다.',
        createdAt: '2024-01-15 09:00'
      },
      {
        id: 2,
        author: '김도현',
        content: 'React Hook Form을 사용해서 구현하겠습니다. 내일까지 완료 예정입니다.',
        createdAt: '2024-01-16 14:30'
      }
    ],
    attachments: [
      { id: 1, name: 'login-mockup.png', size: '245KB' },
      { id: 2, name: 'requirements.pdf', size: '1.2MB' }
    ]
  }

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'TODO': return 'bg-gray-100 text-gray-800'
      case 'IN_PROGRESS': return 'bg-blue-100 text-blue-800'
      case 'REVIEW': return 'bg-yellow-100 text-yellow-800'
      case 'DONE': return 'bg-green-100 text-green-800'
      default: return 'bg-gray-100 text-gray-800'
    }
  }

  const getPriorityColor = (priority: string) => {
    switch (priority) {
      case 'HIGH': return 'bg-red-100 text-red-800'
      case 'MEDIUM': return 'bg-yellow-100 text-yellow-800'
      case 'LOW': return 'bg-green-100 text-green-800'
      default: return 'bg-gray-100 text-gray-800'
    }
  }

  return (
    <div className="max-w-5xl mx-auto space-y-6">
      {/* Issue Header */}
      <div className="card p-6">
        <div className="flex items-start justify-between mb-4">
          <div className="flex-1">
            <div className="flex items-center space-x-3 mb-2">
              <h1 className="text-2xl font-bold text-gray-900">{sampleIssue.title}</h1>
              <span className="text-sm text-gray-500">#{sampleIssue.id}</span>
            </div>
            <div className="flex items-center space-x-4">
              <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(sampleIssue.status)}`}>
                {sampleIssue.status.replace('_', ' ')}
              </span>
              <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getPriorityColor(sampleIssue.priority)}`}>
                {sampleIssue.priority}
              </span>
              <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-purple-100 text-purple-800">
                {sampleIssue.type}
              </span>
            </div>
          </div>
          <div className="flex space-x-2">
            <button className="btn btn-outline">편집</button>
            <button className="btn btn-primary">댓글 달기</button>
          </div>
        </div>

        {/* Issue Meta */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 text-sm">
          <div className="flex items-center space-x-2">
            <User className="h-4 w-4 text-gray-400" />
            <span className="text-gray-600">담당자:</span>
            <span className="font-medium">{sampleIssue.assignee}</span>
          </div>
          <div className="flex items-center space-x-2">
            <User className="h-4 w-4 text-gray-400" />
            <span className="text-gray-600">보고자:</span>
            <span className="font-medium">{sampleIssue.reporter}</span>
          </div>
          <div className="flex items-center space-x-2">
            <Clock className="h-4 w-4 text-gray-400" />
            <span className="text-gray-600">마감일:</span>
            <span className="font-medium">{sampleIssue.dueDate}</span>
          </div>
          <div className="flex items-center space-x-2">
            <Tag className="h-4 w-4 text-gray-400" />
            <span className="text-gray-600">라벨:</span>
            <div className="flex space-x-1">
              {sampleIssue.labels.map((label, index) => (
                <span key={index} className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-indigo-100 text-indigo-800">
                  {label}
                </span>
              ))}
            </div>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Main Content */}
        <div className="lg:col-span-2 space-y-6">
          {/* Description */}
          <div className="card p-6">
            <h2 className="text-lg font-medium text-gray-900 mb-4">설명</h2>
            <div className="prose prose-sm max-w-none">
              <pre className="whitespace-pre-wrap text-gray-700 font-sans">
                {sampleIssue.description}
              </pre>
            </div>
          </div>

          {/* Comments */}
          <div className="card p-6">
            <div className="flex items-center space-x-2 mb-4">
              <MessageSquare className="h-5 w-5 text-gray-400" />
              <h2 className="text-lg font-medium text-gray-900">댓글 ({sampleIssue.comments.length})</h2>
            </div>

            <div className="space-y-4">
              {sampleIssue.comments.map((comment) => (
                <div key={comment.id} className="border-l-2 border-gray-200 pl-4">
                  <div className="flex items-center space-x-2 mb-2">
                    <div className="w-6 h-6 bg-gray-200 rounded-full flex items-center justify-center">
                      <span className="text-xs font-medium">{comment.author.charAt(0)}</span>
                    </div>
                    <span className="font-medium text-sm">{comment.author}</span>
                    <span className="text-xs text-gray-500">{comment.createdAt}</span>
                  </div>
                  <p className="text-gray-700 text-sm">{comment.content}</p>
                </div>
              ))}

              {/* Add Comment Form */}
              <div className="border-t pt-4">
                <textarea
                  className="input w-full"
                  rows={3}
                  placeholder="댓글을 입력하세요..."
                />
                <div className="mt-2 flex justify-end">
                  <button className="btn btn-primary">댓글 추가</button>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Sidebar */}
        <div className="space-y-6">
          {/* Attachments */}
          <div className="card p-4">
            <div className="flex items-center space-x-2 mb-3">
              <Paperclip className="h-4 w-4 text-gray-400" />
              <h3 className="font-medium text-gray-900">첨부파일</h3>
            </div>
            <div className="space-y-2">
              {sampleIssue.attachments.map((attachment) => (
                <div key={attachment.id} className="flex items-center justify-between p-2 bg-gray-50 rounded">
                  <div>
                    <div className="text-sm font-medium text-gray-900">{attachment.name}</div>
                    <div className="text-xs text-gray-500">{attachment.size}</div>
                  </div>
                  <button className="text-indigo-600 hover:text-indigo-500 text-xs">
                    다운로드
                  </button>
                </div>
              ))}
              <button className="w-full p-2 border-2 border-dashed border-gray-300 rounded text-sm text-gray-500 hover:border-gray-400">
                + 파일 첨부
              </button>
            </div>
          </div>

          {/* Activity */}
          <div className="card p-4">
            <h3 className="font-medium text-gray-900 mb-3">활동 내역</h3>
            <div className="space-y-2 text-sm text-gray-600">
              <div>2024-01-16 14:30 - 김도현이 담당자로 설정됨</div>
              <div>2024-01-15 15:20 - 우선순위가 높음으로 변경됨</div>
              <div>2024-01-15 09:00 - 홍길동이 이슈를 생성함</div>
            </div>
          </div>

          {/* Quick Actions */}
          <div className="card p-4">
            <h3 className="font-medium text-gray-900 mb-3">빠른 작업</h3>
            <div className="space-y-2">
              <button className="btn btn-outline w-full text-sm">상태 변경</button>
              <button className="btn btn-outline w-full text-sm">담당자 변경</button>
              <button className="btn btn-outline w-full text-sm">하위 작업 생성</button>
              <button className="btn btn-outline w-full text-sm text-red-600 border-red-300 hover:bg-red-50">이슈 삭제</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default IssueDetailPage 