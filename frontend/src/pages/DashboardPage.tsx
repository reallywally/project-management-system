import React from 'react'
import { useAuthStore } from '@/stores/authStore'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Progress } from '@/components/ui/progress'
import { Avatar } from '@/components/ui/avatar'
import { Button } from '@/components/ui/button'
import { formatDate } from '@/lib/utils'

const DashboardPage: React.FC = () => {
  const { user } = useAuthStore()
  const currentDate = new Date()

  // Mock data for demonstration
  const stats = {
    myIssues: 15,
    inProgress: 5,
    completed: 8,
    projects: 4,
    dueTodayCount: 3,
    overdueCount: 2,
    needReviewCount: 2
  }

  const assignedIssues = [
    {
      id: 'PROJ-123',
      title: 'Implement user authentication system',
      priority: 'high' as const,
      status: 'Due tomorrow',
      type: 'feature'
    },
    {
      id: 'PROJ-124',
      title: 'Dashboard UI improvements',
      priority: 'medium' as const,
      status: 'In Review',
      type: 'enhancement'
    },
    {
      id: 'PROJ-125',
      title: 'API integration for project data',
      priority: 'medium' as const,
      status: 'In Progress',
      type: 'task'
    },
    {
      id: 'PROJ-126',
      title: 'Fix login redirect bug',
      priority: 'high' as const,
      status: 'New',
      type: 'bug'
    }
  ]

  const recentActivity = [
    {
      id: 1,
      user: 'John',
      action: 'commented on PROJ-45',
      time: '2 minutes ago'
    },
    {
      id: 2,
      action: 'Issue PROJ-44 moved to Done',
      time: '15 minutes ago'
    },
    {
      id: 3,
      user: 'Sarah',
      action: 'assigned PROJ-43 to you',
      time: '1 hour ago'
    },
    {
      id: 4,
      action: 'New comment on PROJ-42',
      time: '2 hours ago'
    }
  ]

  const upcomingDeadlines = [
    {
      period: 'Today',
      count: 2,
      issues: [
        { id: 'PROJ-123', title: 'Auth System', priority: 'high' as const },
        { id: 'PROJ-127', title: 'Testing', priority: 'medium' as const }
      ]
    },
    {
      period: 'Tomorrow',
      count: 1,
      issues: [
        { id: 'PROJ-128', title: 'Deployment', priority: 'medium' as const }
      ]
    },
    {
      period: 'This Week',
      count: 3,
      issues: [
        { id: 'PROJ-129', title: 'Documentation', priority: 'low' as const },
        { id: 'PROJ-130', title: 'Performance', priority: 'high' as const },
        { id: 'PROJ-131', title: 'Refactoring', priority: 'medium' as const }
      ]
    }
  ]

  const getPriorityIcon = (priority: 'high' | 'medium' | 'low') => {
    switch (priority) {
      case 'high': return '🔥'
      case 'medium': return '📊'
      case 'low': return '📝'
      default: return '📄'
    }
  }

  const getTypeIcon = (type: string) => {
    switch (type) {
      case 'bug': return '🐛'
      case 'feature': return '✨'
      case 'task': return '📋'
      case 'enhancement': return '⚡'
      default: return '📄'
    }
  }

  return (
    <div className="p-6 space-y-6">
      {/* Welcome Section */}
      <div className="bg-white rounded-lg border p-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold text-gray-900 flex items-center">
              👋 안녕하세요, {user?.nickname || user?.name}님!
            </h1>
            <p className="text-gray-600 mt-1">
              좋은 하루 되세요. 오늘도 화이팅!
            </p>
          </div>
          <div className="text-right text-sm text-gray-500">
            <div className="flex items-center space-x-4">
              <span>📅 {formatDate(currentDate)}</span>
              <span>☀️ 22°C</span>
            </div>
          </div>
        </div>
      </div>

      {/* Quick Stats Cards */}
      <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
        <Card>
          <CardContent className="p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <div className="w-8 h-8 bg-blue-500 rounded-md flex items-center justify-center">
                  <span className="text-white font-semibold">📋</span>
                </div>
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-500">내 이슈</p>
                <p className="text-2xl font-bold text-gray-900">{stats.myIssues}</p>
                <p className="text-xs text-gray-600">{stats.dueTodayCount} Due Today</p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <div className="w-8 h-8 bg-yellow-500 rounded-md flex items-center justify-center">
                  <span className="text-white font-semibold">⚡</span>
                </div>
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-500">진행 중</p>
                <p className="text-2xl font-bold text-gray-900">{stats.inProgress}</p>
                <p className="text-xs text-gray-600">{stats.overdueCount} Overdue</p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <div className="w-8 h-8 bg-green-500 rounded-md flex items-center justify-center">
                  <span className="text-white font-semibold">✅</span>
                </div>
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-500">완료</p>
                <p className="text-2xl font-bold text-gray-900">{stats.completed}</p>
                <p className="text-xs text-gray-600">This Week</p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <div className="w-8 h-8 bg-purple-500 rounded-md flex items-center justify-center">
                  <span className="text-white font-semibold">🎯</span>
                </div>
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-500">프로젝트</p>
                <p className="text-2xl font-bold text-gray-900">{stats.projects}</p>
                <p className="text-xs text-gray-600">{stats.needReviewCount} Need Review</p>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Main Content Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Assigned to Me */}
        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <CardTitle className="text-lg font-semibold">할당된 이슈</CardTitle>
              <Button variant="ghost" size="sm" className="text-blue-600 hover:text-blue-700">
                전체 보기 ({assignedIssues.length})
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {assignedIssues.slice(0, 4).map((issue) => (
                <div key={issue.id} className="flex items-center space-x-3 p-3 hover:bg-gray-50 rounded-lg cursor-pointer">
                  <div className="flex-shrink-0">
                    <span className="text-lg">{getPriorityIcon(issue.priority)}</span>
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium text-gray-900 truncate">
                      {getTypeIcon(issue.type)} {issue.title}
                    </p>
                    <p className="text-xs text-gray-500">{issue.id}</p>
                  </div>
                  <div className="flex-shrink-0">
                    <Badge 
                      variant={issue.status.includes('Due') ? 'destructive' : 'secondary'} 
                      className="text-xs"
                    >
                      {issue.status}
                    </Badge>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        {/* Progress Chart */}
        <Card>
          <CardHeader>
            <CardTitle className="text-lg font-semibold">📈 주간 진행률</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              <div className="text-center">
                <div className="text-3xl font-bold text-blue-600 mb-2">
                  {Math.round((stats.completed / (stats.completed + stats.inProgress + stats.myIssues - stats.completed - stats.inProgress)) * 100)}%
                </div>
                <p className="text-sm text-gray-600">전체 진행률</p>
              </div>
              
              <div className="space-y-3">
                <div>
                  <div className="flex justify-between text-sm mb-1">
                    <span>완료</span>
                    <span>{stats.completed}</span>
                  </div>
                  <Progress value={stats.completed} max={stats.myIssues} />
                </div>
                
                <div>
                  <div className="flex justify-between text-sm mb-1">
                    <span>진행 중</span>
                    <span>{stats.inProgress}</span>
                  </div>
                  <Progress value={stats.inProgress} max={stats.myIssues} />
                </div>
              </div>

              {/* Weekly Chart Placeholder */}
              <div className="mt-6">
                <div className="h-20 bg-gradient-to-r from-blue-100 to-blue-200 rounded-md flex items-end justify-center space-x-1 p-2">
                  {['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'].map((day, index) => (
                    <div key={day} className="flex flex-col items-center">
                      <div 
                        className="w-6 bg-blue-500 rounded-sm mb-1"
                        style={{ height: `${Math.random() * 40 + 10}px` }}
                      />
                      <span className="text-xs text-gray-600">{day}</span>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Bottom Section */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Recent Activity */}
        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <CardTitle className="text-lg font-semibold">최근 활동</CardTitle>
              <Button variant="ghost" size="sm" className="text-blue-600 hover:text-blue-700">
                📜 전체 활동 보기
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {recentActivity.map((activity) => (
                <div key={activity.id} className="flex items-start space-x-3">
                  <div className="flex-shrink-0 w-2 h-2 bg-blue-500 rounded-full mt-2" />
                  <div className="flex-1 min-w-0">
                    <p className="text-sm text-gray-900">
                      {activity.user && (
                        <span className="font-medium">{activity.user} </span>
                      )}
                      {activity.action}
                    </p>
                    <p className="text-xs text-gray-500">{activity.time}</p>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        {/* Upcoming Deadlines */}
        <Card>
          <CardHeader>
            <CardTitle className="text-lg font-semibold">다가오는 마감일</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {upcomingDeadlines.map((deadline, index) => (
                <div key={index}>
                  <div className="flex items-center justify-between mb-2">
                    <h4 className="text-sm font-medium text-gray-900">
                      📅 {deadline.period} ({deadline.count} issues)
                    </h4>
                  </div>
                  <div className="space-y-2">
                    {deadline.issues.slice(0, 2).map((issue) => (
                      <div key={issue.id} className="flex items-center space-x-2 text-sm">
                        <span>{getPriorityIcon(issue.priority)}</span>
                        <span className="text-gray-900">{issue.id} - {issue.title}</span>
                      </div>
                    ))}
                    {deadline.issues.length > 2 && (
                      <p className="text-xs text-gray-500 ml-6">
                        +{deadline.issues.length - 2} more...
                      </p>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}

export default DashboardPage 