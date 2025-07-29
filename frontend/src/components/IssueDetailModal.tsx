import React, { useState } from 'react'
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Badge } from '@/components/ui/badge'
import { Avatar } from '@/components/ui/avatar'
import { Label } from '@/components/ui/label'
import { Checkbox } from '@/components/ui/checkbox'
import { 
  Calendar,
  User,
  Tag,
  MoreHorizontal,
  Paperclip,
  MessageCircle,
  Clock,
  Trash2,
  Share,
  Save,
  Send,
  Plus,
  X
} from 'lucide-react'
import { cn, formatDateTime } from '@/lib/utils'

interface Issue {
  id: string
  issueNumber: string
  title: string
  description: string
  type: 'bug' | 'feature' | 'task' | 'story'
  priority: 'high' | 'medium' | 'low'
  status: 'todo' | 'in-progress' | 'review' | 'done'
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
}

interface IssueDetailModalProps {
  issue: Issue | null
  isOpen: boolean
  onClose: () => void
  onUpdate?: (issue: Issue) => void
}

const IssueDetailModal: React.FC<IssueDetailModalProps> = ({
  issue,
  isOpen,
  onClose,
  onUpdate
}) => {
  const [newComment, setNewComment] = useState('')
  const [editingDescription, setEditingDescription] = useState(false)
  const [description, setDescription] = useState(issue?.description || '')

  if (!issue) return null

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

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'todo': return 'bg-gray-100 text-gray-800'
      case 'in-progress': return 'bg-blue-100 text-blue-800'
      case 'review': return 'bg-yellow-100 text-yellow-800'
      case 'done': return 'bg-green-100 text-green-800'
      default: return 'bg-gray-100 text-gray-800'
    }
  }

  const handleAddComment = () => {
    if (!newComment.trim()) return
    
    // Here you would typically call an API to add the comment
    console.log('Adding comment:', newComment)
    setNewComment('')
  }

  const handleSubtaskToggle = (subtaskId: string) => {
    // Here you would typically call an API to update the subtask
    console.log('Toggling subtask:', subtaskId)
  }

  const handleSaveDescription = () => {
    // Here you would typically call an API to update the description
    console.log('Saving description:', description)
    setEditingDescription(false)
  }

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="max-w-5xl h-[90vh] p-0 overflow-hidden">
        {/* Modal Header */}
        <DialogHeader className="px-6 py-4 border-b">
          <div className="flex items-center space-x-3">
            <span className="text-2xl">{getTypeIcon(issue.type)}</span>
            <div className="flex-1">
              <DialogTitle className="text-xl font-semibold text-gray-900">
                {issue.title}
              </DialogTitle>
              <p className="text-sm text-gray-500 mt-1">{issue.issueNumber}</p>
            </div>
          </div>
        </DialogHeader>

        {/* Modal Body */}
        <div className="flex-1 flex overflow-hidden">
          {/* Left Panel */}
          <div className="flex-1 flex flex-col overflow-hidden border-r">
            <div className="flex-1 overflow-y-auto p-6 space-y-6">
              {/* Description */}
              <div>
                <div className="flex items-center justify-between mb-3">
                  <h3 className="text-lg font-semibold text-gray-900">설명</h3>
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => setEditingDescription(!editingDescription)}
                  >
                    {editingDescription ? '취소' : '편집'}
                  </Button>
                </div>
                
                {editingDescription ? (
                  <div className="space-y-3">
                    <textarea
                      value={description}
                      onChange={(e) => setDescription(e.target.value)}
                      className="w-full h-32 p-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent resize-none"
                      placeholder="이슈에 대한 상세한 설명을 입력하세요..."
                    />
                    <div className="flex space-x-2">
                      <Button size="sm" onClick={handleSaveDescription}>
                        저장
                      </Button>
                      <Button
                        size="sm"
                        variant="ghost"
                        onClick={() => {
                          setDescription(issue.description)
                          setEditingDescription(false)
                        }}
                      >
                        취소
                      </Button>
                    </div>
                  </div>
                ) : (
                  <div className="prose max-w-none">
                    <div className="whitespace-pre-wrap text-gray-700">
                      {issue.description || '설명이 없습니다.'}
                    </div>
                  </div>
                )}
              </div>

              {/* Attachments */}
              <div>
                <h3 className="text-lg font-semibold text-gray-900 mb-3">첨부파일</h3>
                <div className="space-y-2">
                  {issue.attachments.map((attachment) => (
                    <div
                      key={attachment.id}
                      className="flex items-center justify-between p-3 border border-gray-200 rounded-md hover:bg-gray-50"
                    >
                      <div className="flex items-center space-x-3">
                        <Paperclip className="h-4 w-4 text-gray-400" />
                        <div>
                          <p className="text-sm font-medium text-gray-900">
                            {attachment.name}
                          </p>
                          <p className="text-xs text-gray-500">
                            {attachment.size} • {attachment.type}
                          </p>
                        </div>
                      </div>
                      <Button variant="ghost" size="sm">
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>
                  ))}
                  <Button variant="ghost" className="w-full border-2 border-dashed border-gray-300 hover:border-gray-400 h-auto py-3">
                    <Plus className="h-4 w-4 mr-2" />
                    첨부파일 추가
                  </Button>
                </div>
              </div>

              {/* Comments */}
              <div>
                <h3 className="text-lg font-semibold text-gray-900 mb-3">댓글</h3>
                <div className="space-y-4">
                  {issue.comments.map((comment) => (
                    <div key={comment.id} className="flex space-x-3">
                      <Avatar 
                        name={comment.user.name}
                        src={comment.user.avatar}
                        size="sm"
                      />
                      <div className="flex-1">
                        <div className="flex items-center space-x-2 mb-1">
                          <span className="text-sm font-medium text-gray-900">
                            {comment.user.name}
                          </span>
                          <span className="text-xs text-gray-500">
                            {formatDateTime(comment.createdAt)}
                          </span>
                        </div>
                        <div className="text-sm text-gray-700 bg-gray-50 rounded-md p-3">
                          {comment.content}
                        </div>
                      </div>
                    </div>
                  ))}
                  
                  {/* Add Comment */}
                  <div className="flex space-x-3">
                    <Avatar name="Current User" size="sm" />
                    <div className="flex-1">
                      <textarea
                        value={newComment}
                        onChange={(e) => setNewComment(e.target.value)}
                        placeholder="댓글을 입력하세요..."
                        className="w-full p-3 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent resize-none"
                        rows={3}
                      />
                      <div className="flex justify-end mt-2">
                        <Button size="sm" onClick={handleAddComment}>
                          <Send className="h-4 w-4 mr-2" />
                          댓글 작성
                        </Button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Right Panel */}
          <div className="w-80 overflow-y-auto p-6 space-y-6">
            {/* Status */}
            <div>
              <h4 className="text-sm font-medium text-gray-900 mb-2">상태</h4>
              <select
                value={issue.status}
                className={cn(
                  "w-full px-3 py-2 rounded-md text-sm font-medium border focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent",
                  getStatusColor(issue.status)
                )}
              >
                <option value="todo">🟡 To Do</option>
                <option value="in-progress">🔵 In Progress</option>
                <option value="review">🟠 Review</option>
                <option value="done">🟢 Done</option>
              </select>
            </div>

            {/* Assignee */}
            <div>
              <h4 className="text-sm font-medium text-gray-900 mb-2">담당자</h4>
              <div className="flex items-center space-x-3 p-3 border border-gray-200 rounded-md">
                {issue.assignee ? (
                  <>
                    <Avatar 
                      name={issue.assignee.name}
                      src={issue.assignee.avatar}
                      size="sm"
                    />
                    <span className="text-sm text-gray-900">{issue.assignee.name}</span>
                  </>
                ) : (
                  <>
                    <User className="h-5 w-5 text-gray-400" />
                    <span className="text-sm text-gray-500">담당자 없음</span>
                  </>
                )}
              </div>
            </div>

            {/* Due Date */}
            <div>
              <h4 className="text-sm font-medium text-gray-900 mb-2">마감일</h4>
              <div className="flex items-center space-x-3 p-3 border border-gray-200 rounded-md">
                <Calendar className="h-4 w-4 text-gray-400" />
                <span className="text-sm text-gray-900">
                  {issue.dueDate ? formatDateTime(issue.dueDate) : '설정되지 않음'}
                </span>
              </div>
            </div>

            {/* Priority */}
            <div>
              <h4 className="text-sm font-medium text-gray-900 mb-2">우선순위</h4>
              <select
                value={issue.priority}
                className="w-full px-3 py-2 border border-gray-200 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
              >
                <option value="high">🔥 High</option>
                <option value="medium">📊 Medium</option>
                <option value="low">📝 Low</option>
              </select>
            </div>

            {/* Labels */}
            <div>
              <h4 className="text-sm font-medium text-gray-900 mb-2">라벨</h4>
              <div className="flex flex-wrap gap-2">
                {issue.labels.map((label) => (
                  <Badge key={label.id} style={{ backgroundColor: `${label.color}20`, color: label.color }}>
                    {label.name}
                    <Button variant="ghost" size="sm" className="ml-1 h-auto p-0">
                      <X className="h-3 w-3" />
                    </Button>
                  </Badge>
                ))}
                <Button variant="ghost" size="sm" className="h-6 px-2 text-xs">
                  <Plus className="h-3 w-3 mr-1" />
                  라벨 추가
                </Button>
              </div>
            </div>

            {/* Story Points */}
            <div>
              <h4 className="text-sm font-medium text-gray-900 mb-2">스토리 포인트</h4>
              <Input
                type="number"
                value={issue.storyPoints || ''}
                placeholder="포인트 입력"
                className="w-full"
              />
            </div>

            {/* Subtasks */}
            <div>
              <div className="flex items-center justify-between mb-2">
                <h4 className="text-sm font-medium text-gray-900">서브태스크</h4>
                <Button variant="ghost" size="sm">
                  <Plus className="h-4 w-4" />
                </Button>
              </div>
              <div className="space-y-2">
                {issue.subtasks.map((subtask) => (
                  <div key={subtask.id} className="flex items-center space-x-2">
                    <Checkbox
                      checked={subtask.completed}
                      onCheckedChange={() => handleSubtaskToggle(subtask.id)}
                    />
                    <span className={cn(
                      "text-sm flex-1",
                      subtask.completed ? "line-through text-gray-500" : "text-gray-900"
                    )}>
                      {subtask.title}
                    </span>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>

        {/* Modal Footer */}
        <DialogFooter className="px-6 py-4 border-t">
          <div className="flex justify-between w-full">
            <div className="flex space-x-2">
              <Button variant="ghost" size="sm">
                <Clock className="h-4 w-4 mr-2" />
                활동 로그
              </Button>
            </div>
            <div className="flex space-x-2">
              <Button variant="ghost" size="sm">
                <Trash2 className="h-4 w-4 mr-2" />
                삭제
              </Button>
              <Button variant="ghost" size="sm">
                <Share className="h-4 w-4 mr-2" />
                공유
              </Button>
              <Button size="sm">
                <Save className="h-4 w-4 mr-2" />
                변경사항 저장
              </Button>
            </div>
          </div>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}

export default IssueDetailModal