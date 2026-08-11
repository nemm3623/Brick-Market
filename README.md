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
- OAuth 제공자와 제공자 회원 ID 기반 회원 식별 구조
- 회원 기본 역할 및 상태 관리
- 동일 OAuth 식별자 재요청 시 기존 회원 반환
- 동시 OAuth 회원 생성 요청의 유니크 충돌 복구
- 회원 도메인 및 서비스 테스트
