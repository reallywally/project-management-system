# UI 페이지 구성도 및 와이어프레임

## 목차
1. [디자인 시스템 개요](#1-디자인-시스템-개요)
2. [로그인 페이지](#2-로그인-페이지)
3. [메인 레이아웃 구조](#3-메인-레이아웃-구조)
4. [프로젝트 목록 페이지](#4-프로젝트-목록-페이지)
5. [칸반 보드 페이지](#5-칸반-보드-페이지)
6. [이슈 상세 모달](#6-이슈-상세-모달)
7. [개인 대시보드](#7-개인-대시보드)
8. [반응형 디자인 고려사항](#8-반응형-디자인-고려사항)
9. [UX 플로우](#9-ux-플로우)

## 1. 디자인 시스템 개요

### 1.1 색상 팔레트
```css
/* Primary Colors */
:root {
  --primary-50: #eff6ff;
  --primary-100: #dbeafe;
  --primary-500: #3b82f6;
  --primary-600: #2563eb;
  --primary-700: #1d4ed8;
  
  /* Secondary Colors */
  --gray-50: #f9fafb;
  --gray-100: #f3f4f6;
  --gray-200: #e5e7eb;
  --gray-500: #6b7280;
  --gray-700: #374151;
  --gray-900: #111827;
  
  /* Status Colors */
  --success: #10b981;
  --warning: #f59e0b;
  --danger: #ef4444;
  --info: #3b82f6;
}
```

### 1.2 타이포그래피
```css
/* Font Sizes */
.text-xs { font-size: 0.75rem; }    /* 12px */
.text-sm { font-size: 0.875rem; }   /* 14px */
.text-base { font-size: 1rem; }     /* 16px */
.text-lg { font-size: 1.125rem; }   /* 18px */
.text-xl { font-size: 1.25rem; }    /* 20px */
.text-2xl { font-size: 1.5rem; }    /* 24px */

/* Font Weights */
.font-normal { font-weight: 400; }
.font-medium { font-weight: 500; }
.font-semibold { font-weight: 600; }
.font-bold { font-weight: 700; }
```

### 1.3 간격 시스템
```css
/* Spacing Scale */
.space-1 { gap: 0.25rem; }   /* 4px */
.space-2 { gap: 0.5rem; }    /* 8px */
.space-3 { gap: 0.75rem; }   /* 12px */
.space-4 { gap: 1rem; }      /* 16px */
.space-6 { gap: 1.5rem; }    /* 24px */
.space-8 { gap: 2rem; }      /* 32px */
```

## 2. 로그인 페이지

### 2.1 레이아웃 구조
```
┌─────────────────────────────────────────┐
│                 Header                   │
│    🏢 Project Management System         │
│                                         │
│            Login Form                   │
│  ┌─────────────────────────────────┐   │
│  │  📧 Email Address               │   │
│  │  ┌─────────────────────────┐   │   │
│  │  │ user@example.com        │   │   │
│  │  └─────────────────────────┘   │   │
│  │                                 │   │
│  │  🔒 Password                    │   │
│  │  ┌─────────────────────────┐   │   │
│  │  │ ••••••••••••••••        │   │   │
│  │  └─────────────────────────┘   │   │
│  │                                 │   │
│  │  ☑️ Remember me                │   │
│  │                                 │   │
│  │    ┌─────────────────────┐     │   │
│  │    │      Sign In        │     │   │
│  │    └─────────────────────┘     │   │
│  │                                 │   │
│  │  📝 Don't have an account?     │   │
│  │      🔗 Sign up here           │   │
│  │                                 │   │
│  │  🔄 Forgot your password?      │   │
│  └─────────────────────────────────┘   │
│                                         │
│              Footer                     │
│  🌐 Language  🎨 Theme  📄 Privacy     │
└─────────────────────────────────────────┘
```

### 2.2 주요 기능
- **이메일/비밀번호 입력**: 필수 필드 검증
- **Remember Me**: 다음 로그인 시 이메일 기억
- **소셜 로그인**: Google, GitHub 연동 (선택사항)
- **비밀번호 재설정**: 이메일을 통한 재설정 링크 발송
- **언어 선택**: 다국어 지원
- **테마 토글**: 라이트/다크 모드 선택

### 2.3 상태별 UI
```typescript
// 로딩 상태
<Button disabled className="opacity-50">
  <Spinner /> Signing in...
</Button>

// 에러 상태
<div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
  ❌ Invalid email or password. Please try again.
</div>

// 성공 상태
<div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded">
  ✅ Login successful! Redirecting...
</div>
```

## 3. 메인 레이아웃 구조

### 3.1 레이아웃 분할
```
┌──────────────────────────────────────────────────────────────┐
│                        Header (Fixed)                        │
│  🏠 Logo  🔍 Search     🔔 Notifications  👤 User Profile   │
├──────────┬───────────────────────────────────────────────────┤
│          │                                                   │
│ Sidebar  │                Main Content                       │
│ (Sticky) │                                                   │
│          │  ┌─────────────────────────────────────────────┐  │
│ 📊 Dash  │  │              Page Header                    │  │
│ 📁 Proj  │  │  📄 Title    🔧 Actions    🔍 Search       │  │
│ 📋 Issu  │  ├─────────────────────────────────────────────┤  │
│ 🎯 Assi  │  │                                             │  │  
│ 📈 Repo  │  │              Content Body                   │  │
│ ⚙️ Sett   │  │                                             │  │
│          │  │                                             │  │
│          │  │                                             │  │
│          │  └─────────────────────────────────────────────┘  │
│          │                                                   │
└──────────┴───────────────────────────────────────────────────┘
```

### 3.2 헤더 컴포넌트
```typescript
const Header = () => (
  <header className="bg-white border-b border-gray-200 px-6 py-4">
    <div className="flex items-center justify-between">
      <div className="flex items-center space-x-4">
        <Logo />
        <GlobalSearch />
      </div>
      
      <div className="flex items-center space-x-4">
        <NotificationBell />
        <UserProfileDropdown />
      </div>
    </div>
  </header>
);
```

### 3.3 사이드바 네비게이션
```typescript
const Sidebar = () => (
  <aside className="w-64 bg-gray-50 border-r border-gray-200 h-full">
    <nav className="p-4 space-y-2">
      <NavItem icon="📊" label="Dashboard" href="/dashboard" />
      <NavItem icon="📁" label="Projects" href="/projects" />
      <NavItem icon="📋" label="My Issues" href="/issues/me" />
      <NavItem icon="🎯" label="Assigned to Me" href="/issues/assigned" />
      <NavItem icon="📈" label="Reports" href="/reports" />
      <NavItem icon="⚙️" label="Settings" href="/settings" />
    </nav>
  </aside>
);
```

## 4. 프로젝트 목록 페이지

### 4.1 페이지 레이아웃
```
┌─────────────────────────────────────────────────────────────┐
│                     Page Header                             │
│  📁 Projects (12)    ➕ New Project    🔍 Search Projects  │
├─────────────────────────────────────────────────────────────┤
│                   Filter & Sort Bar                        │
│  📊 Status: All ▼  👤 Owner: All ▼  📅 Sort: Latest ▼    │
├─────────────────────────────────────────────────────────────┤
│                   Project Grid                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐       │
│  │ Project A   │  │ Project B   │  │ Project C   │       │
│  │ ────────────│  │ ────────────│  │ ────────────│       │
│  │ 🟢 Active   │  │ 🟡 Planning │  │ 🔴 On Hold  │       │
│  │ 👤 John Doe │  │ 👤 Jane S.  │  │ 👤 Bob W.   │       │
│  │ 📋 15/20    │  │ 📋 8/12     │  │ 📋 5/8      │       │
│  │ 📊 ████▓▓▓▓ │  │ 📊 ██████▓▓ │  │ 📊 ███████▓ │       │
│  │ 👥 5 members│  │ 👥 3 members│  │ 👥 4 members│       │
│  └─────────────┘  └─────────────┘  └─────────────┘       │
│                                                           │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐       │
│  │ Project D   │  │ Project E   │  │ Project F   │       │
│  │ ...         │  │ ...         │  │ ...         │       │
│  └─────────────┘  └─────────────┘  └─────────────┘       │
├─────────────────────────────────────────────────────────────┤
│                   Pagination                              │
│           ← Previous  1  2  3  ...  10  Next →            │
└─────────────────────────────────────────────────────────────┘
```

### 4.2 프로젝트 카드 컴포넌트
```typescript
const ProjectCard = ({ project }: { project: Project }) => (
  <div className="bg-white rounded-lg border border-gray-200 p-6 hover:shadow-md transition-shadow">
    {/* Header */}
    <div className="flex items-start justify-between mb-4">
      <div>
        <h3 className="text-lg font-semibold text-gray-900">
          {project.name}
        </h3>
        <p className="text-sm text-gray-500 mt-1">
          {project.key}
        </p>
      </div>
      <StatusBadge status={project.status} />
    </div>
    
    {/* Description */}
    <p className="text-gray-600 text-sm mb-4 line-clamp-2">
      {project.description}
    </p>
    
    {/* Progress */}
    <div className="mb-4">
      <div className="flex justify-between text-sm mb-1">
        <span>Progress</span>
        <span>{project.completedIssues}/{project.totalIssues}</span>
      </div>
      <ProgressBar 
        value={project.completedIssues} 
        max={project.totalIssues} 
      />
    </div>
    
    {/* Footer */}
    <div className="flex items-center justify-between">
      <div className="flex items-center space-x-2">
        <Avatar src={project.owner.avatar} size="sm" />
        <span className="text-sm text-gray-600">
          {project.owner.name}
        </span>
      </div>
      <div className="flex items-center text-sm text-gray-500">
        <Users size={16} className="mr-1" />
        {project.memberCount}
      </div>
    </div>
  </div>
);
```

### 4.3 상호작용 요소
- **카드 호버**: 그림자 효과로 인터랙션 표시
- **프로젝트 생성**: 모달 또는 새 페이지로 이동
- **필터링**: 실시간 검색 및 필터 적용
- **정렬**: 이름, 생성일, 업데이트일, 진행률 기준

## 5. 칸반 보드 페이지

### 5.1 보드 레이아웃
```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              Board Header                                   │
│  📋 Project Alpha    ➕ Add Issue   🔍 Filter   👥 Assignee   ⚙️ Settings │
├─────────────────────────────────────────────────────────────────────────────┤
│                             Kanban Columns                                  │
│ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│ │📝 TODO (5)  │ │⚡IN PROGRESS│ │👀 REVIEW (2)│ │✅ DONE (8)  │           │
│ │─────────────│ │    (3)      │ │─────────────│ │─────────────│           │
│ │             │ │─────────────│ │             │ │             │           │
│ │ ┌─────────┐ │ │             │ │ ┌─────────┐ │ │ ┌─────────┐ │           │
│ │ │Issue #1 │ │ │ ┌─────────┐ │ │ │Issue #5 │ │ │ │Issue #6 │ │           │
│ │ │🏷️Bug    │ │ │ │Issue #3 │ │ │ │🏷️Feature│ │ │ │🏷️Task  │ │           │
│ │ │🔥High   │ │ │ │🏷️Task   │ │ │ │📊Medium │ │ │ │📊Low    │ │           │
│ │ │👤John   │ │ │ │🔥High   │ │ │ │👤Mike   │ │ │ │👤Sarah  │ │           │
│ │ └─────────┘ │ │ │👤Bob    │ │ │ └─────────┘ │ │ └─────────┘ │           │
│ │             │ │ └─────────┘ │ │             │ │             │           │
│ │ ┌─────────┐ │ │             │ │             │ │ ┌─────────┐ │           │
│ │ │Issue #2 │ │ │ ┌─────────┐ │ │             │ │ │Issue #7 │ │           │
│ │ │🏷️Feature│ │ │ │Issue #4 │ │ │             │ │ │🏷️Bug   │ │           │
│ │ │📊Medium │ │ │ │🏷️Bug    │ │ │             │ │ │🔥High   │ │           │
│ │ │👤Jane   │ │ │ │📊Medium │ │ │             │ │ │👤John   │ │           │
│ │ └─────────┘ │ │ │👤Alice  │ │ │             │ │ └─────────┘ │           │
│ │             │ │ └─────────┘ │ │             │ │             │           │
│ │             │ │             │ │             │ │ ...more     │           │
│ │             │ │             │ │             │ │             │           │
│ │ ➕Add Issue │ │             │ │             │ │             │           │
│ └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘           │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 5.2 이슈 카드 디자인
```typescript
const IssueCard = ({ issue, isDragging }: IssueCardProps) => (
  <div 
    className={`
      bg-white border border-gray-200 rounded-lg p-3 mb-3 cursor-pointer
      hover:shadow-md transition-shadow
      ${isDragging ? 'opacity-50 rotate-2' : ''}
    `}
    draggable
    onDragStart={() => setDraggedIssue(issue)}
  >
    {/* Header */}
    <div className="flex items-start justify-between mb-2">
      <span className="text-xs font-medium text-gray-500">
        {issue.issueNumber}
      </span>
      <PriorityIcon priority={issue.priority} />
    </div>
    
    {/* Title */}
    <h4 className="text-sm font-medium text-gray-900 mb-2 line-clamp-2">
      {issue.title}
    </h4>
    
    {/* Labels */}
    <div className="flex flex-wrap gap-1 mb-3">
      {issue.labels.map(label => (
        <Label key={label.id} color={label.color}>
          {label.name}
        </Label>
      ))}
    </div>
    
    {/* Footer */}
    <div className="flex items-center justify-between">
      <div className="flex items-center space-x-2">
        {issue.subtasks.length > 0 && (
          <div className="flex items-center text-xs text-gray-500">
            <CheckSquare size={12} className="mr-1" />
            {issue.completedSubtasks}/{issue.subtasks.length}
          </div>
        )}
        {issue.comments.length > 0 && (
          <div className="flex items-center text-xs text-gray-500">
            <MessageCircle size={12} className="mr-1" />
            {issue.comments.length}
          </div>
        )}
        {issue.attachments.length > 0 && (
          <div className="flex items-center text-xs text-gray-500">
            <Paperclip size={12} className="mr-1" />
            {issue.attachments.length}
          </div>
        )}
      </div>
      
      <Avatar 
        src={issue.assignee?.avatar} 
        name={issue.assignee?.name}
        size="xs"
      />
    </div>
  </div>
);
```

### 5.3 드래그 앤 드롭 기능
```typescript
const KanbanColumn = ({ column, issues }: KanbanColumnProps) => {
  const [{ isOver }, drop] = useDrop({
    accept: 'issue',
    drop: (item: Issue) => {
      moveIssue(item.id, item.status, column.id);
    },
    collect: (monitor) => ({
      isOver: !!monitor.isOver(),
    }),
  });

  return (
    <div 
      ref={drop}
      className={`
        bg-gray-50 rounded-lg p-4 min-h-[600px]
        ${isOver ? 'bg-blue-50 border-2 border-blue-300 border-dashed' : ''}
      `}
    >
      <div className="flex items-center justify-between mb-4">
        <h3 className="font-semibold text-gray-900">
          {column.title} ({issues.length})
        </h3>
        <button className="text-gray-400 hover:text-gray-600">
          ⋯
        </button>
      </div>
      
      <div className="space-y-3">
        {issues.map(issue => (
          <IssueCard key={issue.id} issue={issue} />
        ))}
      </div>
      
      <button className="w-full mt-4 p-2 text-gray-500 border-2 border-dashed border-gray-300 rounded-lg hover:border-gray-400 hover:text-gray-600">
        ➕ Add Issue
      </button>
    </div>
  );
};
```

## 6. 이슈 상세 모달

### 6.1 모달 레이아웃
```
┌─────────────────────────────────────────────────────────────────────────┐
│                              Modal Header                               │
│  🏷️ Story  📝 Implement user authentication system  PROJ-123  ❌      │
├─────────────────────────────────────┬───────────────────────────────────┤
│              Left Panel             │           Right Panel            │
│                                     │                                   │
│ ┌─────────────────────────────────┐ │ ┌─────────────────────────────┐   │
│ │          Description            │ │ │           Status            │   │
│ │ ─────────────────────────────── │ │ │ ┌─────────────────────────┐ │   │
│ │                                 │ │ │ │     🟡 In Progress     │ │   │
│ │ ## Requirements                 │ │ │ └─────────────────────────┘ │   │
│ │ - JWT-based authentication      │ │ │                             │   │
│ │ - Email/password login          │ │ │          Assignee           │   │
│ │ - Social login integration      │ │ │ ┌─────────────────────────┐ │   │
│ │                                 │ │ │ │  👤 John Doe           │ │   │
│ │ ## Acceptance Criteria          │ │ │ └─────────────────────────┘ │   │
│ │ 1. User can register            │ │ │                             │   │
│ │ 2. User can login               │ │ │         Due Date            │   │
│ │ 3. User can reset password      │ │ │ ┌─────────────────────────┐ │   │
│ │                                 │ │ │ │     📅 Jan 30, 2024    │ │   │
│ └─────────────────────────────────┘ │ │ └─────────────────────────┘ │   │
│                                     │ │                             │   │
│ ┌─────────────────────────────────┐ │ │         Priority            │   │
│ │         Attachments             │ │ │ ┌─────────────────────────┐ │   │
│ │ ─────────────────────────────── │ │ │ │       🔥 High          │ │   │
│ │ 📎 login-mockup.png    🗑️      │ │ │ └─────────────────────────┘ │   │
│ │ 📎 auth-flow-diagram.pdf 🗑️    │ │ │                             │   │
│ │ ➕ Add attachment               │ │ │          Labels             │   │
│ └─────────────────────────────────┘ │ │ ┌─────────────────────────┐ │   │
│                                     │ │ │ 🏷️ Backend  🏷️ Security │ │   │
│ ┌─────────────────────────────────┐ │ │ │ 🏷️ Authentication        │ │   │
│ │           Comments              │ │ │ └─────────────────────────┘ │   │
│ │ ─────────────────────────────── │ │ │                             │   │
│ │                                 │ │ │       Story Points          │   │
│ │ 👤 Alice Brown (2h ago)         │ │ │ ┌─────────────────────────┐ │   │
│ │ I've started working on the     │ │ │ │          8              │ │   │
│ │ JWT implementation. ETA 2 days. │ │ │ └─────────────────────────┘ │   │
│ │                                 │ │ │                             │   │
│ │ 👤 John Doe (1d ago)            │ │ │         Subtasks            │   │
│ │ Great! Let me know if you need  │ │ │ ┌─────────────────────────┐ │   │
│ │ help with the frontend part.    │ │ │ │ ☑️ Setup JWT library    │ │   │
│ │                                 │ │ │ │ ⬜ Create login endpoint │ │   │
│ │ ┌─────────────────────────────┐ │ │ │ │ ⬜ Add password hashing  │ │   │
│ │ │ 💬 Add a comment...         │ │ │ │ ⬜ Implement logout      │ │   │
│ │ └─────────────────────────────┘ │ │ │ └─────────────────────────┘ │   │
│ └─────────────────────────────────┘ │ └─────────────────────────────┘   │
├─────────────────────────────────────┴───────────────────────────────────┤
│                              Modal Footer                               │
│  🕒 Activity Log    🗑️ Delete Issue    📤 Share    💾 Save Changes    │
└─────────────────────────────────────────────────────────────────────────┘
```

### 6.2 모달 컴포넌트 구조
```typescript
const IssueDetailModal = ({ issueId, isOpen, onClose }: IssueDetailModalProps) => {
  const { data: issue, isLoading } = useIssue(issueId);
  const updateIssue = useUpdateIssue();

  return (
    <Modal isOpen={isOpen} onClose={onClose} size="xl">
      <Modal.Header>
        <div className="flex items-center space-x-3">
          <IssueTypeIcon type={issue?.type} />
          <div>
            <h2 className="text-xl font-semibold">{issue?.title}</h2>
            <span className="text-sm text-gray-500">{issue?.issueNumber}</span>
          </div>
        </div>
      </Modal.Header>
      
      <Modal.Body>
        <div className="flex space-x-6">
          {/* Left Panel */}
          <div className="flex-1 space-y-6">
            <IssueDescription 
              description={issue?.description}
              onUpdate={(description) => updateIssue.mutate({ id: issueId, description })}
            />
            <IssueAttachments attachments={issue?.attachments} />
            <IssueComments 
              comments={issue?.comments}
              issueId={issueId}
            />
          </div>
          
          {/* Right Panel */}
          <div className="w-80 space-y-4">
            <IssueStatusSelector 
              status={issue?.status}
              onChange={(status) => updateIssue.mutate({ id: issueId, status })}
            />
            <IssueAssigneeSelector 
              assignee={issue?.assignee}
              onChange={(assigneeId) => updateIssue.mutate({ id: issueId, assigneeId })}
            />
            <IssuePrioritySelector 
              priority={issue?.priority}
              onChange={(priority) => updateIssue.mutate({ id: issueId, priority })}
            />
            <IssueLabels 
              labels={issue?.labels}
              onChange={(labelIds) => updateIssue.mutate({ id: issueId, labelIds })}
            />
            <IssueSubtasks 
              subtasks={issue?.subtasks}
              onUpdate={(subtasks) => updateIssue.mutate({ id: issueId, subtasks })}
            />
          </div>
        </div>
      </Modal.Body>
      
      <Modal.Footer>
        <div className="flex justify-between w-full">
          <div className="flex space-x-2">
            <Button variant="ghost" size="sm">
              🕒 Activity Log
            </Button>
          </div>
          <div className="flex space-x-2">
            <Button variant="ghost" size="sm">
              🗑️ Delete
            </Button>
            <Button variant="ghost" size="sm">
              📤 Share
            </Button>
            <Button variant="primary" size="sm">
              💾 Save Changes
            </Button>
          </div>
        </div>
      </Modal.Footer>
    </Modal>
  );
};
```

## 7. 개인 대시보드

### 7.1 대시보드 레이아웃
```
┌─────────────────────────────────────────────────────────────────────────┐
│                           Welcome Section                               │
│        👋 Good morning, John!     📅 January 15, 2024    ☀️ 22°C      │
├─────────────────────────────────────────────────────────────────────────┤
│                          Quick Stats Cards                             │
│ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐       │
│ │📋 My Issues │ │⚡In Progress│ │✅ Completed │ │🎯 Projects  │       │
│ │     15      │ │      5      │ │      8      │ │      4      │       │
│ │ 3 Due Today │ │  2 Overdue  │ │ This Week   │ │ 2 Need Rev. │       │
│ └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘       │
├─────────────────────────────────────────────────────────────────────────┤
│                            Main Content                                 │
│ ┌─────────────────────────────────┐ ┌─────────────────────────────────┐ │
│ │        Assigned to Me           │ │        Progress Chart           │ │
│ │ ─────────────────────────────── │ │ ─────────────────────────────── │ │
│ │                                 │ │     📈 Weekly Progress          │ │
│ │ 🔥 PROJ-123 - Auth System      │ │                                 │ │
│ │    Due tomorrow                 │ │    ████████████████████████     │ │
│ │                                 │ │    ████████████████████████     │ │
│ │ 📊 PROJ-124 - Dashboard UI     │ │    ████████████████████████     │ │
│ │    In Review                    │ │                                 │ │
│ │                                 │ │    Mon Tue Wed Thu Fri Sat Sun  │ │
│ │ ⚡ PROJ-125 - API Integration   │ │                                 │ │
│ │    In Progress                  │ │                                 │ │
│ │                                 │ │                                 │ │
│ │ 📝 PROJ-126 - Bug Fix          │ │                                 │ │
│ │    New                          │ │                                 │ │
│ │                                 │ │                                 │ │
│ │ 📄 View All (15)                │ │                                 │ │
│ │                                 │ │                                 │ │
│ └─────────────────────────────────┘ └─────────────────────────────────┘ │
│                                                                         │
│ ┌─────────────────────────────────┐ ┌─────────────────────────────────┐ │
│ │        Recent Activity          │ │      Upcoming Deadlines         │ │
│ │ ─────────────────────────────── │ │ ─────────────────────────────── │ │
│ │                                 │ │                                 │ │
│ │ • John commented on PROJ-45     │ │ 📅 Today (2 issues)             │ │
│ │   2 minutes ago                 │ │   🔥 PROJ-123 - Auth System    │ │
│ │                                 │ │   📊 PROJ-127 - Testing        │ │
│ │ • Issue PROJ-44 moved to Done  │ │                                 │ │
│ │   15 minutes ago                │ │ 📅 Tomorrow (1 issue)           │ │
│ │                                 │ │   ⚡ PROJ-128 - Deployment     │ │
│ │ • Sarah assigned PROJ-43 to you│ │                                 │ │
│ │   1 hour ago                    │ │ 📅 This Week (3 issues)         │ │
│ │                                 │ │   📝 PROJ-129 - Documentation  │ │
│ │ • New comment on PROJ-42        │ │   🎯 PROJ-130 - Performance    │ │
│ │   2 hours ago                   │ │   🔧 PROJ-131 - Refactoring    │ │
│ │                                 │ │                                 │ │
│ │ 📜 View All Activity            │ │ 📅 Next Week (2 issues)         │ │
│ │                                 │ │                                 │ │
│ └─────────────────────────────────┘ └─────────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────────────┤
│                           Bottom Section                                │
│ 📈 Productivity Insights    🏆 Achievements    🎯 Goals & Milestones   │
└─────────────────────────────────────────────────────────────────────────┘
```

### 7.2 위젯 컴포넌트
```typescript
const StatCard = ({ icon, title, value, subtitle, trend }: StatCardProps) => (
  <div className="bg-white rounded-lg border border-gray-200 p-6">
    <div className="flex items-center justify-between">
      <div>
        <div className="flex items-center space-x-2 mb-1">
          <span className="text-2xl">{icon}</span>
          <h3 className="text-sm font-medium text-gray-500">{title}</h3>
        </div>
        <p className="text-2xl font-bold text-gray-900">{value}</p>
        <p className="text-sm text-gray-600 mt-1">{subtitle}</p>
      </div>
      {trend && (
        <div className={`text-sm ${trend > 0 ? 'text-green-600' : 'text-red-600'}`}>
          {trend > 0 ? '↗️' : '↘️'} {Math.abs(trend)}%
        </div>
      )}
    </div>
  </div>
);

const IssueList = ({ issues, title }: IssueListProps) => (
  <div className="bg-white rounded-lg border border-gray-200 p-6">
    <div className="flex items-center justify-between mb-4">
      <h3 className="text-lg font-semibold text-gray-900">{title}</h3>
      <button className="text-sm text-blue-600 hover:text-blue-700">
        View All ({issues.length})
      </button>
    </div>
    
    <div className="space-y-3">
      {issues.slice(0, 4).map(issue => (
        <div key={issue.id} className="flex items-center space-x-3 p-2 hover:bg-gray-50 rounded">
          <PriorityIcon priority={issue.priority} />
          <div className="flex-1">
            <p className="text-sm font-medium text-gray-900">{issue.title}</p>
            <p className="text-xs text-gray-500">{issue.issueNumber}</p>
          </div>
          <StatusBadge status={issue.status} size="sm" />
        </div>
      ))}
    </div>
  </div>
);
```

## 8. 반응형 디자인 고려사항

### 8.1 브레이크포인트 정의
```css
/* Tailwind CSS 브레이크포인트 */
sm:  640px  /* 모바일 (세로) */
md:  768px  /* 태블릿 (세로) */
lg:  1024px /* 태블릿 (가로) / 작은 데스크탑 */
xl:  1280px /* 데스크탑 */
2xl: 1536px /* 큰 데스크탑 */
```

### 8.2 모바일 레이아웃 적응
```typescript
// 모바일에서 사이드바는 오버레이 형태로 변경
const MobileLayout = () => (
  <div className="lg:hidden">
    {/* 모바일 헤더 */}
    <header className="bg-white border-b px-4 py-3 flex items-center justify-between">
      <button onClick={() => setSidebarOpen(true)}>
        ☰
      </button>
      <Logo />
      <NotificationBell />
    </header>
    
    {/* 오버레이 사이드바 */}
    {sidebarOpen && (
      <div className="fixed inset-0 z-50">
        <div 
          className="absolute inset-0 bg-black opacity-50"
          onClick={() => setSidebarOpen(false)}
        />
        <aside className="absolute left-0 top-0 h-full w-64 bg-white shadow-xl">
          <Navigation />
        </aside>
      </div>
    )}
  </div>
);

// 칸반 보드의 모바일 적응
const MobileKanban = () => (
  <div className="lg:hidden">
    {/* 모바일에서는 탭 형태로 컬럼 전환 */}
    <div className="border-b">
      <nav className="flex space-x-8 px-4">
        {columns.map(column => (
          <button
            key={column.id}
            onClick={() => setActiveColumn(column.id)}
            className={`py-4 px-1 border-b-2 font-medium text-sm ${
              activeColumn === column.id 
                ? 'border-blue-500 text-blue-600' 
                : 'border-transparent text-gray-500'
            }`}
          >
            {column.title} ({column.issues.length})
          </button>
        ))}
      </nav>
    </div>
    
    {/* 선택된 컬럼만 표시 */}
    <div className="p-4">
      <KanbanColumn column={columns[activeColumn]} />
    </div>
  </div>
);
```

### 8.3 터치 친화적 인터페이스
```css
/* 터치 타겟 최소 크기 44px x 44px */
.touch-target {
  min-height: 44px;
  min-width: 44px;
}

/* 스크롤 영역에 모멘텀 스크롤 적용 */
.scroll-area {
  -webkit-overflow-scrolling: touch;
  overflow-y: auto;
}

/* 호버 효과는 터치 디바이스에서 제외 */
@media (hover: hover) {
  .hover-effect:hover {
    background-color: #f3f4f6;
  }
}
```

## 9. UX 플로우

### 9.1 사용자 온보딩 플로우
```
1. 회원가입 → 2. 이메일 인증 → 3. 프로필 설정 → 4. 첫 프로젝트 생성 → 5. 팀원 초대
```

### 9.2 일반적인 작업 플로우
```
프로젝트 작업 플로우:
로그인 → 대시보드 → 프로젝트 선택 → 칸반 보드 → 이슈 생성/수정 → 상태 변경

이슈 관리 플로우:
이슈 생성 → 담당자 지정 → 라벨/우선순위 설정 → 진행 → 리뷰 → 완료

협업 플로우:
댓글 작성 → 멘션 → 알림 → 실시간 업데이트 → 피드백 → 해결
```

### 9.3 오류 처리 UX
```typescript
// 네트워크 오류 처리
const ErrorFallback = ({ error, resetErrorBoundary }: ErrorFallbackProps) => (
  <div className="min-h-screen flex items-center justify-center bg-gray-50">
    <div className="max-w-md w-full bg-white rounded-lg shadow-md p-6 text-center">
      <div className="text-6xl mb-4">😵</div>
      <h2 className="text-xl font-semibold text-gray-900 mb-2">
        Something went wrong
      </h2>
      <p className="text-gray-600 mb-4">
        Don't worry, we're working on fixing this issue.
      </p>
      <div className="space-y-2">
        <button 
          onClick={resetErrorBoundary}
          className="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700"
        >
          Try Again
        </button>
        <button 
          onClick={() => window.location.href = '/'}
          className="w-full bg-gray-100 text-gray-700 py-2 px-4 rounded-md hover:bg-gray-200"
        >
          Go Home
        </button>
      </div>
    </div>
  </div>
);

// 로딩 상태 스켈레톤
const IssueSkeleton = () => (
  <div className="bg-white border border-gray-200 rounded-lg p-4 animate-pulse">
    <div className="flex items-start justify-between mb-3">
      <div className="h-4 bg-gray-200 rounded w-16"></div>
      <div className="h-4 bg-gray-200 rounded w-8"></div>
    </div>
    <div className="h-5 bg-gray-200 rounded w-3/4 mb-2"></div>
    <div className="h-4 bg-gray-200 rounded w-1/2 mb-3"></div>
    <div className="flex items-center justify-between">
      <div className="flex space-x-2">
        <div className="h-5 bg-gray-200 rounded w-12"></div>
        <div className="h-5 bg-gray-200 rounded w-12"></div>
      </div>
      <div className="h-6 w-6 bg-gray-200 rounded-full"></div>
    </div>
  </div>
);
```

이러한 UI 설계를 통해 직관적이고 효율적인 사용자 경험을 제공하는 프로젝트 관리 시스템을 구축할 수 있습니다. 