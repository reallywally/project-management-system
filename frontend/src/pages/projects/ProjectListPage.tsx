import React, { useState } from 'react'
import { Link } from 'react-router-dom'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Progress } from '@/components/ui/progress'
import { Avatar } from '@/components/ui/avatar'
import { 
  Search, 
  Plus, 
  Filter, 
  SortDesc, 
  Users, 
  Calendar,
  ChevronDown,
  ChevronLeft,
  ChevronRight
} from 'lucide-react'
import { cn, getStatusColor } from '@/lib/utils'

interface Project {
  id: string
  name: string
  key: string
  description: string
  status: 'active' | 'planning' | 'on-hold' | 'completed'
  owner: {
    id: string
    name: string
    avatar?: string
  }
  memberCount: number
  completedIssues: number
  totalIssues: number
  createdAt: string
  updatedAt: string
}

const ProjectListPage: React.FC = () => {
  const [searchQuery, setSearchQuery] = useState('')
  const [statusFilter, setStatusFilter] = useState('all')
  const [ownerFilter, setOwnerFilter] = useState('all')
  const [sortBy, setSortBy] = useState('latest')
  const [currentPage, setCurrentPage] = useState(1)
  const itemsPerPage = 6

  // Mock data
  const projects: Project[] = [
    {
      id: '1',
      name: 'Project Alpha',
      key: 'ALPHA',
      description: 'Main web application development with React and Node.js backend integration',
      status: 'active',
      owner: { id: '1', name: 'John Doe', avatar: '/avatars/john.jpg' },
      memberCount: 5,
      completedIssues: 15,
      totalIssues: 20,
      createdAt: '2024-01-15',
      updatedAt: '2024-01-28'
    },
    {
      id: '2',
      name: 'Project Beta',
      key: 'BETA',
      description: 'Mobile application development using React Native',
      status: 'planning',
      owner: { id: '2', name: 'Jane Smith', avatar: '/avatars/jane.jpg' },
      memberCount: 3,
      completedIssues: 8,
      totalIssues: 12,
      createdAt: '2024-01-10',
      updatedAt: '2024-01-25'
    },
    {
      id: '3',
      name: 'Project Gamma',
      key: 'GAMMA',
      description: 'Data analytics and visualization dashboard',
      status: 'on-hold',
      owner: { id: '3', name: 'Bob Wilson', avatar: '/avatars/bob.jpg' },
      memberCount: 4,
      completedIssues: 5,
      totalIssues: 8,
      createdAt: '2024-01-05',
      updatedAt: '2024-01-20'
    },
    {
      id: '4',
      name: 'Project Delta',
      key: 'DELTA',
      description: 'API microservices architecture implementation',
      status: 'active',
      owner: { id: '4', name: 'Alice Brown', avatar: '/avatars/alice.jpg' },
      memberCount: 6,
      completedIssues: 22,
      totalIssues: 25,
      createdAt: '2024-01-01',
      updatedAt: '2024-01-27'
    },
    {
      id: '5',
      name: 'Project Epsilon',
      key: 'EPSI',
      description: 'DevOps and CI/CD pipeline setup',
      status: 'completed',
      owner: { id: '5', name: 'Charlie Davis', avatar: '/avatars/charlie.jpg' },
      memberCount: 2,
      completedIssues: 10,
      totalIssues: 10,
      createdAt: '2023-12-20',
      updatedAt: '2024-01-15'
    },
    {
      id: '6',
      name: 'Project Zeta',
      key: 'ZETA',
      description: 'Security audit and penetration testing',
      status: 'planning',
      owner: { id: '6', name: 'Diana Evans', avatar: '/avatars/diana.jpg' },
      memberCount: 3,
      completedIssues: 2,
      totalIssues: 15,
      createdAt: '2024-01-20',
      updatedAt: '2024-01-26'
    }
  ]

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'active': return '🟢'
      case 'planning': return '🟡'
      case 'on-hold': return '🔴'
      case 'completed': return '🔵'
      default: return '⚪'
    }
  }

  const getStatusText = (status: string) => {
    switch (status) {
      case 'active': return 'Active'
      case 'planning': return 'Planning'
      case 'on-hold': return 'On Hold'
      case 'completed': return 'Completed'
      default: return status
    }
  }

  // Filter and sort projects
  const filteredProjects = projects.filter(project => {
    const matchesSearch = project.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
                         project.description.toLowerCase().includes(searchQuery.toLowerCase())
    const matchesStatus = statusFilter === 'all' || project.status === statusFilter
    const matchesOwner = ownerFilter === 'all' || project.owner.id === ownerFilter
    
    return matchesSearch && matchesStatus && matchesOwner
  }).sort((a, b) => {
    switch (sortBy) {
      case 'name':
        return a.name.localeCompare(b.name)
      case 'progress':
        return (b.completedIssues / b.totalIssues) - (a.completedIssues / a.totalIssues)
      case 'members':
        return b.memberCount - a.memberCount
      case 'latest':
      default:
        return new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime()
    }
  })

  // Pagination
  const totalPages = Math.ceil(filteredProjects.length / itemsPerPage)
  const startIndex = (currentPage - 1) * itemsPerPage
  const paginatedProjects = filteredProjects.slice(startIndex, startIndex + itemsPerPage)

  return (
    <div className="p-6 space-y-6">
      {/* Page Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 flex items-center">
            📁 프로젝트 ({filteredProjects.length})
          </h1>
          <p className="text-gray-600 mt-1">팀의 모든 프로젝트를 관리하고 추적하세요</p>
        </div>
        <Button className="flex items-center space-x-2">
          <Plus className="h-4 w-4" />
          <span>새 프로젝트</span>
        </Button>
      </div>

      {/* Filter & Sort Bar */}
      <Card>
        <CardContent className="p-4">
          <div className="flex flex-col sm:flex-row gap-4 items-start sm:items-center justify-between">
            {/* Search */}
            <div className="relative flex-1 max-w-md">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
              <Input
                placeholder="프로젝트 검색..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-10"
              />
            </div>

            {/* Filters and Sort */}
            <div className="flex items-center space-x-4">
              {/* Status Filter */}
              <div className="relative">
                <select
                  value={statusFilter}
                  onChange={(e) => setStatusFilter(e.target.value)}
                  className="appearance-none bg-white border border-gray-300 rounded-md px-3 py-2 pr-8 text-sm focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
                >
                  <option value="all">📊 상태: 전체</option>
                  <option value="active">🟢 활성</option>
                  <option value="planning">🟡 계획</option>
                  <option value="on-hold">🔴 보류</option>
                  <option value="completed">🔵 완료</option>
                </select>
                <ChevronDown className="absolute right-2 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400 pointer-events-none" />
              </div>

              {/* Owner Filter */}
              <div className="relative">
                <select
                  value={ownerFilter}
                  onChange={(e) => setOwnerFilter(e.target.value)}
                  className="appearance-none bg-white border border-gray-300 rounded-md px-3 py-2 pr-8 text-sm focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
                >
                  <option value="all">👤 소유자: 전체</option>
                  <option value="1">John Doe</option>
                  <option value="2">Jane Smith</option>
                  <option value="3">Bob Wilson</option>
                  <option value="4">Alice Brown</option>
                </select>
                <ChevronDown className="absolute right-2 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400 pointer-events-none" />
              </div>

              {/* Sort */}
              <div className="relative">
                <select
                  value={sortBy}
                  onChange={(e) => setSortBy(e.target.value)}
                  className="appearance-none bg-white border border-gray-300 rounded-md px-3 py-2 pr-8 text-sm focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
                >
                  <option value="latest">📅 정렬: 최신순</option>
                  <option value="name">🔤 이름순</option>
                  <option value="progress">📈 진행률순</option>
                  <option value="members">👥 멤버수순</option>
                </select>
                <ChevronDown className="absolute right-2 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400 pointer-events-none" />
              </div>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Project Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {paginatedProjects.map((project) => (
          <Card key={project.id} className="hover:shadow-md transition-shadow cursor-pointer">
            <CardHeader>
              <div className="flex items-start justify-between">
                <div>
                  <CardTitle className="text-lg font-semibold text-gray-900">
                    {project.name}
                  </CardTitle>
                  <p className="text-sm text-gray-500 mt-1">{project.key}</p>
                </div>
                <Badge className={cn("text-xs", getStatusColor(project.status))}>
                  {getStatusIcon(project.status)} {getStatusText(project.status)}
                </Badge>
              </div>
            </CardHeader>

            <CardContent>
              {/* Description */}
              <p className="text-gray-600 text-sm mb-4 line-clamp-2">
                {project.description}
              </p>

              {/* Progress */}
              <div className="mb-4">
                <div className="flex justify-between text-sm mb-1">
                  <span>진행률</span>
                  <span>{project.completedIssues}/{project.totalIssues}</span>
                </div>
                <Progress 
                  value={project.completedIssues} 
                  max={project.totalIssues}
                  className="h-2"
                />
                <div className="text-xs text-gray-500 mt-1">
                  {Math.round((project.completedIssues / project.totalIssues) * 100)}% 완료
                </div>
              </div>

              {/* Footer */}
              <div className="flex items-center justify-between">
                <div className="flex items-center space-x-2">
                  <Avatar 
                    name={project.owner.name}
                    src={project.owner.avatar}
                    size="sm" 
                  />
                  <span className="text-sm text-gray-600">
                    {project.owner.name}
                  </span>
                </div>
                <div className="flex items-center text-sm text-gray-500">
                  <Users className="h-4 w-4 mr-1" />
                  {project.memberCount}
                </div>
              </div>

              {/* Action Button */}
              <div className="mt-4 pt-4 border-t">
                <Link to={`/projects/${project.id}`}>
                  <Button variant="outline" size="sm" className="w-full">
                    프로젝트 보기
                  </Button>
                </Link>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      {/* Empty State */}
      {paginatedProjects.length === 0 && (
        <div className="text-center py-12">
          <div className="text-6xl mb-4">📁</div>
          <h3 className="text-lg font-medium text-gray-900 mb-2">
            프로젝트가 없습니다
          </h3>
          <p className="text-gray-600 mb-4">
            검색 조건을 변경하거나 새 프로젝트를 생성해보세요.
          </p>
          <Button>
            <Plus className="h-4 w-4 mr-2" />
            새 프로젝트 만들기
          </Button>
        </div>
      )}

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="flex items-center justify-center space-x-2">
          <Button
            variant="outline"
            size="sm"
            onClick={() => setCurrentPage(Math.max(1, currentPage - 1))}
            disabled={currentPage === 1}
          >
            <ChevronLeft className="h-4 w-4 mr-1" />
            이전
          </Button>

          <div className="flex items-center space-x-1">
            {Array.from({ length: totalPages }, (_, i) => i + 1).map((page) => (
              <button
                key={page}
                onClick={() => setCurrentPage(page)}
                className={cn(
                  "px-3 py-1 text-sm rounded-md",
                  currentPage === page
                    ? "bg-primary text-primary-foreground"
                    : "text-gray-600 hover:bg-gray-100"
                )}
              >
                {page}
              </button>
            ))}
          </div>

          <Button
            variant="outline"
            size="sm"
            onClick={() => setCurrentPage(Math.min(totalPages, currentPage + 1))}
            disabled={currentPage === totalPages}
          >
            다음
            <ChevronRight className="h-4 w-4 ml-1" />
          </Button>
        </div>
      )}
    </div>
  )
}

export default ProjectListPage