# 데이터베이스 규칙 작성 이유와 예시

이 문서는 데이터베이스 규칙을 왜 이렇게 작성했는지 설명한다. 실제 공통 개발 규칙은 `docs/conventions.md`를 기준으로 한다.

# Flyway 스키마 관리

## 왜 필요한가

Entity만 추가하고 실제 테이블 마이그레이션을 빼먹으면 테스트 프로필에서는 통과해도 기본 프로필에서는 앱이 기동하지 않을 수 있다. Flyway는 스키마 변경 이력을 코드와 함께 관리하게 해준다.

## 예시

- `Member` Entity를 추가했다면 `V1__create_members_table.sql`이 필요하다.
- `Product` Entity를 추가했다면 `V2__create_products_table.sql`처럼 다음 버전 마이그레이션을 추가한다.

# Entity와 DB 제약 일치

## 왜 필요한가

Java 코드와 DB 제약이 다르면 실패 지점이 예측하기 어려워진다. Domain에서는 통과했는데 저장 시점에 DB가 막거나, 반대로 DB에는 잘못된 데이터가 들어갈 수 있다.

## 예시

- `Product.title`을 100자까지 허용한다면 Entity의 `@Column(length = 100)`, Domain 검증, DB 컬럼 길이를 맞춘다.
- 가격은 Domain에서 0 이하를 막고, DB에서도 가능한 경우 CHECK 제약조건으로 한 번 더 막는다.

# Enum과 CHECK 제약조건

## 왜 필요한가

enum 값은 Java와 DB 양쪽에서 동시에 관리된다. 한쪽만 바꾸면 저장 실패나 잘못된 데이터 허용으로 이어질 수 있다.

## 예시

- Java enum에 `ProductType.UNOPENED`가 있다면 DB CHECK에도 `'UNOPENED'`가 있어야 한다.
- `ProductStatus.RESERVED`를 제거하거나 이름을 바꿀 때는 기존 저장 데이터와 API 문서까지 함께 확인한다.

# 기본 프로필 기동 확인

## 왜 필요한가

테스트 프로필은 빠른 테스트를 위해 `create-drop`을 사용할 수 있다. 하지만 실제 실행 설정은 Flyway와 `ddl-auto: validate`를 사용할 수 있으므로, 테스트 통과만으로 앱 실행 성공을 보장할 수 없다.

## 예시

- Flyway 파일을 추가했다면 `./gradlew test` 후 기본 프로필로 앱이 기동되는지도 확인한다.
- Entity 컬럼명을 바꿨다면 마이그레이션의 컬럼명도 같은지 확인한다.
