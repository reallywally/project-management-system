# React 프론트엔드 아키텍처

## 1. 프로젝트 폴더 구조

```
src/
├── api/                        # API 관련 모듈
│   ├── httpClient.ts          # Axios 인스턴스 및 인터셉터
│   ├── endpoints.ts           # API 엔드포인트 상수
│   └── services/              # API 서비스 계층
│       ├── authService.ts
│       ├── projectService.ts
│       ├── issueService.ts
│       ├── commentService.ts
│       └── notificationService.ts
├── components/                # 재사용 가능한 컴포넌트
│   ├── ui/                   # 기본 UI 컴포넌트
│   │   ├── Button.tsx
│   │   ├── Input.tsx
│   │   ├── Modal.tsx
│   │   ├── Dropdown.tsx
│   │   ├── Badge.tsx
│   │   └── index.ts
│   ├── common/               # 공통 컴포넌트
│   │   ├── Header.tsx
│   │   ├── Sidebar.tsx
│   │   ├── Layout.tsx
│   │   ├── LoadingSpinner.tsx
│   │   ├── ErrorBoundary.tsx
│   │   └── ProtectedRoute.tsx
│   ├── forms/                # 폼 관련 컴포넌트
│   │   ├── LoginForm.tsx
│   │   ├── ProjectForm.tsx
│   │   ├── IssueForm.tsx
│   │   └── CommentForm.tsx
│   └── features/             # 기능별 컴포넌트
│       ├── auth/
│       ├── projects/
│       ├── issues/
│       ├── kanban/
│       ├── comments/
│       ├── notifications/
│       └── dashboard/
├── pages/                    # 페이지 컴포넌트
│   ├── auth/
│   │   ├── LoginPage.tsx
│   │   ├── RegisterPage.tsx
│   │   └── ForgotPasswordPage.tsx
│   ├── dashboard/
│   │   ├── PersonalDashboard.tsx
│   │   └── ProjectDashboard.tsx
│   ├── projects/
│   │   ├── ProjectListPage.tsx
│   │   ├── ProjectDetailPage.tsx
│   │   └── ProjectSettingsPage.tsx
│   ├── issues/
│   │   ├── IssueListPage.tsx
│   │   ├── IssueDetailPage.tsx
│   │   └── KanbanBoardPage.tsx
│   └── profile/
│       └── ProfilePage.tsx
├── hooks/                    # 커스텀 훅
│   ├── useAuth.ts
│   ├── useDebounce.ts
│   ├── useLocalStorage.ts
│   ├── useWebSocket.ts
│   └── queries/              # React Query 훅
│       ├── useAuthQueries.ts
│       ├── useProjectQueries.ts
│       ├── useIssueQueries.ts
│       └── useNotificationQueries.ts
├── store/                    # Zustand 스토어
│   ├── authStore.ts
│   ├── uiStore.ts
│   ├── notificationStore.ts
│   └── kanbanStore.ts
├── utils/                    # 유틸리티 함수
│   ├── constants.ts
│   ├── helpers.ts
│   ├── validators.ts
│   ├── formatters.ts
│   ├── tokenManager.ts
│   └── webSocketManager.ts
├── types/                    # TypeScript 타입 정의
│   ├── api.ts
│   ├── auth.ts
│   ├── project.ts
│   ├── issue.ts
│   ├── comment.ts
│   └── notification.ts
├── styles/                   # 스타일 관련
│   ├── globals.css
│   ├── components.css
│   └── tailwind.css
├── assets/                   # 정적 자원
│   ├── images/
│   ├── icons/
│   └── fonts/
├── contexts/                 # React Context
│   ├── AuthContext.tsx
│   ├── ThemeContext.tsx
│   └── WebSocketContext.tsx
├── config/                   # 설정 파일
│   ├── env.ts
│   ├── queryClient.ts
│   └── routes.ts
├── App.tsx
├── main.tsx
└── vite-env.d.ts
```

## 2. 상태 관리 전략

### 2.1 상태 분류

본 프로젝트에서는 상태를 다음과 같이 분류하여 관리합니다:

1. **서버 상태 (Server State)**: React Query로 관리
   - API에서 가져오는 데이터
   - 캐싱, 동기화, 백그라운드 업데이트 필요

2. **클라이언트 상태 (Client State)**: Zustand로 관리
   - 로그인 정보, UI 상태, 사용자 설정
   - 컴포넌트 간 공유가 필요한 상태

3. **로컬 상태 (Local State)**: useState로 관리
   - 컴포넌트 내부에서만 사용되는 상태

### 2.2 Zustand 스토어 설계

#### Auth Store
```typescript
// store/authStore.ts
import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { TokenManager } from '../utils/tokenManager';
import { User } from '../types/auth';

interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
}

interface AuthActions {
  setUser: (user: User | null) => void;
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
  login: (email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  clearError: () => void;
}

type AuthStore = AuthState & AuthActions;

export const useAuthStore = create<AuthStore>()(
  persist(
    (set, get) => ({
      // State
      user: null,
      isAuthenticated: false,
      isLoading: false,
      error: null,

      // Actions
      setUser: (user) => set({ 
        user, 
        isAuthenticated: !!user,
        error: null 
      }),

      setLoading: (isLoading) => set({ isLoading }),

      setError: (error) => set({ error }),

      clearError: () => set({ error: null }),

      login: async (email, password) => {
        set({ isLoading: true, error: null });
        
        try {
          const response = await authService.login(email, password);
          const { accessToken, refreshToken, user } = response.data;
          
          TokenManager.setTokens(accessToken, refreshToken);
          set({ 
            user, 
            isAuthenticated: true, 
            isLoading: false,
            error: null
          });
        } catch (error: any) {
          set({ 
            error: error.response?.data?.message || 'Login failed',
            isLoading: false
          });
          throw error;
        }
      },

      logout: async () => {
        try {
          await authService.logout();
        } catch (error) {
          console.error('Logout error:', error);
        } finally {
          TokenManager.clearTokens();
          set({ 
            user: null, 
            isAuthenticated: false,
            error: null
          });
        }
      }
    }),
    {
      name: 'auth-store',
      partialize: (state) => ({ 
        user: state.user,
        isAuthenticated: state.isAuthenticated
      })
    }
  )
);
```

#### UI Store
```typescript
// store/uiStore.ts
import { create } from 'zustand';

interface UIState {
  sidebarCollapsed: boolean;
  theme: 'light' | 'dark';
  currentProject: string | null;
  modals: {
    issueDetail: { isOpen: boolean; issueId: string | null };
    createIssue: { isOpen: boolean; projectId: string | null };
    projectSettings: { isOpen: boolean; projectId: string | null };
  };
}

interface UIActions {
  toggleSidebar: () => void;
  setSidebarCollapsed: (collapsed: boolean) => void;
  setTheme: (theme: 'light' | 'dark') => void;
  setCurrentProject: (projectId: string | null) => void;
  openModal: (modal: keyof UIState['modals'], data?: any) => void;
  closeModal: (modal: keyof UIState['modals']) => void;
}

type UIStore = UIState & UIActions;

export const useUIStore = create<UIStore>((set) => ({
  // State
  sidebarCollapsed: false,
  theme: 'light',
  currentProject: null,
  modals: {
    issueDetail: { isOpen: false, issueId: null },
    createIssue: { isOpen: false, projectId: null },
    projectSettings: { isOpen: false, projectId: null }
  },

  // Actions
  toggleSidebar: () => set((state) => ({ 
    sidebarCollapsed: !state.sidebarCollapsed 
  })),

  setSidebarCollapsed: (collapsed) => set({ sidebarCollapsed: collapsed }),

  setTheme: (theme) => set({ theme }),

  setCurrentProject: (projectId) => set({ currentProject: projectId }),

  openModal: (modalName, data = {}) => set((state) => ({
    modals: {
      ...state.modals,
      [modalName]: { isOpen: true, ...data }
    }
  })),

  closeModal: (modalName) => set((state) => ({
    modals: {
      ...state.modals,
      [modalName]: { isOpen: false, issueId: null, projectId: null }
    }
  }))
}));
```

#### Kanban Store
```typescript
// store/kanbanStore.ts
import { create } from 'zustand';
import { Issue } from '../types/issue';

interface KanbanState {
  columns: {
    [key: string]: {
      id: string;
      title: string;
      issues: Issue[];
    };
  };
  draggedIssue: Issue | null;
  isLoading: boolean;
}

interface KanbanActions {
  setColumns: (columns: any) => void;
  moveIssue: (issueId: string, fromColumn: string, toColumn: string, position: number) => void;
  setDraggedIssue: (issue: Issue | null) => void;
  addIssue: (columnId: string, issue: Issue) => void;
  updateIssue: (issueId: string, updates: Partial<Issue>) => void;
  removeIssue: (columnId: string, issueId: string) => void;
  setLoading: (loading: boolean) => void;
}

type KanbanStore = KanbanState & KanbanActions;

export const useKanbanStore = create<KanbanStore>((set, get) => ({
  // State
  columns: {},
  draggedIssue: null,
  isLoading: false,

  // Actions
  setColumns: (columns) => set({ columns }),

  setLoading: (isLoading) => set({ isLoading }),

  setDraggedIssue: (draggedIssue) => set({ draggedIssue }),

  moveIssue: (issueId, fromColumn, toColumn, position) => {
    const state = get();
    const issue = state.columns[fromColumn]?.issues.find(i => i.id === issueId);
    
    if (!issue) return;

    set({
      columns: {
        ...state.columns,
        [fromColumn]: {
          ...state.columns[fromColumn],
          issues: state.columns[fromColumn].issues.filter(i => i.id !== issueId)
        },
        [toColumn]: {
          ...state.columns[toColumn],
          issues: [
            ...state.columns[toColumn].issues.slice(0, position),
            { ...issue, status: toColumn },
            ...state.columns[toColumn].issues.slice(position)
          ]
        }
      }
    });
  },

  addIssue: (columnId, issue) => {
    const state = get();
    set({
      columns: {
        ...state.columns,
        [columnId]: {
          ...state.columns[columnId],
          issues: [...state.columns[columnId].issues, issue]
        }
      }
    });
  },

  updateIssue: (issueId, updates) => {
    const state = get();
    const updatedColumns = { ...state.columns };
    
    Object.keys(updatedColumns).forEach(columnId => {
      updatedColumns[columnId] = {
        ...updatedColumns[columnId],
        issues: updatedColumns[columnId].issues.map(issue =>
          issue.id === issueId ? { ...issue, ...updates } : issue
        )
      };
    });
    
    set({ columns: updatedColumns });
  },

  removeIssue: (columnId, issueId) => {
    const state = get();
    set({
      columns: {
        ...state.columns,
        [columnId]: {
          ...state.columns[columnId],
          issues: state.columns[columnId].issues.filter(i => i.id !== issueId)
        }
      }
    });
  }
}));
```

### 2.3 React Query 구성

#### Query Client 설정
```typescript
// config/queryClient.ts
import { QueryClient } from '@tanstack/react-query';

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 1000 * 60 * 5, // 5분
      gcTime: 1000 * 60 * 30,   // 30분 (이전의 cacheTime)
      retry: (failureCount, error: any) => {
        // 401, 403 에러는 재시도하지 않음
        if (error?.response?.status === 401 || error?.response?.status === 403) {
          return false;
        }
        return failureCount < 3;
      },
      refetchOnWindowFocus: false,
      refetchOnMount: true,
    },
    mutations: {
      retry: 1,
    }
  }
});
```

#### 프로젝트 관련 쿼리
```typescript
// hooks/queries/useProjectQueries.ts
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { projectService } from '../../api/services/projectService';
import { Project, CreateProjectRequest, UpdateProjectRequest } from '../../types/project';

export const PROJECT_QUERY_KEYS = {
  all: ['projects'] as const,
  lists: () => [...PROJECT_QUERY_KEYS.all, 'list'] as const,
  list: (filters: any) => [...PROJECT_QUERY_KEYS.lists(), filters] as const,
  details: () => [...PROJECT_QUERY_KEYS.all, 'detail'] as const,
  detail: (id: string) => [...PROJECT_QUERY_KEYS.details(), id] as const,
};

// 프로젝트 목록 조회
export const useProjects = (filters: any = {}) => {
  return useQuery({
    queryKey: PROJECT_QUERY_KEYS.list(filters),
    queryFn: () => projectService.getProjects(filters),
    select: (data) => data.data,
  });
};

// 프로젝트 상세 조회
export const useProject = (projectId: string) => {
  return useQuery({
    queryKey: PROJECT_QUERY_KEYS.detail(projectId),
    queryFn: () => projectService.getProject(projectId),
    select: (data) => data.data,
    enabled: !!projectId,
  });
};

// 프로젝트 생성
export const useCreateProject = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateProjectRequest) => projectService.createProject(data),
    onSuccess: (response) => {
      // 프로젝트 목록 캐시 무효화
      queryClient.invalidateQueries({ queryKey: PROJECT_QUERY_KEYS.lists() });
      
      // 새로 생성된 프로젝트를 캐시에 추가
      queryClient.setQueryData(
        PROJECT_QUERY_KEYS.detail(response.data.id),
        response
      );
    },
  });
};

// 프로젝트 수정
export const useUpdateProject = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ projectId, data }: { projectId: string; data: UpdateProjectRequest }) =>
      projectService.updateProject(projectId, data),
    onSuccess: (response, { projectId }) => {
      // 관련 캐시 무효화
      queryClient.invalidateQueries({ queryKey: PROJECT_QUERY_KEYS.lists() });
      queryClient.invalidateQueries({ queryKey: PROJECT_QUERY_KEYS.detail(projectId) });
    },
  });
};

// 프로젝트 삭제
export const useDeleteProject = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (projectId: string) => projectService.deleteProject(projectId),
    onSuccess: (_, projectId) => {
      // 캐시에서 제거
      queryClient.removeQueries({ queryKey: PROJECT_QUERY_KEYS.detail(projectId) });
      queryClient.invalidateQueries({ queryKey: PROJECT_QUERY_KEYS.lists() });
    },
  });
};
```

#### 이슈 관련 쿼리
```typescript
// hooks/queries/useIssueQueries.ts
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { issueService } from '../../api/services/issueService';
import { useKanbanStore } from '../../store/kanbanStore';

export const ISSUE_QUERY_KEYS = {
  all: ['issues'] as const,
  lists: () => [...ISSUE_QUERY_KEYS.all, 'list'] as const,
  list: (projectId: string, filters: any) => [...ISSUE_QUERY_KEYS.lists(), projectId, filters] as const,
  details: () => [...ISSUE_QUERY_KEYS.all, 'detail'] as const,
  detail: (id: string) => [...ISSUE_QUERY_KEYS.details(), id] as const,
  kanban: (projectId: string) => [...ISSUE_QUERY_KEYS.all, 'kanban', projectId] as const,
};

// 이슈 목록 조회
export const useIssues = (projectId: string, filters: any = {}) => {
  return useQuery({
    queryKey: ISSUE_QUERY_KEYS.list(projectId, filters),
    queryFn: () => issueService.getIssues(projectId, filters),
    select: (data) => data.data,
    enabled: !!projectId,
  });
};

// 칸반 보드용 이슈 조회
export const useKanbanIssues = (projectId: string) => {
  const { setColumns, setLoading } = useKanbanStore();

  return useQuery({
    queryKey: ISSUE_QUERY_KEYS.kanban(projectId),
    queryFn: () => issueService.getKanbanIssues(projectId),
    select: (data) => data.data,
    enabled: !!projectId,
    onSuccess: (data) => {
      setColumns(data.columns);
      setLoading(false);
    },
    onError: () => {
      setLoading(false);
    },
  });
};

// 이슈 상태 변경 (드래그 앤 드롭)
export const useUpdateIssueStatus = () => {
  const queryClient = useQueryClient();
  const { moveIssue } = useKanbanStore();

  return useMutation({
    mutationFn: ({ 
      issueId, 
      status, 
      position 
    }: { 
      issueId: string; 
      status: string; 
      position: number; 
    }) => issueService.updateIssueStatus(issueId, { status, position }),
    
    onMutate: async ({ issueId, status, position }) => {
      // 낙관적 업데이트를 위해 현재 이슈 정보 가져오기
      const issueDetail = queryClient.getQueryData(ISSUE_QUERY_KEYS.detail(issueId));
      
      if (issueDetail) {
        // Zustand store에서 UI 즉시 업데이트
        const currentStatus = (issueDetail as any).status;
        moveIssue(issueId, currentStatus, status, position);
      }
      
      return { previousIssue: issueDetail };
    },
    
    onError: (error, { issueId }, context) => {
      // 에러 발생 시 이전 상태로 롤백
      if (context?.previousIssue) {
        const issue = context.previousIssue as any;
        moveIssue(issueId, issue.status, issue.status, issue.position);
      }
    },
    
    onSettled: (data, error, { issueId }) => {
      // 관련 쿼리 무효화
      queryClient.invalidateQueries({ queryKey: ISSUE_QUERY_KEYS.detail(issueId) });
    },
  });
};
```

### 2.4 커스텀 훅 예시

#### useDebounce 훅
```typescript
// hooks/useDebounce.ts
import { useState, useEffect } from 'react';

export function useDebounce<T>(value: T, delay: number): T {
  const [debouncedValue, setDebouncedValue] = useState<T>(value);

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    return () => {
      clearTimeout(handler);
    };
  }, [value, delay]);

  return debouncedValue;
}

// 사용 예시
export const useSearchIssues = (projectId: string) => {
  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearchTerm = useDebounce(searchTerm, 300);

  const { data: issues, isLoading } = useIssues(projectId, {
    search: debouncedSearchTerm,
  });

  return {
    issues,
    isLoading,
    searchTerm,
    setSearchTerm,
  };
};
```

#### useWebSocket 훅
```typescript
// hooks/useWebSocket.ts
import { useEffect, useRef } from 'react';
import { useAuthStore } from '../store/authStore';
import { useNotificationStore } from '../store/notificationStore';
import { WebSocketManager } from '../utils/webSocketManager';

export const useWebSocket = () => {
  const wsRef = useRef<WebSocketManager | null>(null);
  const { isAuthenticated } = useAuthStore();
  const { addNotification } = useNotificationStore();

  useEffect(() => {
    if (isAuthenticated && !wsRef.current) {
      wsRef.current = new WebSocketManager();
      
      wsRef.current.connect();
      
      wsRef.current.onMessage((message) => {
        switch (message.type) {
          case 'ISSUE_UPDATED':
            // 이슈 업데이트 알림
            addNotification({
              type: 'info',
              title: 'Issue Updated',
              message: `${message.data.issueNumber} has been updated`,
            });
            break;
          case 'COMMENT_ADDED':
            // 댓글 추가 알림
            addNotification({
              type: 'info',
              title: 'New Comment',
              message: `New comment on ${message.data.issueNumber}`,
            });
            break;
        }
      });
    }

    return () => {
      if (wsRef.current) {
        wsRef.current.disconnect();
        wsRef.current = null;
      }
    };
  }, [isAuthenticated, addNotification]);

  return {
    sendMessage: (message: any) => wsRef.current?.sendMessage(message),
    isConnected: wsRef.current?.isConnected() || false,
  };
};
```

## 3. 컴포넌트 설계 패턴

### 3.1 Compound Component 패턴

```typescript
// components/ui/Modal.tsx
import React, { createContext, useContext, useState } from 'react';

interface ModalContextType {
  isOpen: boolean;
  onClose: () => void;
}

const ModalContext = createContext<ModalContextType | null>(null);

const useModalContext = () => {
  const context = useContext(ModalContext);
  if (!context) {
    throw new Error('Modal components must be used within Modal');
  }
  return context;
};

interface ModalProps {
  children: React.ReactNode;
  isOpen: boolean;
  onClose: () => void;
}

const Modal = ({ children, isOpen, onClose }: ModalProps) => {
  if (!isOpen) return null;

  return (
    <ModalContext.Provider value={{ isOpen, onClose }}>
      <div className="fixed inset-0 z-50 flex items-center justify-center">
        <div 
          className="fixed inset-0 bg-black bg-opacity-50"
          onClick={onClose}
        />
        <div className="relative bg-white rounded-lg shadow-xl max-w-md w-full mx-4">
          {children}
        </div>
      </div>
    </ModalContext.Provider>
  );
};

const ModalHeader = ({ children }: { children: React.ReactNode }) => {
  const { onClose } = useModalContext();
  
  return (
    <div className="flex items-center justify-between p-6 border-b">
      <h2 className="text-xl font-semibold">{children}</h2>
      <button 
        onClick={onClose}
        className="text-gray-400 hover:text-gray-600"
      >
        ×
      </button>
    </div>
  );
};

const ModalBody = ({ children }: { children: React.ReactNode }) => (
  <div className="p-6">{children}</div>
);

const ModalFooter = ({ children }: { children: React.ReactNode }) => (
  <div className="flex justify-end space-x-2 p-6 border-t">
    {children}
  </div>
);

Modal.Header = ModalHeader;
Modal.Body = ModalBody;
Modal.Footer = ModalFooter;

export { Modal };

// 사용 예시
const IssueDetailModal = () => {
  const { modals, closeModal } = useUIStore();
  const { data: issue } = useIssue(modals.issueDetail.issueId!);

  return (
    <Modal 
      isOpen={modals.issueDetail.isOpen} 
      onClose={() => closeModal('issueDetail')}
    >
      <Modal.Header>
        {issue?.title}
      </Modal.Header>
      <Modal.Body>
        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700">
              Description
            </label>
            <p className="mt-1 text-sm text-gray-900">{issue?.description}</p>
          </div>
        </div>
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={() => closeModal('issueDetail')}>
          Close
        </Button>
        <Button variant="primary">
          Edit
        </Button>
      </Modal.Footer>
    </Modal>
  );
};
```

### 3.2 Render Props 패턴

```typescript
// components/common/DataFetcher.tsx
interface DataFetcherProps<T> {
  queryKey: string[];
  queryFn: () => Promise<T>;
  children: (props: {
    data: T | undefined;
    isLoading: boolean;
    error: Error | null;
    refetch: () => void;
  }) => React.ReactNode;
}

export function DataFetcher<T>({ queryKey, queryFn, children }: DataFetcherProps<T>) {
  const { data, isLoading, error, refetch } = useQuery({
    queryKey,
    queryFn,
  });

  return (
    <>
      {children({ 
        data, 
        isLoading, 
        error: error as Error | null, 
        refetch 
      })}
    </>
  );
}

// 사용 예시
const ProjectList = () => (
  <DataFetcher
    queryKey={['projects']}
    queryFn={() => projectService.getProjects()}
  >
    {({ data, isLoading, error, refetch }) => {
      if (isLoading) return <LoadingSpinner />;
      if (error) return <ErrorMessage error={error} onRetry={refetch} />;
      
      return (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {data?.map(project => (
            <ProjectCard key={project.id} project={project} />
          ))}
        </div>
      );
    }}
  </DataFetcher>
);
```

## 4. 성능 최적화 전략

### 4.1 React.memo와 useMemo 활용

```typescript
// components/features/issues/IssueCard.tsx
import React, { memo } from 'react';
import { Issue } from '../../../types/issue';

interface IssueCardProps {
  issue: Issue;
  onEdit: (issueId: string) => void;
  onDelete: (issueId: string) => void;
}

const IssueCard = memo(({ issue, onEdit, onDelete }: IssueCardProps) => {
  const handleEdit = useCallback(() => {
    onEdit(issue.id);
  }, [issue.id, onEdit]);

  const handleDelete = useCallback(() => {
    onDelete(issue.id);
  }, [issue.id, onDelete]);

  const priorityColor = useMemo(() => {
    switch (issue.priority) {
      case 'HIGHEST': return 'bg-red-500';
      case 'HIGH': return 'bg-orange-500';
      case 'MEDIUM': return 'bg-yellow-500';
      case 'LOW': return 'bg-green-500';
      case 'LOWEST': return 'bg-gray-500';
      default: return 'bg-gray-500';
    }
  }, [issue.priority]);

  return (
    <div className="bg-white p-4 rounded-lg shadow-sm border hover:shadow-md transition-shadow">
      <div className="flex items-start justify-between">
        <div className="flex-1">
          <h3 className="font-medium text-gray-900 truncate">
            {issue.title}
          </h3>
          <p className="text-sm text-gray-500 mt-1 line-clamp-2">
            {issue.description}
          </p>
        </div>
        <div className={`w-3 h-3 rounded-full ${priorityColor} ml-2 flex-shrink-0`} />
      </div>
      
      <div className="flex items-center justify-between mt-4">
        <div className="flex items-center space-x-2">
          <Badge variant={issue.type}>{issue.type}</Badge>
          <span className="text-xs text-gray-500">{issue.issueNumber}</span>
        </div>
        
        <div className="flex items-center space-x-1">
          <button 
            onClick={handleEdit}
            className="text-blue-600 hover:text-blue-800 text-sm"
          >
            Edit
          </button>
          <button 
            onClick={handleDelete}
            className="text-red-600 hover:text-red-800 text-sm"
          >
            Delete
          </button>
        </div>
      </div>
    </div>
  );
});

IssueCard.displayName = 'IssueCard';

export { IssueCard };
```

### 4.2 코드 분할과 Lazy Loading

```typescript
// pages/routes.tsx
import { lazy, Suspense } from 'react';
import { Routes, Route } from 'react-router-dom';
import { LoadingSpinner } from '../components/common/LoadingSpinner';

// Lazy load 페이지 컴포넌트
const ProjectListPage = lazy(() => import('./projects/ProjectListPage'));
const ProjectDetailPage = lazy(() => import('./projects/ProjectDetailPage'));
const KanbanBoardPage = lazy(() => import('./issues/KanbanBoardPage'));
const DashboardPage = lazy(() => import('./dashboard/PersonalDashboard'));

const AppRoutes = () => (
  <Suspense fallback={<LoadingSpinner />}>
    <Routes>
      <Route path="/dashboard" element={<DashboardPage />} />
      <Route path="/projects" element={<ProjectListPage />} />
      <Route path="/projects/:id" element={<ProjectDetailPage />} />
      <Route path="/projects/:id/kanban" element={<KanbanBoardPage />} />
    </Routes>
  </Suspense>
);

export { AppRoutes };
```

## 5. 에러 처리 및 로딩 상태

### 5.1 에러 바운더리

```typescript
// components/common/ErrorBoundary.tsx
import React, { Component, ReactNode } from 'react';

interface Props {
  children: ReactNode;
  fallback?: ReactNode;
}

interface State {
  hasError: boolean;
  error?: Error;
}

class ErrorBoundary extends Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error };
  }

  componentDidCatch(error: Error, errorInfo: any) {
    console.error('Error caught by boundary:', error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      return this.props.fallback || (
        <div className="min-h-screen flex items-center justify-center">
          <div className="text-center">
            <h2 className="text-2xl font-bold text-gray-900 mb-4">
              Something went wrong
            </h2>
            <p className="text-gray-600 mb-4">
              {this.state.error?.message || 'An unexpected error occurred'}
            </p>
            <button
              onClick={() => window.location.reload()}
              className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700"
            >
              Reload Page
            </button>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}

export { ErrorBoundary };
```

이러한 React 아키텍처를 통해 확장 가능하고 유지보수성이 높은 프론트엔드 애플리케이션을 구축할 수 있습니다. 