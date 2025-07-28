import React from 'react'

function App() {
  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center">
      <div className="bg-white p-8 rounded-lg shadow-md">
        <h1 className="text-3xl font-bold text-gray-900 mb-4">
          🎉 Frontend가 정상적으로 실행되었습니다!
        </h1>
        <p className="text-gray-600 mb-4">
          Project Management System이 성공적으로 로드되었습니다.
        </p>
        <div className="space-y-2">
          <p className="text-sm text-gray-500">✅ React 앱 로드 완료</p>
          <p className="text-sm text-gray-500">✅ Tailwind CSS 적용 완료</p>
          <p className="text-sm text-gray-500">✅ Vite 개발서버 실행 완료</p>
        </div>
        <button 
          onClick={() => alert('버튼이 정상적으로 동작합니다!')}
          className="mt-4 bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
        >
          테스트 버튼
        </button>
      </div>
    </div>
  )
}

export default App 