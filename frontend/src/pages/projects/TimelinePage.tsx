import React, { useState, useMemo, useCallback } from 'react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Card, CardContent } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Avatar } from '@/components/ui/avatar'
import IssueDetailModal from '@/components/IssueDetailModal'
import { 
  Plus, 
  Search,
  Calendar,
  ChevronLeft,
  ChevronRight,
  Grid3X3,
  BarChart3,
  Share,
  Download
} from 'lucide-react'
import { cn } from '@/lib/utils'

interface Epic {
  id: string
  key: string
  title: string
  status: 'planning' | 'in-progress' | 'done'
  startDate: string
  endDate: string
  assignee?: {
    id: string
    name: string
    avatar?: string
  }
  progress: number
  color: string
  issues: Issue[]
}

interface Issue {
  id: string
  key: string
  title: string
  type: 'story' | 'task' | 'bug' | 'epic'
  priority: 'high' | 'medium' | 'low'
  status: 'todo' | 'in-progress' | 'review' | 'done'
  startDate?: string
  endDate?: string
  assignee?: {
    id: string
    name: string
    avatar?: string
  }
  storyPoints?: number
  progress?: number
  description?: string
}

interface TooltipData {
  x: number
  y: number
  issue: Issue | Epic
  type: 'issue' | 'epic'
}

const TimelinePage: React.FC = () => {
  const [viewType, setViewType] = useState<'timeline' | 'kanban'>('timeline')
  const [searchQuery, setSearchQuery] = useState('')
  const [selectedTimeRange, setSelectedTimeRange] = useState<'day' | 'week' | 'month'>('week')
  const [currentDate, setCurrentDate] = useState(new Date())
  const [tooltip, setTooltip] = useState<TooltipData | null>(null)
  const [draggedItem, setDraggedItem] = useState<{id: string, type: 'issue' | 'epic', originalStart: string, originalEnd: string} | null>(null)
  const [statusFilter, setStatusFilter] = useState<string>('all')
  const [assigneeFilter, setAssigneeFilter] = useState<string>('all')
  const [groupBy, setGroupBy] = useState<'none' | 'assignee' | 'status'>('none')
  
  // Timeline drag state for date navigation
  const [timelineDrag, setTimelineDrag] = useState<{
    isDragging: boolean
    startX: number
    startDate: Date
  }>({
    isDragging: false,
    startX: 0,
    startDate: new Date()
  })

  // Modal state for issue details
  const [selectedIssue, setSelectedIssue] = useState<Issue | null>(null)
  const [isModalOpen, setIsModalOpen] = useState(false)

  // Enhanced mock data with progress for issues
  const [epics, setEpics] = useState<Epic[]>([
    {
      id: '1',
      key: 'PROJ-100',
      title: 'User Authentication System',
      status: 'in-progress',
      startDate: '2025-07-01',
      endDate: '2025-07-28',
      assignee: { id: '1', name: 'John Doe' },
      progress: 65,
      color: '#3b82f6',
      issues: [
        {
          id: '1',
          key: 'PROJ-101',
          title: 'Login page design',
          type: 'story',
          priority: 'high',
          status: 'done',
          startDate: '2025-07-01',
          endDate: '2025-07-05',
          assignee: { id: '2', name: 'Jane Smith' },
          storyPoints: 5,
          progress: 100,
          description: 'Design and implement the login page UI'
        },
        {
          id: '2',
          key: 'PROJ-102',
          title: 'JWT implementation',
          type: 'task',
          priority: 'high',
          status: 'in-progress',
          startDate: '2025-07-06',
          endDate: '2025-07-15',
          assignee: { id: '1', name: 'John Doe' },
          storyPoints: 8,
          progress: 70,
          description: 'Implement JWT token authentication system'
        },
        {
          id: '3',
          key: 'PROJ-103',
          title: 'Password reset flow',
          type: 'story',
          priority: 'medium',
          status: 'todo',
          startDate: '2025-07-16',
          endDate: '2025-07-25',
          assignee: { id: '3', name: 'Bob Wilson' },
          storyPoints: 3,
          progress: 0,
          description: 'Create password reset functionality'
        }
      ]
    },
    {
      id: '2',
      key: 'PROJ-200', 
      title: 'Dashboard & Analytics',
      status: 'planning',
      startDate: '2025-07-01',
      endDate: '2025-07-31',
      assignee: { id: '4', name: 'Alice Brown' },
      progress: 20,
      color: '#10b981',
      issues: [
        {
          id: '4',
          key: 'PROJ-201',
          title: 'Dashboard layout',
          type: 'story',
          priority: 'high',
          status: 'todo',
          startDate: '2025-07-01',
          endDate: '2025-07-10',
          assignee: { id: '4', name: 'Alice Brown' },
          storyPoints: 5,
          progress: 30,
          description: 'Create responsive dashboard layout'
        },
        {
          id: '5',
          key: 'PROJ-202',
          title: 'Chart components',
          type: 'task',
          priority: 'medium',
          status: 'todo',
          startDate: '2025-07-11',
          endDate: '2025-07-20',
          assignee: { id: '5', name: 'Charlie Davis' },
          storyPoints: 8,
          progress: 10,
          description: 'Implement chart visualization components'
        }
      ]
    }
  ])

  // Enhanced timeline date generation with different zoom levels
  const getTimelineDates = useCallback(() => {
    const dates = []
    const start = new Date(currentDate)

    if (selectedTimeRange === 'day') {
      // Start from the exact current date
      start.setHours(0, 0, 0, 0) 
      for (let i = 0; i < 14; i++) { // 2 weeks in days
        const date = new Date(start)
        date.setDate(start.getDate() + i)
        dates.push(date)
      }
    } else if (selectedTimeRange === 'week') {
      // Start from the beginning of the current week (Monday)
      const dayOfWeek = start.getDay()
      const mondayOffset = dayOfWeek === 0 ? -6 : 1 - dayOfWeek // Make Monday the start
      start.setDate(start.getDate() + mondayOffset)
      start.setHours(0, 0, 0, 0)
      
      for (let i = 0; i < 12; i++) { // 12 weeks
        const date = new Date(start)
        date.setDate(start.getDate() + (i * 7))
        dates.push(date)
      }
    } else { // month
      // Start from the first day of the current month
      start.setDate(1)
      start.setHours(0, 0, 0, 0)
      
      for (let i = 0; i < 6; i++) { // 6 months
        const date = new Date(start)
        date.setMonth(start.getMonth() + i)
        dates.push(date)
      }
    }
    return dates
  }, [currentDate, selectedTimeRange])

  const timelineDates = useMemo(() => getTimelineDates(), [getTimelineDates])

  const getDatePosition = useCallback((date: string) => {
    const targetDate = new Date(date)
    const startDate = timelineDates[0]
    const endDate = timelineDates[timelineDates.length - 1]
    
    const totalDuration = endDate.getTime() - startDate.getTime()
    const targetDuration = targetDate.getTime() - startDate.getTime()
    
    return Math.max(0, Math.min(100, (targetDuration / totalDuration) * 100))
  }, [timelineDates])

  // Enhanced date position with column constraints
  const getConstrainedDatePosition = useCallback((startDate: string, endDate: string) => {
    const start = new Date(startDate)
    const end = new Date(endDate)
    
    const timelineStart = timelineDates[0]
    const timelineEnd = timelineDates[timelineDates.length - 1]
    
    // Find which column the start and end dates belong to
    const columnWidth = 100 / timelineDates.length
    
    const startPosition = getDatePosition(startDate)
    const endPosition = getDatePosition(endDate)
    
    // Calculate which columns are involved
    const startColumnIndex = Math.floor(startPosition / columnWidth)
    const endColumnIndex = Math.floor(endPosition / columnWidth)
    
    // If the bar spans multiple columns, we need to create separate segments
    const segments = []
    
    for (let i = startColumnIndex; i <= Math.min(endColumnIndex, timelineDates.length - 1); i++) {
      const columnStart = i * columnWidth
      const columnEnd = (i + 1) * columnWidth
      
      const segmentStart = Math.max(startPosition, columnStart)
      const segmentEnd = Math.min(endPosition, columnEnd)
      
      if (segmentEnd > segmentStart) {
        segments.push({
          left: segmentStart,
          width: segmentEnd - segmentStart,
          columnIndex: i
        })
      }
    }
    
    return segments
  }, [timelineDates, getDatePosition])

  // Enhanced navigation with different zoom levels
  const navigateTime = (direction: 'prev' | 'next') => {
    const newDate = new Date(currentDate)

    if (selectedTimeRange === 'day') {
      newDate.setDate(newDate.getDate() + (direction === 'next' ? 7 : -7)) // Move by 1 week
    } else if (selectedTimeRange === 'week') {
      newDate.setDate(newDate.getDate() + (direction === 'next' ? 84 : -84)) // Move by 12 weeks
    } else {
      newDate.setMonth(newDate.getMonth() + (direction === 'next' ? 3 : -3)) // Move by 3 months
    }
    setCurrentDate(newDate)
  }

  // Drag and drop handlers
  const handleDragStart = (e: React.DragEvent, item: Issue | Epic, type: 'issue' | 'epic') => {
    setDraggedItem({
      id: item.id,
      type,
      originalStart: item.startDate!,
      originalEnd: item.endDate
    })
    e.dataTransfer.effectAllowed = 'move'
  }

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault()
    e.dataTransfer.dropEffect = 'move'
  }

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault()
    if (!draggedItem) return

    const rect = e.currentTarget.getBoundingClientRect()
    const x = e.clientX - rect.left
    const percentage = (x / rect.width) * 100
    
    // Calculate new date based on position
    const startDate = timelineDates[0]
    const endDate = timelineDates[timelineDates.length - 1]
    const totalDuration = endDate.getTime() - startDate.getTime()
    const newStartTime = startDate.getTime() + (totalDuration * percentage / 100)
    const newStartDate = new Date(newStartTime)
    
    // Calculate duration and new end date
    const originalDuration = new Date(draggedItem.originalEnd).getTime() - new Date(draggedItem.originalStart).getTime()
    const newEndDate = new Date(newStartTime + originalDuration)

    // Update the data
    setEpics(prevEpics => {
      return prevEpics.map(epic => {
        if (draggedItem.type === 'epic' && epic.id === draggedItem.id) {
          return {
            ...epic,
            startDate: newStartDate.toISOString().split('T')[0],
            endDate: newEndDate.toISOString().split('T')[0]
          }
        } else {
          return {
            ...epic,
            issues: epic.issues.map(issue => {
              if (draggedItem.type === 'issue' && issue.id === draggedItem.id) {
                return {
                  ...issue,
                  startDate: newStartDate.toISOString().split('T')[0],
                  endDate: newEndDate.toISOString().split('T')[0]
                }
              }
              return issue
            })
          }
        }
      })
    })

    setDraggedItem(null)
    console.log('Date updated:', {
      id: draggedItem.id,
      type: draggedItem.type,
      newStartDate: newStartDate.toISOString().split('T')[0],
      newEndDate: newEndDate.toISOString().split('T')[0]
    })
  }

  // Timeline drag handlers for date navigation
  const handleTimelineMouseDown = (e: React.MouseEvent) => {
    // Only start timeline drag if not clicking on an issue/epic bar
    const target = e.target as HTMLElement
    if (target.closest('[draggable="true"]')) return
    
    setTimelineDrag({
      isDragging: true,
      startX: e.clientX,
      startDate: new Date(currentDate)
    })
  }

  const handleTimelineMouseMove = (e: React.MouseEvent) => {
    if (!timelineDrag.isDragging) return
    
    const deltaX = e.clientX - timelineDrag.startX
    const timelineWidth = 800 // Approximate timeline width
    const sensitivity = 0.5 // Adjust sensitivity
    
    // Calculate time offset based on current time range
    let timeOffset = 0
    if (selectedTimeRange === 'day') {
      timeOffset = (deltaX / timelineWidth) * 14 * 24 * 60 * 60 * 1000 * sensitivity // 14 days
    } else if (selectedTimeRange === 'week') {
      timeOffset = (deltaX / timelineWidth) * 12 * 7 * 24 * 60 * 60 * 1000 * sensitivity // 12 weeks
    } else {
      timeOffset = (deltaX / timelineWidth) * 6 * 30 * 24 * 60 * 60 * 1000 * sensitivity // 6 months
    }
    
    const newDate = new Date(timelineDrag.startDate.getTime() - timeOffset)
    setCurrentDate(newDate)
  }

  const handleTimelineMouseUp = () => {
    setTimelineDrag({
      isDragging: false,
      startX: 0,
      startDate: new Date()
    })
  }

  const handleTimelineMouseLeave = () => {
    setTimelineDrag({
      isDragging: false,
      startX: 0,
      startDate: new Date()
    })
  }

  // Tooltip handlers
  const handleMouseEnter = (e: React.MouseEvent, item: Issue | Epic, type: 'issue' | 'epic') => {
    const rect = e.currentTarget.getBoundingClientRect()
    setTooltip({
      x: rect.left + rect.width / 2,
      y: rect.top - 10,
      issue: item,
      type
    })
  }

  const handleMouseLeave = () => {
    setTooltip(null)
  }

  // Issue click handler
  const handleIssueClick = (issue: Issue) => {
    // Convert timeline issue to modal-compatible issue
    const modalIssue = {
      ...issue,
      issueNumber: issue.key,
      description: issue.description || 'No description available',
      dueDate: issue.endDate,
      labels: [],
      subtasks: [],
      attachments: [],
      comments: [],
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    }
    setSelectedIssue(modalIssue)
    setIsModalOpen(true)
  }

  // Modal handlers
  const handleModalClose = () => {
    setIsModalOpen(false)
    setSelectedIssue(null)
  }

  const handleIssueUpdate = (updatedIssue: any) => {
    setEpics(prevEpics => {
      return prevEpics.map(epic => ({
        ...epic,
        issues: epic.issues.map(issue => 
          issue.id === updatedIssue.id ? { ...issue, ...updatedIssue } : issue
        )
      }))
    })
  }

  // Filter and group functions
  const getFilteredEpics = () => {
    return epics.filter(epic => {
      if (statusFilter !== 'all' && epic.status !== statusFilter) return false
      if (assigneeFilter !== 'all' && epic.assignee?.id !== assigneeFilter) return false
      if (searchQuery && !epic.title.toLowerCase().includes(searchQuery.toLowerCase())) return false
      return true
    }).map(epic => ({
      ...epic,
      issues: epic.issues.filter(issue => {
        if (statusFilter !== 'all' && issue.status !== statusFilter) return false
        if (assigneeFilter !== 'all' && issue.assignee?.id !== assigneeFilter) return false
        if (searchQuery && !issue.title.toLowerCase().includes(searchQuery.toLowerCase())) return false
        return true
      })
    }))
  }

  // Check if item should be visible in timeline (for right panel only)
  const isInTimelineRange = (startDate: string | undefined, endDate: string | undefined) => {
    if (!startDate || !endDate) return false
    const timelineStart = timelineDates[0]
    const timelineEnd = timelineDates[timelineDates.length - 1]
    const itemStart = new Date(startDate)
    const itemEnd = new Date(endDate)
    return !(itemEnd < timelineStart || itemStart > timelineEnd)
  }

  const filteredEpics = getFilteredEpics()

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'planning': return 'bg-yellow-500'
      case 'in-progress': return 'bg-blue-500'
      case 'done': return 'bg-green-500'
      case 'todo': return 'bg-gray-500'
      case 'review': return 'bg-purple-500'
      default: return 'bg-gray-500'
    }
  }

  const getPriorityColor = (priority: string) => {
    switch (priority) {
      case 'high': return 'text-red-600'
      case 'medium': return 'text-yellow-600' 
      case 'low': return 'text-green-600'
      default: return 'text-gray-600'
    }
  }

  // Responsive timeline width calculation
  const getTimelineWidth = useCallback(() => {
    const baseWidth = 800 // Minimum width
    const columnWidth = selectedTimeRange === 'day' ? 80 : selectedTimeRange === 'week' ? 100 : 120
    return Math.max(baseWidth, timelineDates.length * columnWidth)
  }, [timelineDates.length, selectedTimeRange])

  const timelineWidth = useMemo(() => getTimelineWidth(), [getTimelineWidth])

  return (
    <div className="p-6 h-full flex flex-col bg-gray-50">
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 flex items-center">
            <Calendar className="h-7 w-7 mr-3 text-primary" />
            Project Timeline
          </h1>
          <p className="text-gray-600 mt-1">Track and manage project progress over time</p>
        </div>
        
        <div className="flex items-center space-x-3">
          {/* View Toggle */}
          <div className="flex bg-gray-200 rounded-lg p-1">
            <Button
              variant={viewType === 'timeline' ? 'default' : 'ghost'}
              size="sm"
              onClick={() => setViewType('timeline')}
              className="h-8"
            >
              <BarChart3 className="h-4 w-4 mr-2" />
              Timeline
            </Button>
            <Button
              variant={viewType === 'kanban' ? 'default' : 'ghost'}
              size="sm"
              onClick={() => setViewType('kanban')}
              className="h-8"
            >
              <Grid3X3 className="h-4 w-4 mr-2" />
              Kanban
            </Button>
          </div>
          
          <Button size="sm" variant="outline">
            <Plus className="h-4 w-4 mr-2" />
            Create Epic
          </Button>
          
          <Button size="sm" variant="outline">
            <Share className="h-4 w-4 mr-2" />
            Share
          </Button>
          
          <Button size="sm" variant="outline">
            <Download className="h-4 w-4 mr-2" />
            Export
          </Button>
        </div>
      </div>

      {/* Enhanced Filters and Controls */}
      <Card className="mb-6">
        <CardContent className="p-4">
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center space-x-4">
              <div className="relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
                <Input
                  placeholder="Search epics and issues..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="pl-10 w-80"
                />
              </div>
              
              <select
                value={statusFilter}
                onChange={(e) => setStatusFilter(e.target.value)}
                className="px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary"
              >
                <option value="all">All Status</option>
                <option value="planning">Planning</option>
                <option value="todo">To Do</option>
                <option value="in-progress">In Progress</option>
                <option value="review">Review</option>
                <option value="done">Done</option>
              </select>
              
              <select
                value={assigneeFilter}
                onChange={(e) => setAssigneeFilter(e.target.value)}
                className="px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary"
              >
                <option value="all">All Assignees</option>
                <option value="1">John Doe</option>
                <option value="2">Jane Smith</option>
                <option value="3">Bob Wilson</option>
                <option value="4">Alice Brown</option>
                <option value="5">Charlie Davis</option>
              </select>
              
              <select
                value={groupBy}
                onChange={(e) => setGroupBy(e.target.value as 'none' | 'assignee' | 'status')}
                className="px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary"
              >
                <option value="none">No Grouping</option>
                <option value="assignee">Group by Assignee</option>
                <option value="status">Group by Status</option>
              </select>
            </div>
            
            <div className="flex items-center space-x-3">
              {/* Enhanced Time Range Selector */}
              <div className="flex bg-gray-100 rounded-lg p-1">
                {(['day', 'week', 'month'] as const).map((range) => (
                  <Button
                    key={range}
                    variant={selectedTimeRange === range ? 'default' : 'ghost'}
                    size="sm"
                    onClick={() => setSelectedTimeRange(range)}
                    className="h-8 px-3 text-xs"
                  >
                    {range === 'day' ? 'Day' : range === 'week' ? 'Week' : 'Month'}
                  </Button>
                ))}
              </div>
              
              {/* Date Navigation */}
              <div className="flex items-center space-x-2">
                <Button variant="outline" size="sm" onClick={() => navigateTime('prev')}>
                  <ChevronLeft className="h-4 w-4" />
                </Button>
                <span className="text-sm font-medium min-w-32 text-center">
                  {selectedTimeRange === 'day' 
                    ? currentDate.toLocaleDateString('ko-KR', { month: 'short', day: 'numeric' })
                    : selectedTimeRange === 'week'
                    ? `W${Math.ceil(currentDate.getDate() / 7)} ${currentDate.toLocaleDateString('ko-KR', { month: 'short' })}`
                    : currentDate.toLocaleDateString('ko-KR', { year: 'numeric', month: 'long' })
                  }
                </span>
                <Button variant="outline" size="sm" onClick={() => navigateTime('next')}>
                  <ChevronRight className="h-4 w-4" />
                </Button>
              </div>
              
              <div className="text-xs text-gray-500 bg-gray-100 px-2 py-1 rounded">
                💡 Drag timeline to navigate
              </div>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Timeline View */}
      {viewType === 'timeline' && (
        <div className="flex-1 flex flex-col overflow-hidden bg-white rounded-lg border">
          {/* Timeline Container */}
          <div className="flex-1 flex overflow-hidden">
            {/* Left Panel - Fixed Issue List */}
            <div className="w-80 flex-shrink-0 flex flex-col border-r border-gray-200 bg-gray-50">
              {/* Left Header */}
              <div className="h-16 px-4 py-3 border-b border-gray-200 bg-white flex items-center">
                <h3 className="font-semibold text-gray-900">Epics & Issues</h3>
              </div>

              {/* Left Body - Scrollable */}
              <div className="flex-1 overflow-y-auto">
                <div className="p-2">
                  {filteredEpics.map((epic) => (
                    <div key={epic.id} className="mb-1">
                      {/* Epic Row - Fixed Height */}
                      <div className="h-12 flex items-center p-2 bg-white rounded border mb-1 hover:shadow-sm transition-shadow">
                        <div className="flex-1 min-w-0">
                          <div className="flex items-center mb-1">
                            <div
                              className="w-3 h-3 rounded-full mr-2 flex-shrink-0"
                              style={{ backgroundColor: epic.color }}
                            />
                            <span className="text-xs text-gray-500 font-mono">
                              {epic.key}
                            </span>
                          </div>
                          <h4 className="font-medium text-gray-900 text-sm truncate">
                            {epic.title}
                          </h4>
                        </div>
                        <div className="ml-2 flex items-center space-x-1 flex-shrink-0">
                          <Badge variant="secondary" className="text-xs">
                            {epic.status}
                          </Badge>
                          {epic.assignee && (
                            <Avatar name={epic.assignee.name} size="xs" />
                          )}
                        </div>
                      </div>

                      {/* Child Issues - Fixed Height */}
                      <div className="ml-6 space-y-1">
                        {epic.issues.map((issue) => (
                          <div
                            key={issue.id}
                            className="h-8 flex items-center p-2 bg-gray-50 rounded border hover:bg-white transition-colors cursor-pointer"
                            onClick={() => handleIssueClick(issue)}
                          >
                            <div className="flex-1 min-w-0">
                              <div className="flex items-center">
                                <div className="w-2 h-2 bg-gray-400 rounded-full mr-2 flex-shrink-0" />
                                <span className="text-xs text-gray-500 font-mono mr-2">
                                  {issue.key}
                                </span>
                                <span className="text-sm text-gray-700 truncate">
                                  {issue.title}
                                </span>
                              </div>
                            </div>
                            <div className="ml-2 flex items-center space-x-1 flex-shrink-0">
                              <Badge variant="outline" className="text-xs">
                                {issue.type}
                              </Badge>
                              {issue.assignee && (
                                <Avatar name={issue.assignee.name} size="xs" />
                              )}
                              {issue.storyPoints && (
                                <span className="text-xs text-gray-500">
                                  {issue.storyPoints}pt
                                </span>
                              )}
                            </div>
                          </div>
                        ))}
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>

            {/* Right Panel - Timeline Chart */}
            <div className="flex-1 flex flex-col overflow-hidden">
              {/* Timeline Header - Fixed */}
              <div className="h-16 border-b border-gray-200 bg-white flex-shrink-0 overflow-x-auto" 
                   onScroll={(e) => {
                     const target = e.target as HTMLDivElement
                     const timelineBody = document.getElementById('timeline-body')
                     if (timelineBody) {
                       timelineBody.scrollLeft = target.scrollLeft
                     }
                   }}>
                <div className="flex h-full" style={{ minWidth: `${timelineWidth}px` }}>
                  {timelineDates.map((date, index) => (
                    <div 
                      key={index} 
                      className="flex-1 border-r border-gray-200 last:border-r-0 flex flex-col justify-center items-center bg-gray-50"
                      style={{ 
                        minWidth: selectedTimeRange === 'day' ? '80px' : selectedTimeRange === 'week' ? '100px' : '120px'
                      }}
                    >
                      <div className="text-xs text-gray-500 mb-1">
                        {selectedTimeRange === 'day' 
                          ? date.toLocaleDateString('ko-KR', { year: '2-digit', month: 'short', day: 'numeric' })
                          : selectedTimeRange === 'week'
                          ? `${date.toLocaleDateString('ko-KR', { year: 'numeric' })} W${Math.ceil(date.getDate() / 7)}`
                          : date.getFullYear()
                        }
                      </div>
                      <div className="text-sm font-medium">
                        {selectedTimeRange === 'day' 
                          ? date.toLocaleDateString('ko-KR', { weekday: 'short' })
                          : selectedTimeRange === 'week'
                          ? date.toLocaleDateString('ko-KR', { month: 'short', day: 'numeric' })
                          : date.toLocaleDateString('ko-KR', { month: 'short' })
                        }
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              {/* Timeline Body - Scrollable */}
              <div 
                id="timeline-body"
                className={cn(
                  "flex-1 overflow-auto relative select-none",
                  timelineDrag.isDragging ? "cursor-grabbing" : "cursor-grab"
                )}
                onScroll={(e) => {
                  const target = e.target as HTMLDivElement
                  const timelineHeader = document.querySelector('.h-16.border-b.border-gray-200.bg-white.flex-shrink-0.overflow-x-auto') as HTMLDivElement
                  if (timelineHeader) {
                    timelineHeader.scrollLeft = target.scrollLeft
                  }
                }}
                onMouseDown={handleTimelineMouseDown}
                onMouseMove={handleTimelineMouseMove}
                onMouseUp={handleTimelineMouseUp}
                onMouseLeave={handleTimelineMouseLeave}
              >
                {/* Background Grid */}
                <div className="absolute inset-0" style={{ minWidth: `${timelineWidth}px` }}>
                  <div className="flex h-full">
                    {timelineDates.map((_, index) => (
                      <div 
                        key={index} 
                        className="flex-1 border-r border-gray-200 last:border-r-0 bg-white"
                        style={{ 
                          minWidth: selectedTimeRange === 'day' ? '80px' : selectedTimeRange === 'week' ? '100px' : '120px'
                        }}
                      />
                    ))}
                  </div>
                </div>

                {/* Timeline Content */}
                <div className="relative z-10 p-2" style={{ minWidth: `${timelineWidth}px` }}>
                  {filteredEpics.map((epic) => (
                    <div key={epic.id} className="mb-1">
                      {/* Epic Timeline Row - Fixed Height */}
                      <div className="h-12 relative mb-1">
                        {isInTimelineRange(epic.startDate, epic.endDate) && (
                          <EpicTimelineBar 
                            epic={epic} 
                            getDatePosition={getDatePosition}
                            getConstrainedDatePosition={getConstrainedDatePosition}
                            onDragStart={handleDragStart}
                            onMouseEnter={handleMouseEnter}
                            onMouseLeave={handleMouseLeave}
                          />
                        )}
                      </div>

                      {/* Issue Timeline Rows - Fixed Height */}
                      <div className="ml-6 space-y-1">
                        {epic.issues.map((issue) => (
                          <div key={issue.id} className="h-8 relative">
                            {isInTimelineRange(issue.startDate, issue.endDate) && (
                              <IssueTimelineBar 
                                issue={issue} 
                                getDatePosition={getDatePosition}
                                getConstrainedDatePosition={getConstrainedDatePosition}
                                onDragStart={handleDragStart}
                                onMouseEnter={handleMouseEnter}
                                onMouseLeave={handleMouseLeave}
                                onClick={handleIssueClick}
                              />
                            )}
                          </div>
                        ))}
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Kanban View Placeholder */}
      {viewType === 'kanban' && (
        <Card className="flex-1 flex items-center justify-center">
          <div className="text-center">
            <Grid3X3 className="h-16 w-16 text-gray-300 mx-auto mb-4" />
            <h3 className="text-lg font-semibold text-gray-600 mb-2">
              Kanban View
            </h3>
            <p className="text-gray-500">
              Switch to existing kanban board or implement kanban view here
            </p>
          </div>
        </Card>
      )}

      {/* Tooltip */}
      {tooltip && (
        <Tooltip
          x={tooltip.x}
          y={tooltip.y}
          issue={tooltip.issue}
          type={tooltip.type}
        />
      )}

      {/* Issue Detail Modal */}
      {selectedIssue && (
        <IssueDetailModal
          isOpen={isModalOpen}
          onClose={handleModalClose}
          issue={selectedIssue}
          onUpdate={handleIssueUpdate}
        />
      )}
    </div>
  )
}

// Enhanced Epic Timeline Bar Component with Column Constraints
interface EpicTimelineBarProps {
  epic: Epic
  getDatePosition: (date: string) => number
  getConstrainedDatePosition: (startDate: string, endDate: string) => Array<{left: number, width: number, columnIndex: number}>
  onDragStart: (e: React.DragEvent, item: Epic, type: 'epic') => void
  onMouseEnter: (e: React.MouseEvent, item: Epic, type: 'epic') => void
  onMouseLeave: () => void
}

const EpicTimelineBar: React.FC<EpicTimelineBarProps> = ({ 
  epic, 
  getDatePosition,
  getConstrainedDatePosition,
  onDragStart,
  onMouseEnter,
  onMouseLeave
}) => {
  const segments = getConstrainedDatePosition(epic.startDate, epic.endDate)

  return (
    <>
      {segments.map((segment, index) => (
        <div
          key={index}
          className="absolute inset-y-1 rounded-sm shadow-sm cursor-move hover:shadow-md transition-all border-2 border-white hover:border-gray-300 flex items-center"
          style={{
            left: `${segment.left}%`,
            width: `${segment.width}%`,
            backgroundColor: epic.color,
            height: '40px'
          }}
          draggable
          onDragStart={(e) => onDragStart(e, epic, 'epic')}
          onMouseEnter={(e) => onMouseEnter(e, epic, 'epic')}
          onMouseLeave={onMouseLeave}
        >
          <div className="flex items-center h-full px-2 relative w-full">
            {/* Only show title on the first segment */}
            {index === 0 && (
              <span className="text-white text-sm font-medium truncate flex-1">
                {epic.title}
              </span>
            )}
            {/* Only show progress on the last segment */}
            {index === segments.length - 1 && (
              <span className="text-white text-sm ml-auto bg-white bg-opacity-20 px-1 py-0.5 rounded text-xs">
                {epic.progress}%
              </span>
            )}
          </div>
          
          {/* Progress Bar Overlay */}
          <div className="absolute bottom-1 left-1 right-1 h-1 bg-white bg-opacity-60 rounded">
            <div
              className="h-full bg-white rounded transition-all duration-300"
              style={{ 
                width: `${epic.progress * (segment.width / (segments.reduce((sum, s) => sum + s.width, 0)))}%` 
              }}
            />
          </div>
        </div>
      ))}
    </>
  )
}

// Enhanced Issue Timeline Bar Component with Column Constraints
interface IssueTimelineBarProps {
  issue: Issue
  getDatePosition: (date: string) => number
  getConstrainedDatePosition: (startDate: string, endDate: string) => Array<{left: number, width: number, columnIndex: number}>
  onDragStart: (e: React.DragEvent, item: Issue, type: 'issue') => void
  onMouseEnter: (e: React.MouseEvent, item: Issue, type: 'issue') => void
  onMouseLeave: () => void
  onClick: (issue: Issue) => void
}

const IssueTimelineBar: React.FC<IssueTimelineBarProps> = ({ 
  issue, 
  getDatePosition,
  getConstrainedDatePosition,
  onDragStart,
  onMouseEnter,
  onMouseLeave,
  onClick
}) => {
  if (!issue.startDate || !issue.endDate) return null

  const segments = getConstrainedDatePosition(issue.startDate, issue.endDate)

  const getTypeColor = (type: string) => {
    switch (type) {
      case 'bug': return 'bg-red-500'
      case 'feature': return 'bg-blue-500'  
      case 'task': return 'bg-green-500'
      case 'story': return 'bg-purple-500'
      default: return 'bg-gray-500'
    }
  }

  const getTypeSymbol = (type: string) => {
    switch (type) {
      case 'bug': return '🐛'
      case 'feature': return '✨'
      case 'task': return '📋'
      case 'story': return '📖'
      default: return '📄'
    }
  }

  const handleClick = (e: React.MouseEvent) => {
    e.stopPropagation()
    onClick(issue)
  }

  return (
    <>
      {segments.map((segment, index) => (
        <div
          key={index}
          className={cn(
            "absolute inset-y-1 rounded-sm cursor-pointer hover:shadow-md transition-all border border-white hover:border-gray-300 hover:z-10 flex items-center",
            getTypeColor(issue.type)
          )}
          style={{
            left: `${segment.left}%`,
            width: `${segment.width}%`,
            height: '24px'
          }}
          draggable
          onDragStart={(e) => onDragStart(e, issue, 'issue')}
          onMouseEnter={(e) => onMouseEnter(e, issue, 'issue')}
          onMouseLeave={onMouseLeave}
          onClick={handleClick}
        >
          <div className="flex items-center h-full px-1 relative w-full">
            {/* Only show icon on the first segment */}
            {index === 0 && (
              <span className="text-xs">{getTypeSymbol(issue.type)}</span>
            )}
            {/* Only show story points on the last segment */}
            {index === segments.length - 1 && issue.storyPoints && (
              <span className="text-white text-xs ml-auto bg-white bg-opacity-20 px-1 rounded">
                {issue.storyPoints}
              </span>
            )}
          </div>
          
          {/* Progress Bar Overlay */}
          {issue.progress !== undefined && (
            <div className="absolute bottom-0 left-0 right-0 h-0.5 bg-white bg-opacity-40">
              <div
                className="h-full bg-white transition-all duration-300"
                style={{ 
                  width: `${issue.progress * (segment.width / (segments.reduce((sum, s) => sum + s.width, 0)))}%` 
                }}
              />
            </div>
          )}
        </div>
      ))}
    </>
  )
}

// Tooltip Component
interface TooltipProps {
  x: number
  y: number  
  issue: Issue | Epic
  type: 'issue' | 'epic'
}

const Tooltip: React.FC<TooltipProps> = ({ x, y, issue, type }) => {
  return (
    <div
      className="fixed z-50 bg-gray-900 text-white p-3 rounded-lg shadow-lg max-w-xs pointer-events-none"
      style={{
        left: x - 150, // Center the tooltip
        top: y - 10,
        transform: 'translateY(-100%)'
      }}
    >
      <div className="text-sm font-semibold mb-2">
        {type === 'epic' ? '🏗️ Epic' : '📋 Issue'}: {issue.key}
      </div>
      <div className="text-sm mb-2">{issue.title}</div>
      
      <div className="space-y-1 text-xs">
        <div className="flex justify-between">
          <span>Status:</span>
          <span className="capitalize">{issue.status.replace('-', ' ')}</span>
        </div>
        
        {issue.assignee && (
          <div className="flex justify-between">
            <span>Assignee:</span>
            <span>{issue.assignee.name}</span>
          </div>
        )}
        
        {type === 'epic' && (
          <div className="flex justify-between">
            <span>Progress:</span>
            <span>{(issue as Epic).progress}%</span>
          </div>
        )}
        
        {type === 'issue' && (issue as Issue).progress !== undefined && (
          <div className="flex justify-between">
            <span>Progress:</span>
            <span>{(issue as Issue).progress}%</span>
          </div>
        )}
        
        {type === 'issue' && (issue as Issue).storyPoints && (
          <div className="flex justify-between">
            <span>Story Points:</span>
            <span>{(issue as Issue).storyPoints}</span>
          </div>
        )}
        
        <div className="flex justify-between">
          <span>Duration:</span>
          <span>
            {issue.startDate ? new Date(issue.startDate).toLocaleDateString('ko-KR', { year: 'numeric', month: 'short', day: 'numeric' }) : 'N/A'} - 
            {issue.endDate ? new Date(issue.endDate).toLocaleDateString('ko-KR', { year: 'numeric', month: 'short', day: 'numeric' }) : 'N/A'}
          </span>
        </div>
        
        {type === 'issue' && (issue as Issue).description && (
          <div className="mt-2 pt-2 border-t border-gray-700">
            <div className="text-gray-300">{(issue as Issue).description}</div>
          </div>
        )}
      </div>
      
      {/* Tooltip arrow */}
      <div 
        className="absolute left-1/2 bottom-0 w-2 h-2 bg-gray-900 transform rotate-45 translate-y-1 -translate-x-1"
      />
    </div>
  )
}

export default TimelinePage