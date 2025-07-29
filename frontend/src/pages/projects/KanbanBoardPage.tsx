import React, { useState } from 'react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Avatar } from '@/components/ui/avatar'
import { Label } from '@/components/ui/label'
import { Tabs, TabsList, TabsTrigger, TabsContent } from '@/components/ui/tabs'
import IssueDetailModal from '@/components/IssueDetailModal'
import { 
  Plus, 
  Filter, 
  Users, 
  Settings,
  MoreHorizontal,
  MessageCircle,
  Paperclip,
  CheckSquare
} from 'lucide-react'
import { cn } from '@/lib/utils'

interface Issue {
  id: string
  issueNumber: string
  title: string
  description: string
  type: 'bug' | 'feature' | 'task' | 'story'
  priority: 'high' | 'medium' | 'low'
  assignee?: {
    id: string
    name: string
    avatar?: string
  }
  dueDate?: string
  storyPoints?: number
  labels: Array<{
    id: string
    name: string
    color: string
  }>
  subtasks: Array<{
    id: string
    title: string
    completed: boolean
  }>
  attachments: Array<{
    id: string
    name: string
    size: string
    type: string
  }>
  comments: Array<{
    id: string
    user: {
      id: string
      name: string
      avatar?: string
    }
    content: string
    createdAt: string
  }>
  createdAt: string
  updatedAt: string
  status: 'todo' | 'in-progress' | 'review' | 'done'
}

interface Column {
  id: string
  title: string
  emoji: string
  issues: Issue[]
}

const KanbanBoardPage: React.FC = () => {
  const [draggedIssue, setDraggedIssue] = useState<Issue | null>(null)
  const [dragOverColumn, setDragOverColumn] = useState<string | null>(null)
  const [activeTabColumn, setActiveTabColumn] = useState('todo')
  const [searchQuery, setSearchQuery] = useState('')
  const [assigneeFilter, setAssigneeFilter] = useState('all')
  const [selectedIssue, setSelectedIssue] = useState<Issue | null>(null)
  const [isModalOpen, setIsModalOpen] = useState(false)

  // Mock data with detailed issue information
  const [columns, setColumns] = useState<Column[]>([
    {
      id: 'todo',
      title: 'TODO',
      emoji: '📝',
      issues: [
        {
          id: '1',
          issueNumber: 'PROJ-123',
          title: 'Implement user authentication system',
          description: '## Requirements\n- JWT-based authentication\n- Email/password login\n- Social login integration\n\n## Acceptance Criteria\n1. User can register\n2. User can login\n3. User can reset password',
          type: 'feature',
          priority: 'high',
          assignee: { id: '1', name: 'John Doe' },
          dueDate: '2024-02-15T10:00:00Z',
          storyPoints: 8,
          labels: [
            { id: '1', name: 'Backend', color: '#3b82f6' },
            { id: '2', name: 'Security', color: '#ef4444' }
          ],
          subtasks: [
            { id: '1', title: 'Setup JWT library', completed: true },
            { id: '2', title: 'Create login endpoint', completed: false },
            { id: '3', title: 'Add password hashing', completed: false },
            { id: '4', title: 'Implement logout', completed: false }
          ],
          attachments: [
            { id: '1', name: 'login-mockup.png', size: '2.4 MB', type: 'PNG' },
            { id: '2', name: 'auth-flow-diagram.pdf', size: '1.1 MB', type: 'PDF' }
          ],
          comments: [
            {
              id: '1',
              user: { id: '2', name: 'Alice Brown' },
              content: "I've started working on the JWT implementation. ETA 2 days.",
              createdAt: '2024-01-28T14:30:00Z'
            },
            {
              id: '2',
              user: { id: '1', name: 'John Doe' },
              content: "Great! Let me know if you need help with the frontend part.",
              createdAt: '2024-01-27T09:15:00Z'
            }
          ],
          createdAt: '2024-01-25T08:00:00Z',
          updatedAt: '2024-01-28T14:30:00Z',
          status: 'todo'
        },
        {
          id: '2',
          issueNumber: 'PROJ-124',
          title: 'Design user profile page',
          description: 'Create responsive user profile page with edit functionality.',
          type: 'task',
          priority: 'medium',
          assignee: { id: '2', name: 'Jane Smith' },
          dueDate: '2024-02-10T17:00:00Z',
          storyPoints: 5,
          labels: [
            { id: '3', name: 'Frontend', color: '#10b981' },
            { id: '4', name: 'UI/UX', color: '#8b5cf6' }
          ],
          subtasks: [
            { id: '5', title: 'Create wireframes', completed: false },
            { id: '6', title: 'Implement layout', completed: false }
          ],
          attachments: [],
          comments: [
            {
              id: '3',
              user: { id: '3', name: 'Mike Johnson' },
              content: "Should we include social media links in the profile?",
              createdAt: '2024-01-26T11:20:00Z'
            }
          ],
          createdAt: '2024-01-25T10:00:00Z',
          updatedAt: '2024-01-26T11:20:00Z',
          status: 'todo'
        }
      ]
    },
    {
      id: 'in-progress',
      title: 'IN PROGRESS',
      emoji: '⚡',
      issues: [
        {
          id: '3',
          issueNumber: 'PROJ-125',
          title: 'API integration for dashboard data',
          description: 'Integrate REST API endpoints for dashboard statistics and charts.',
          type: 'task',
          priority: 'high',
          assignee: { id: '3', name: 'Bob Wilson' },
          dueDate: '2024-02-05T12:00:00Z',
          storyPoints: 13,
          labels: [
            { id: '1', name: 'Backend', color: '#3b82f6' },
            { id: '5', name: 'API', color: '#f59e0b' }
          ],
          subtasks: [
            { id: '7', title: 'Design API endpoints', completed: true },
            { id: '8', title: 'Implement data fetching', completed: true },
            { id: '9', title: 'Add error handling', completed: false }
          ],
          attachments: [
            { id: '3', name: 'api-specs.json', size: '45 KB', type: 'JSON' }
          ],
          comments: [
            {
              id: '4',
              user: { id: '4', name: 'Sarah Davis' },
              content: "API looks good. Just need to add proper error handling.",
              createdAt: '2024-01-28T16:45:00Z'
            }
          ],
          createdAt: '2024-01-24T09:00:00Z',
          updatedAt: '2024-01-28T16:45:00Z',
          status: 'in-progress'
        }
      ]
    },
    {
      id: 'review',
      title: 'REVIEW',
      emoji: '👀',
      issues: [
        {
          id: '5',
          issueNumber: 'PROJ-127',
          title: 'Add data validation to forms',
          description: 'Implement client-side and server-side validation for all forms.',
          type: 'feature',
          priority: 'medium',
          assignee: { id: '5', name: 'Mike Johnson' },
          dueDate: '2024-02-08T15:00:00Z',
          storyPoints: 3,
          labels: [
            { id: '3', name: 'Frontend', color: '#10b981' },
            { id: '7', name: 'Validation', color: '#8b5cf6' }
          ],
          subtasks: [
            { id: '10', title: 'Add form validation library', completed: true },
            { id: '11', title: 'Implement validation rules', completed: true }
          ],
          attachments: [],
          comments: [],
          createdAt: '2024-01-23T14:00:00Z',
          updatedAt: '2024-01-28T10:00:00Z',
          status: 'review'
        }
      ]
    },
    {
      id: 'done',
      title: 'DONE',
      emoji: '✅',
      issues: [
        {
          id: '6',
          issueNumber: 'PROJ-128',
          title: 'Setup CI/CD pipeline',
          description: 'Configure GitHub Actions for automated testing and deployment.',
          type: 'task',
          priority: 'low',
          assignee: { id: '6', name: 'Sarah Davis' },
          dueDate: '2024-01-30T18:00:00Z',
          storyPoints: 8,
          labels: [
            { id: '8', name: 'DevOps', color: '#06b6d4' }
          ],
          subtasks: [
            { id: '12', title: 'Setup GitHub Actions', completed: true },
            { id: '13', title: 'Configure testing', completed: true },
            { id: '14', title: 'Setup deployment', completed: true },
            { id: '15', title: 'Add monitoring', completed: true },
            { id: '16', title: 'Documentation', completed: true }
          ],
          attachments: [
            { id: '4', name: 'ci-config.yml', size: '2.1 KB', type: 'YAML' },
            { id: '5', name: 'deployment-guide.md', size: '8.5 KB', type: 'Markdown' },
            { id: '6', name: 'monitoring-setup.pdf', size: '1.8 MB', type: 'PDF' }
          ],
          comments: [
            {
              id: '5',
              user: { id: '1', name: 'John Doe' },
              content: "Great job on the CI/CD setup! Deployment is now much smoother.",
              createdAt: '2024-01-29T13:20:00Z'
            }
          ],
          createdAt: '2024-01-20T11:00:00Z',
          updatedAt: '2024-01-29T13:20:00Z',
          status: 'done'
        }
      ]
    }
  ])

  const getTypeIcon = (type: string) => {
    switch (type) {
      case 'bug': return '🐛'
      case 'feature': return '✨'
      case 'task': return '📋'
      case 'story': return '📖'
      default: return '📄'
    }
  }

  const getPriorityIcon = (priority: string) => {
    switch (priority) {
      case 'high': return '🔥'
      case 'medium': return '📊'
      case 'low': return '📝'
      default: return '📄'
    }
  }

  // Drag and Drop handlers
  const handleDragStart = (e: React.DragEvent, issue: Issue) => {
    setDraggedIssue(issue)
    e.dataTransfer.effectAllowed = 'move'
  }

  const handleDragOver = (e: React.DragEvent, columnId: string) => {
    e.preventDefault()
    e.dataTransfer.dropEffect = 'move'
    setDragOverColumn(columnId)
  }

  const handleDragLeave = () => {
    setDragOverColumn(null)
  }

  const handleDrop = (e: React.DragEvent, targetColumnId: string) => {
    e.preventDefault()
    setDragOverColumn(null)

    if (!draggedIssue) return

    // Update columns state
    setColumns(prevColumns => {
      const newColumns = prevColumns.map(column => ({
        ...column,
        issues: column.issues.filter(issue => issue.id !== draggedIssue.id)
      }))

      const targetColumn = newColumns.find(col => col.id === targetColumnId)
      if (targetColumn) {
        targetColumn.issues.push({
          ...draggedIssue,
          status: targetColumnId as Issue['status']
        })
      }

      return newColumns
    })

    setDraggedIssue(null)
  }

  const handleDragEnd = () => {
    setDraggedIssue(null)
    setDragOverColumn(null)
  }

  // Modal handlers
  const handleIssueClick = (issue: Issue) => {
    setSelectedIssue(issue)
    setIsModalOpen(true)
  }

  const handleModalClose = () => {
    setIsModalOpen(false)
    setSelectedIssue(null)
  }

  // Filter issues
  const filterIssues = (issues: Issue[]) => {
    return issues.filter(issue => {
      const matchesSearch = issue.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
                           issue.issueNumber.toLowerCase().includes(searchQuery.toLowerCase())
      const matchesAssignee = assigneeFilter === 'all' || issue.assignee?.id === assigneeFilter
      
      return matchesSearch && matchesAssignee
    })
  }

  return (
    <div className="p-6 h-full flex flex-col">
      {/* Board Header */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 flex items-center">
            📋 Project Alpha
          </h1>
          <p className="text-gray-600 mt-1">Kanban board for project management</p>
        </div>
        <div className="flex items-center space-x-2">
          <Button size="sm" variant="outline">
            <Plus className="h-4 w-4 mr-2" />
            Add Issue
          </Button>
          <Button size="sm" variant="outline">
            <Filter className="h-4 w-4 mr-2" />
            Filter
          </Button>
          <Button size="sm" variant="outline">
            <Users className="h-4 w-4 mr-2" />
            Assignee
          </Button>
          <Button size="sm" variant="outline">
            <Settings className="h-4 w-4" />
          </Button>
        </div>
      </div>

      {/* Filter Bar */}
      <div className="flex items-center space-x-4 mb-6">
        <div className="flex-1 min-w-0">
          <Input
            placeholder="Search issues..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="max-w-md"
          />
        </div>
        <select
          value={assigneeFilter}
          onChange={(e) => setAssigneeFilter(e.target.value)}
          className="px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
        >
          <option value="all">All Assignees</option>
          <option value="1">John Doe</option>
          <option value="2">Jane Smith</option>
          <option value="3">Bob Wilson</option>
          <option value="4">Alice Brown</option>
        </select>
      </div>

      {/* Mobile Tabs (visible on small screens) */}
      <div className="lg:hidden mb-6">
        <Tabs value={activeTabColumn} onValueChange={setActiveTabColumn}>
          <TabsList className="grid w-full grid-cols-4">
            {columns.map((column) => (
              <TabsTrigger key={column.id} value={column.id} className="text-xs">
                {column.emoji} {column.title} ({filterIssues(column.issues).length})
              </TabsTrigger>
            ))}
          </TabsList>
          
          {columns.map((column) => (
            <TabsContent key={column.id} value={column.id}>
              <KanbanColumn
                column={column}
                issues={filterIssues(column.issues)}
                draggedIssue={draggedIssue}
                dragOverColumn={dragOverColumn}
                onDragStart={handleDragStart}
                onDragOver={handleDragOver}
                onDragLeave={handleDragLeave}
                onDrop={handleDrop}
                onDragEnd={handleDragEnd}
                onIssueClick={handleIssueClick}
                getTypeIcon={getTypeIcon}
                getPriorityIcon={getPriorityIcon}
              />
            </TabsContent>
          ))}
        </Tabs>
      </div>

      {/* Desktop Kanban Columns */}
      <div className="hidden lg:flex flex-1 space-x-6 overflow-x-auto">
        {columns.map((column) => (
          <KanbanColumn
            key={column.id}
            column={column}
            issues={filterIssues(column.issues)}
            draggedIssue={draggedIssue}
            dragOverColumn={dragOverColumn}
            onDragStart={handleDragStart}
            onDragOver={handleDragOver}
            onDragLeave={handleDragLeave}
            onDrop={handleDrop}
            onDragEnd={handleDragEnd}
            onIssueClick={handleIssueClick}
            getTypeIcon={getTypeIcon}
            getPriorityIcon={getPriorityIcon}
          />
        ))}
      </div>

      {/* Issue Detail Modal */}
      <IssueDetailModal
        issue={selectedIssue}
        isOpen={isModalOpen}
        onClose={handleModalClose}
      />
    </div>
  )
}

// Kanban Column Component
interface KanbanColumnProps {
  column: Column
  issues: Issue[]
  draggedIssue: Issue | null
  dragOverColumn: string | null
  onDragStart: (e: React.DragEvent, issue: Issue) => void
  onDragOver: (e: React.DragEvent, columnId: string) => void
  onDragLeave: () => void
  onDrop: (e: React.DragEvent, columnId: string) => void
  onDragEnd: () => void
  onIssueClick: (issue: Issue) => void
  getTypeIcon: (type: string) => string
  getPriorityIcon: (priority: string) => string
}

const KanbanColumn: React.FC<KanbanColumnProps> = ({
  column,
  issues,
  draggedIssue,
  dragOverColumn,
  onDragStart,
  onDragOver,
  onDragLeave,
  onDrop,
  onDragEnd,
  onIssueClick,
  getTypeIcon,
  getPriorityIcon
}) => {
  const isDropTarget = dragOverColumn === column.id

  return (
    <div className="flex-1 min-w-80 lg:min-w-72">
      <Card className={cn(
        "h-full transition-colors",
        isDropTarget ? "ring-2 ring-primary ring-opacity-50 bg-primary/5" : ""
      )}>
        <CardHeader className="pb-3">
          <div className="flex items-center justify-between">
            <CardTitle className="text-sm font-semibold text-gray-900 flex items-center">
              <span className="text-lg mr-2">{column.emoji}</span>
              {column.title} ({issues.length})
            </CardTitle>
            <Button variant="ghost" size="sm">
              <MoreHorizontal className="h-4 w-4" />
            </Button>
          </div>
        </CardHeader>
        
        <CardContent 
          className="flex-1 space-y-3 min-h-96"
          onDragOver={(e) => onDragOver(e, column.id)}
          onDragLeave={onDragLeave}
          onDrop={(e) => onDrop(e, column.id)}
        >
          {issues.map((issue) => (
            <IssueCard
              key={issue.id}
              issue={issue}
              isDragging={draggedIssue?.id === issue.id}
              onDragStart={onDragStart}
              onDragEnd={onDragEnd}
              onClick={onIssueClick}
              getTypeIcon={getTypeIcon}
              getPriorityIcon={getPriorityIcon}
            />
          ))}
          
          <Button
            variant="ghost"
            className="w-full border-2 border-dashed border-gray-300 hover:border-gray-400 hover:bg-gray-50 h-auto py-3"
          >
            <Plus className="h-4 w-4 mr-2" />
            Add Issue
          </Button>
        </CardContent>
      </Card>
    </div>
  )
}

// Issue Card Component
interface IssueCardProps {
  issue: Issue
  isDragging: boolean
  onDragStart: (e: React.DragEvent, issue: Issue) => void
  onDragEnd: () => void
  onClick: (issue: Issue) => void
  getTypeIcon: (type: string) => string
  getPriorityIcon: (priority: string) => string
}

const IssueCard: React.FC<IssueCardProps> = ({
  issue,
  isDragging,
  onDragStart,
  onDragEnd,
  onClick,
  getTypeIcon,
  getPriorityIcon
}) => {
  const handleClick = () => {
    // Prevent opening modal during drag
    if (!isDragging) {
      onClick(issue)
    }
  }

  return (
    <Card
      draggable
      onDragStart={(e) => onDragStart(e, issue)}
      onDragEnd={onDragEnd}
      onClick={handleClick}
      className={cn(
        "cursor-grab active:cursor-grabbing hover:shadow-md transition-all",
        isDragging ? "opacity-50 rotate-2 shadow-lg" : ""
      )}
    >
      <CardContent className="p-3">
        {/* Header */}
        <div className="flex items-start justify-between mb-2">
          <span className="text-xs font-medium text-gray-500">
            {issue.issueNumber}
          </span>
          <span className="text-base">{getPriorityIcon(issue.priority)}</span>
        </div>

        {/* Title */}
        <h4 className="text-sm font-medium text-gray-900 mb-3 line-clamp-2">
          {getTypeIcon(issue.type)} {issue.title}
        </h4>

        {/* Labels */}
        <div className="flex flex-wrap gap-1 mb-3">
          {issue.labels.map((label) => (
            <Label key={label.id} color={label.color} className="text-xs">
              {label.name}
            </Label>
          ))}
        </div>

        {/* Footer */}
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-3 text-xs text-gray-500">
            {issue.subtasks.length > 0 && (
              <div className="flex items-center">
                <CheckSquare className="h-3 w-3 mr-1" />
                {issue.subtasks.filter(st => st.completed).length}/{issue.subtasks.length}
              </div>
            )}
            {issue.comments.length > 0 && (
              <div className="flex items-center">
                <MessageCircle className="h-3 w-3 mr-1" />
                {issue.comments.length}
              </div>
            )}
            {issue.attachments.length > 0 && (
              <div className="flex items-center">
                <Paperclip className="h-3 w-3 mr-1" />
                {issue.attachments.length}
              </div>
            )}
          </div>
          
          {issue.assignee && (
            <Avatar 
              name={issue.assignee.name}
              src={issue.assignee.avatar}
              size="xs"
            />
          )}
        </div>
      </CardContent>
    </Card>
  )
}

export default KanbanBoardPage 