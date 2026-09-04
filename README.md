# Brick-Market

Brick-Market은 중고 레고와 미개봉 레고 제품을 안전하게 거래할 수 있도록 중개하는 Spring Boot 기반 백엔드 프로젝트입니다.

## 주요 기능

- 카카오 OAuth2 기반 세션 로그인
- 중고·미개봉 레고 상품 등록
- 비로그인 사용자를 포함한 상품 상세 조회
- 판매 중 상품 유형·제목 검색과 페이지 조회
- 로그인 회원 정보 조회

## 구현 현황

| 영역 | 구현 내용 |
|---|---|
| 인증·회원 | 카카오 OAuth2 세션 로그인, OAuth 회원 생성·재사용, 로그인 회원 조회 |
| 상품 | 중고·미개봉 상품 구분, 로그인 판매자의 상품 등록, 상품 단건·목록 조회, 유형·제목 검색과 페이지네이션 |
| 공통 기반 | 공통 API 응답, 비즈니스 예외와 전역 예외 처리, 요청값 검증, 인증·인가 오류 응답 |
| 데이터 관리 | Flyway 회원·상품 스키마 관리, JPA Auditing 생성·수정 시간 기록 |
| 자동화 | GitHub Actions 빌드·테스트, CodeRabbit 리뷰 설정 |

API별 구현 상태와 계획은 [API 현황](docs/api.md)에서 관리합니다.

## 기술 스택

- Backend: Java 21, Spring Boot 3.5.x, Spring Web
- Security: Spring Security, OAuth2 Client
- Data: Spring Data JPA, H2 Database, Flyway
- Test: JUnit 5, AssertJ, Mockito
- Automation: GitHub Actions, CodeRabbit

## 로컬 실행

```bash
./gradlew bootRun
```

## 카카오 OAuth 로그인 설정

실제 카카오 로그인을 사용하려면 카카오 개발자 콘솔에서 애플리케이션의 카카오 로그인을 활성화하고 다음 Redirect URI를 등록해야 합니다.

```text
http://localhost:8080/login/oauth2/code/kakao
```

카카오 애플리케이션의 REST API 키와 Client Secret을 환경 변수로 전달해 실행합니다.

```bash
KAKAO_CLIENT_ID=your-rest-api-key \
KAKAO_CLIENT_SECRET=your-client-secret \
./gradlew bootRun
```

`KAKAO_CLIENT_ID`와 `KAKAO_CLIENT_SECRET`의 기본 더미 값은 로컬 애플리케이션 기동 확인용이며 실제 카카오 로그인에는 사용할 수 없습니다. 실제 키와 Secret은 저장소에 커밋하지 않습니다.

## 테스트

```bash
./gradlew test
```

## 프로젝트 문서

- [API 현황](docs/api.md)
- [개발 규칙](docs/conventions.md)
