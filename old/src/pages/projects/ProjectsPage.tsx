const ProjectsPage = () => {
  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">프로젝트</h1>
          <p className="mt-1 text-sm text-gray-600">
            참여중인 프로젝트 목록입니다.
          </p>
        </div>
        <button className="btn btn-primary">
          새 프로젝트 만들기
        </button>
      </div>

      {/* Project Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {/* Sample Projects */}
        {[1, 2, 3, 4, 5].map((i) => (
          <div key={i} className="card p-6 hover:shadow-md transition-shadow">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg font-medium text-gray-900">
                프로젝트 {i}
              </h3>
              <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                진행중
              </span>
            </div>
            <p className="text-gray-600 text-sm mb-4">
              프로젝트 {i}의 설명입니다. 이 프로젝트는 현재 진행중인 상태입니다.
            </p>
            <div className="flex items-center justify-between text-sm text-gray-500">
              <span>이슈 {i * 3}개</span>
              <span>멤버 {i + 2}명</span>
            </div>
            <div className="mt-4">
              <div className="w-full bg-gray-200 rounded-full h-2">
                <div 
                  className="bg-blue-600 h-2 rounded-full" 
                  style={{ width: `${Math.random() * 100}%` }}
                />
              </div>
              <p className="text-xs text-gray-500 mt-1">
                진행률 {Math.floor(Math.random() * 100)}%
              </p>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}

export default ProjectsPage 