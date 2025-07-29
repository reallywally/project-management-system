import React, { useState } from 'react'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Progress } from '@/components/ui/progress'
import { Avatar } from '@/components/ui/avatar'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Tabs, TabsList, TabsTrigger, TabsContent } from '@/components/ui/tabs'
import LoginPage from '@/pages/auth/LoginPage'
import DashboardPage from '@/pages/DashboardPage'
import ProjectListPage from '@/pages/projects/ProjectListPage'
import KanbanBoardPage from '@/pages/projects/KanbanBoardPage'
import MainLayout from '@/components/layout/MainLayout'

function App() {
  const [activeTab, setActiveTab] = useState('overview')
  const [currentPage, setCurrentPage] = useState('login')

  const pages = [
    { id: 'login', label: '🔐 로그인', component: LoginPage },
    { id: 'dashboard', label: '📊 대시보드', component: DashboardPage },
    { id: 'projects', label: '📁 프로젝트 목록', component: ProjectListPage },
    { id: 'kanban', label: '📋 칸반 보드', component: KanbanBoardPage }
  ]

  const renderPageContent = () => {
    const selectedPage = pages.find(page => page.id === currentPage)
    if (!selectedPage) return null

    const PageComponent = selectedPage.component

    if (currentPage === 'login') {
      return <PageComponent />
    } else {
      return (
        <MainLayout>
          <PageComponent />
        </MainLayout>
      )
    }
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {activeTab === 'demo' ? (
        <div className="h-screen">
          {/* Page Navigation */}
          <div className="bg-white border-b shadow-sm p-4">
            <div className="max-w-6xl mx-auto">
              <div className="flex flex-wrap gap-2">
                {pages.map((page) => (
                  <Button
                    key={page.id}
                    variant={currentPage === page.id ? 'default' : 'outline'}
                    size="sm"
                    onClick={() => setCurrentPage(page.id)}
                  >
                    {page.label}
                  </Button>
                ))}
              </div>
            </div>
          </div>

          {/* Page Content */}
          <div className="h-[calc(100vh-73px)]">
            {renderPageContent()}
          </div>
        </div>
      ) : (
        <div className="p-8">
          <div className="max-w-6xl mx-auto space-y-8">
            {/* Header */}
            <div className="text-center">
              <div className="flex justify-center items-center mb-6">
                <div className="w-16 h-16 bg-primary rounded-lg flex items-center justify-center">
                  <span className="text-white text-2xl font-bold">🏢</span>
                </div>
              </div>
              <h1 className="text-4xl font-bold text-gray-900 mb-4">
                🎉 Project Management System
              </h1>
              <p className="text-lg text-gray-600 mb-6">
                Shadcn UI와 와이어프레임 기반으로 완성된 프로젝트 관리 시스템
              </p>
              <div className="flex justify-center space-x-4 mb-8">
                <Badge variant="success">✅ 모든 기능 구현 완료</Badge>
                <Badge variant="info">🎨 와이어프레임 기반 디자인</Badge>
                <Badge variant="default">📱 완전 반응형</Badge>
              </div>
            </div>

            {/* Navigation Tabs */}
            <div className="flex justify-center">
              <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full max-w-2xl">
                <TabsList className="grid w-full grid-cols-4">
                  <TabsTrigger value="overview">📊 개요</TabsTrigger>
                  <TabsTrigger value="components">🧩 컴포넌트</TabsTrigger>
                  <TabsTrigger value="features">✨ 기능</TabsTrigger>
                  <TabsTrigger value="demo">🚀 데모</TabsTrigger>
                </TabsList>

                {/* Overview Tab */}
                <TabsContent value="overview" className="mt-8">
                  <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    <Card className="hover:shadow-md transition-shadow">
                      <CardContent className="p-6">
                        <div className="flex items-center">
                          <div className="w-12 h-12 bg-blue-500 rounded-lg flex items-center justify-center">
                            <span className="text-white text-xl">🔐</span>
                          </div>
                          <div className="ml-4">
                            <h3 className="text-lg font-semibold">로그인 시스템</h3>
                            <p className="text-sm text-gray-600">JWT 기반 인증 및 소셜 로그인</p>
                          </div>
                        </div>
                      </CardContent>
                    </Card>

                    <Card className="hover:shadow-md transition-shadow">
                      <CardContent className="p-6">
                        <div className="flex items-center">
                          <div className="w-12 h-12 bg-green-500 rounded-lg flex items-center justify-center">
                            <span className="text-white text-xl">📊</span>
                          </div>
                          <div className="ml-4">
                            <h3 className="text-lg font-semibold">대시보드</h3>
                            <p className="text-sm text-gray-600">실시간 통계 및 진행률 추적</p>
                          </div>
                        </div>
                      </CardContent>
                    </Card>

                    <Card className="hover:shadow-md transition-shadow">
                      <CardContent className="p-6">
                        <div className="flex items-center">
                          <div className="w-12 h-12 bg-purple-500 rounded-lg flex items-center justify-center">
                            <span className="text-white text-xl">📁</span>
                          </div>
                          <div className="ml-4">
                            <h3 className="text-lg font-semibold">프로젝트 관리</h3>
                            <p className="text-sm text-gray-600">필터링, 정렬, 검색 기능</p>
                          </div>
                        </div>
                      </CardContent>
                    </Card>

                    <Card className="hover:shadow-md transition-shadow">
                      <CardContent className="p-6">
                        <div className="flex items-center">
                          <div className="w-12 h-12 bg-orange-500 rounded-lg flex items-center justify-center">
                            <span className="text-white text-xl">📋</span>
                          </div>
                          <div className="ml-4">
                            <h3 className="text-lg font-semibold">칸반 보드</h3>
                            <p className="text-sm text-gray-600">드래그 앤 드롭 이슈 관리</p>
                          </div>
                        </div>
                      </CardContent>
                    </Card>

                    <Card className="hover:shadow-md transition-shadow">
                      <CardContent className="p-6">
                        <div className="flex items-center">
                          <div className="w-12 h-12 bg-indigo-500 rounded-lg flex items-center justify-center">
                            <span className="text-white text-xl">🗂️</span>
                          </div>
                          <div className="ml-4">
                            <h3 className="text-lg font-semibold">이슈 상세</h3>
                            <p className="text-sm text-gray-600">풍부한 에디터 및 협업 기능</p>
                          </div>
                        </div>
                      </CardContent>
                    </Card>

                    <Card className="hover:shadow-md transition-shadow">
                      <CardContent className="p-6">
                        <div className="flex items-center">
                          <div className="w-12 h-12 bg-red-500 rounded-lg flex items-center justify-center">
                            <span className="text-white text-xl">📱</span>
                          </div>
                          <div className="ml-4">
                            <h3 className="text-lg font-semibold">반응형 디자인</h3>
                            <p className="text-sm text-gray-600">모든 디바이스 최적화</p>
                          </div>
                        </div>
                      </CardContent>
                    </Card>
                  </div>
                </TabsContent>

                {/* Components Tab */}
                <TabsContent value="components" className="mt-8">
                  <Card>
                    <CardHeader>
                      <CardTitle>Shadcn UI 컴포넌트 갤러리</CardTitle>
                    </CardHeader>
                    <CardContent className="space-y-8">
                      {/* Buttons */}
                      <div>
                        <h3 className="text-lg font-semibold mb-4">버튼 시스템</h3>
                        <div className="flex flex-wrap gap-3">
                          <Button>Primary</Button>
                          <Button variant="secondary">Secondary</Button>
                          <Button variant="outline">Outline</Button>
                          <Button variant="ghost">Ghost</Button>
                          <Button variant="destructive">Destructive</Button>
                        </div>
                      </div>

                      {/* Badges */}
                      <div>
                        <h3 className="text-lg font-semibold mb-4">상태 배지</h3>
                        <div className="flex flex-wrap gap-3">
                          <Badge>Default</Badge>
                          <Badge variant="secondary">Secondary</Badge>
                          <Badge variant="success">Success</Badge>
                          <Badge variant="warning">Warning</Badge>
                          <Badge variant="info">Info</Badge>
                        </div>
                      </div>

                      {/* Progress */}
                      <div>
                        <h3 className="text-lg font-semibold mb-4">진행률 표시</h3>
                        <div className="space-y-4 max-w-md">
                          <div>
                            <div className="flex justify-between text-sm mb-2">
                              <span>프로젝트 진행률</span>
                              <span>75%</span>
                            </div>
                            <Progress value={75} max={100} />
                          </div>
                          <div>
                            <div className="flex justify-between text-sm mb-2">
                              <span>이슈 완료율</span>
                              <span>45%</span>
                            </div>
                            <Progress value={45} max={100} />
                          </div>
                        </div>
                      </div>

                      {/* Avatars */}
                      <div>
                        <h3 className="text-lg font-semibold mb-4">사용자 아바타</h3>
                        <div className="flex items-center space-x-4">
                          <Avatar name="John Doe" size="xs" />
                          <Avatar name="Jane Smith" size="sm" />
                          <Avatar name="Bob Wilson" size="md" />
                          <Avatar name="Alice Brown" size="lg" />
                          <Avatar name="Charlie Davis" size="xl" />
                        </div>
                      </div>

                      {/* Forms */}
                      <div>
                        <h3 className="text-lg font-semibold mb-4">폼 요소</h3>
                        <div className="space-y-4 max-w-md">
                          <Input placeholder="검색어를 입력하세요..." />
                          <Input type="email" placeholder="이메일 주소" />
                          <div className="flex flex-wrap gap-2">
                            <Label variant="bug">🐛 Bug</Label>
                            <Label variant="feature">✨ Feature</Label>
                            <Label variant="task">📋 Task</Label>
                            <Label variant="story">📖 Story</Label>
                          </div>
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                </TabsContent>

                {/* Features Tab */}
                <TabsContent value="features" className="mt-8">
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <Card>
                      <CardHeader>
                        <CardTitle>🎯 완성된 주요 기능</CardTitle>
                      </CardHeader>
                      <CardContent>
                        <ul className="space-y-3">
                          <li className="flex items-center">
                            <span className="text-green-500 mr-3">✅</span>
                            Shadcn UI 컴포넌트 시스템
                          </li>
                          <li className="flex items-center">
                            <span className="text-green-500 mr-3">✅</span>
                            와이어프레임 기반 UI 설계
                          </li>
                          <li className="flex items-center">
                            <span className="text-green-500 mr-3">✅</span>
                            드래그 앤 드롭 칸반 보드
                          </li>
                          <li className="flex items-center">
                            <span className="text-green-500 mr-3">✅</span>
                            이슈 상세 모달 시스템
                          </li>
                          <li className="flex items-center">
                            <span className="text-green-500 mr-3">✅</span>
                            완전한 반응형 디자인
                          </li>
                          <li className="flex items-center">
                            <span className="text-green-500 mr-3">✅</span>
                            프로젝트 필터링 및 검색
                          </li>
                          <li className="flex items-center">
                            <span className="text-green-500 mr-3">✅</span>
                            실시간 대시보드 통계
                          </li>
                          <li className="flex items-center">
                            <span className="text-green-500 mr-3">✅</span>
                            모던한 로그인 시스템
                          </li>
                        </ul>
                      </CardContent>
                    </Card>

                    <Card>
                      <CardHeader>
                        <CardTitle>📊 기술 스택 및 도구</CardTitle>
                      </CardHeader>
                      <CardContent>
                        <div className="space-y-4">
                          <div>
                            <h4 className="font-medium mb-2">프론트엔드</h4>
                            <div className="flex flex-wrap gap-2">
                              <Badge variant="info">React 19+</Badge>
                              <Badge variant="info">TypeScript</Badge>
                              <Badge variant="info">Vite</Badge>
                              <Badge variant="info">Tailwind CSS</Badge>
                            </div>
                          </div>
                          <div>
                            <h4 className="font-medium mb-2">UI 라이브러리</h4>
                            <div className="flex flex-wrap gap-2">
                              <Badge variant="success">Shadcn UI</Badge>
                              <Badge variant="success">Radix UI</Badge>
                              <Badge variant="success">Lucide Icons</Badge>
                              <Badge variant="success">CVA</Badge>
                            </div>
                          </div>
                          <div>
                            <h4 className="font-medium mb-2">기능</h4>
                            <div className="flex flex-wrap gap-2">
                              <Badge variant="secondary">Drag & Drop</Badge>
                              <Badge variant="secondary">Modal System</Badge>
                              <Badge variant="secondary">Responsive</Badge>
                              <Badge variant="secondary">Dark Mode Ready</Badge>
                            </div>
                          </div>
                        </div>
                      </CardContent>
                    </Card>
                  </div>
                </TabsContent>

                {/* Demo Tab - Already handled above */}
                <TabsContent value="demo" className="mt-8">
                  <Card>
                    <CardHeader>
                      <CardTitle>🚀 라이브 데모</CardTitle>
                    </CardHeader>
                    <CardContent>
                      <p className="text-gray-600 mb-4">
                        위의 페이지 탭을 선택하여 각 기능을 직접 체험해보세요!
                      </p>
                      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                        {pages.map((page) => (
                          <Button
                            key={page.id}
                            variant="outline"
                            onClick={() => setCurrentPage(page.id)}
                            className="h-16 flex flex-col items-center justify-center"
                          >
                            <span className="text-lg mb-1">{page.label.split(' ')[0]}</span>
                            <span className="text-xs">{page.label.split(' ').slice(1).join(' ')}</span>
                          </Button>
                        ))}
                      </div>
                    </CardContent>
                  </Card>
                </TabsContent>
              </Tabs>
            </div>

            {/* Footer */}
            <div className="text-center py-8 border-t">
              <p className="text-gray-600 mb-4">
                🎉 <strong>Project Management System</strong> - 와이어프레임 기반 Shadcn UI로 구축된 완전한 프로젝트 관리 솔루션
              </p>
              <div className="flex justify-center space-x-6 text-sm text-gray-500">
                <span>🌐 다국어 지원 준비</span>
                <span>🎨 다크모드 준비</span>
                <span>📱 완전 반응형</span>
                <span>⚡ 최적화된 성능</span>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default App 