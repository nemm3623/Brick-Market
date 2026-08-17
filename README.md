# Brick-Market

Brick-Market은 중고 레고와 미개봉 레고 제품을 안전하게 거래할 수 있도록 중개하는 Spring Boot 기반 백엔드 프로젝트입니다.

## 기술 스택

- Java 21
- Spring Boot 3.5.x
- Spring Web
- Spring Data JPA
- Spring Security
- OAuth2 Client
- H2 Database
- Flyway
- JUnit 5

## 실행

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

## 현재 구현 범위

- Spring Boot 프로젝트 초기 세팅
- 테스트 프로파일 구성
- 애플리케이션 컨텍스트 로딩 테스트
- 공통 API 응답 구조
- 공통 비즈니스 예외 및 전역 예외 처리
- CodeRabbit 리뷰 설정
- GitHub Actions 기반 Gradle 빌드 검증
- Flyway 기반 회원 테이블 마이그레이션
- Flyway 기반 상품 테이블 마이그레이션
- OAuth 제공자와 제공자 회원 ID 기반 회원 식별 구조
- 회원 기본 역할 및 상태 관리
- 동일 OAuth 식별자 재요청 시 기존 회원 반환
- 동시 OAuth 회원 생성 요청의 유니크 충돌 복구
- Spring Security 세션 기반 카카오 OAuth 로그인
- 로그인 회원 조회 API (`GET /api/members/me`)
- 회원 도메인 및 서비스 테스트
- 중고/미개봉 상품 도메인 구조
- 판매자 회원 기준 상품 등록
- 상품 단건 조회
- 상품 도메인 및 서비스 테스트
