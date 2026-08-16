# Brick-Market 개발 규칙

이 문서는 Brick-Market 백엔드에서 처음부터 일관되게 지킬 최소 규칙만 정리한다.

개별 API의 구현 상태는 `docs/api.md`에서 관리한다.

# 기본 방향

- 도메인 중심 패키지 구조를 사용한다.
- 한 번에 모든 도메인을 설계하기보다 하나의 기능을 세로로 완성한다.
- Controller, Service, Domain, Repository의 책임을 섞지 않는다.
- 현재 코드, 테스트, Flyway 마이그레이션이 문서보다 우선한다.

# 패키지 구조

```text
com.brickmarket
  product
    controller
    service
    repository
    domain
    dto
  member
    controller
    service
    repository
    domain
    dto
  common
    exception
```

- 새 기능은 가능한 한 해당 도메인 패키지 안에 둔다.
- 공통 응답, 예외, 설정처럼 여러 도메인이 함께 쓰는 코드는 `common`에 둔다.

# 계층별 책임

## Controller

- HTTP 요청을 요청 DTO로 받는다.
- `@Valid`로 요청 형식을 검증한다.
- 인증된 사용자를 식별한다.
- Service를 호출하고 응답 DTO를 반환한다.
- 비즈니스 규칙이나 Repository 호출을 직접 구현하지 않는다.

## Service

- 하나의 유스케이스 흐름을 조율한다.
- 회원과 리소스의 존재 여부를 확인한다.
- 권한, 소유권, 중복, 상태 전이를 검증한다.
- 여러 Domain과 Repository가 참여하는 작업을 연결한다.
- Controller 전용 HTTP 객체에 의존하지 않는다.

## Domain

- 어떤 호출 경로에서도 반드시 지켜야 하는 불변식을 보호한다.
- Entity 생성 시 필수 값과 초기 상태를 검증한다.
- 상태 변경은 의미가 드러나는 메서드로 수행한다.
- 외부에서 모든 필드를 임의로 바꿀 수 있는 public setter를 제공하지 않는다.
- Controller, Service, Repository에 의존하지 않는다.

## Repository

- Entity의 저장과 조회를 담당한다.
- 비즈니스 규칙과 상태 전이를 구현하지 않는다.
- 단순 CRUD는 Spring Data JPA가 제공하는 기능을 우선 사용한다.
- 복잡한 조회는 실제 요구가 생겼을 때 추가한다.

# DTO와 Command

- 요청 DTO와 응답 DTO를 분리한다.
- JPA Entity를 API 요청이나 응답으로 직접 사용하지 않는다.
- HTTP 요청 DTO를 Domain이나 Repository까지 전달하지 않는다.
- Service 입력 항목이 많거나 의미 있는 입력 묶음이 필요하면 Command 객체를 사용한다.
- Command는 `@RequestBody`, `@RequestParam`, `HttpServletRequest` 같은 HTTP 전용 annotation이나 객체를 포함하지 않는다.

# 검증 책임

- Controller/DTO: null, blank, 길이, 범위, 형식 같은 요청 형식 검증
- Service: 리소스 존재 여부, 권한, 소유권, 중복, 상태 전이 검증
- Domain: 필수 연관관계, 가격, 초기 상태처럼 객체 자체가 책임져야 하는 불변식 검증
- 공개 Service 메서드는 Controller 외부에서도 호출될 수 있다고 보고, 필수 인자가 `null`이면 Repository 호출 전에 프로젝트의 비즈니스 예외로 변환한다.
- Controller의 Bean Validation이 Domain 불변식 검증을 대신하지 않는다.

# Entity 작성 규칙

- JPA용 기본 생성자는 `protected`로 제한한다.
- Entity 생성은 검증된 생성자 또는 의미 있는 정적 팩토리 메서드를 사용한다.
- 초기 상태는 생성 과정에서 결정한다.
- 연관관계는 기본적으로 지연 로딩을 사용한다.
- Entity 변경은 `product.reserve()`, `order.cancel()`처럼 의도가 드러나는 메서드로 수행한다.

# API 규칙

- API 경로는 `/api`로 시작한다.
- 리소스 이름은 복수 명사를 사용한다.
- 경로는 소문자와 하이픈을 사용한다.
- JSON 필드명은 `camelCase`를 사용한다.
- 컬렉션 결과가 없으면 `null` 대신 빈 배열을 반환한다.
- 날짜와 시간은 ISO 8601 형식으로 반환한다.
- `204 No Content` 응답에는 `ApiResponse` 본문을 포함하지 않는다.
- 실패 응답의 HTTP 상태 코드는 오류 원인과 일치시킨다.

# 예외 응답

- 비즈니스 오류는 `BusinessException`과 `ErrorCode`로 표현한다.
- `GlobalExceptionHandler`는 `ErrorCode`의 HTTP 상태와 메시지를 사용한다.
- `errorCode`는 클라이언트가 분기 처리할 수 있는 고정된 `UPPER_SNAKE_CASE` 문자열을 사용한다.
- 스택 트레이스, SQL, 토큰과 개인정보를 API 응답에 포함하지 않는다.

실패 응답 예시:

```json
{
  "success": false,
  "data": null,
  "errorCode": "PRODUCT_NOT_FOUND",
  "message": "상품을 찾을 수 없습니다."
}
```

# 데이터베이스 규칙

- 데이터베이스 스키마는 Flyway 마이그레이션으로 관리한다.
- 마이그레이션 파일은 `src/main/resources/db/migration`에 둔다.
- 파일명은 `V버전__설명.sql` 형식을 사용한다.
- 이미 적용되거나 공유된 마이그레이션 파일은 수정하지 않고 새 버전을 추가한다.
- 테이블명과 컬럼명은 `snake_case`를 사용한다.
- Entity의 필수 값은 DB에서도 `NOT NULL`로 보호한다.
- enum은 `EnumType.STRING`으로 저장하고, Java enum 값과 CHECK 제약조건을 함께 확인한다.
- 사용 가능성이 있다는 이유만으로 인덱스를 미리 추가하지 않는다.

# 검증 기준

- 문서만 변경한 경우 맞춤법, 링크, 현재 코드와의 충돌 여부를 확인한다.
- 새 동작이나 버그 수정에는 관련 테스트를 추가한다.
- API 변경은 정상 요청과 주요 예외 응답을 검증한다.
- JPA, Flyway 또는 설정 변경은 전체 테스트와 기본 프로필 애플리케이션 기동을 확인한다.
- 실행하지 않은 테스트나 명령은 통과했다고 표현하지 않는다.
