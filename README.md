# project-management-system

지라, 트렐로 같은 프로젝트 관리 시스템 만들기

예제 코드라서 스프링 시큐리티 활용이 부족하고 예외처리도 좀 부실하게 된 부분도 있음
어차피 베이스 코드 완성 후에는 다 수정 예정

### 나중에 수정사항
- 스프링 시큐리티 활용한 구조로 변경
  - 컨트롤러에서 jwt 사용하지 않기
- 연관 관계 최대한 ManyToOne만 사용 
- querydsl 사용
- 레이어 이동간 DTO, Request DTO, Response DTO 사용
- runtime exception 추가
- 빌더, 객체 생성 맵퍼 추가
- 컨트롤러에서 예외처리 하는거 개선
  - UserController가 NotFoundException을 처리하면 나중에 다른 서비스에서 호출할때는 user 체크가 누락되니까