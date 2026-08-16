# API 현황 문서 작성 이유와 예시

이 문서는 `docs/api.md`를 왜 이렇게 작성했는지 설명한다. 실제 API 구현 상태 관리는 `docs/api.md`를 기준으로 한다.

# 상태 기준

## 왜 필요한가

포트폴리오 프로젝트에서는 무엇이 실제로 동작하고 무엇이 계획인지 구분하는 것이 중요하다. 상태를 나누지 않으면 Domain과 Service만 있는 기능을 완성된 API처럼 오해할 수 있다.

## 예시

- `구현`: Controller, 요청 DTO, 응답 DTO, 관련 테스트가 있고 HTTP로 호출 가능하다.
- `진행 중`: `ProductService`는 있지만 `ProductController`가 아직 없다.
- `계획`: 찜 목록 API처럼 경로와 정책이 아직 확정되지 않았다.

# Controller와 테스트 기준

## 왜 필요한가

API는 Java 메서드가 아니라 HTTP 계약이다. 상태 코드, JSON 응답 구조, 오류 응답까지 검증되어야 외부 클라이언트가 사용할 수 있다.

## 예시

- `GET /api/products/{productId}`가 `200 OK`와 `ApiResponse<ProductResponse>`를 반환하는지 확인해야 `구현`으로 표시한다.
- 없는 상품을 조회했을 때 `404 Not Found`, `PRODUCT_NOT_FOUND`가 반환되는지도 Controller 테스트로 확인한다.

# 승인되지 않은 API를 계획으로 남기는 이유

## 왜 필요한가

문서에 적힌 API는 쉽게 계약처럼 받아들여진다. 아직 정책이 정해지지 않은 API를 확정처럼 쓰면 이후 구현과 문서가 충돌할 수 있다.

## 예시

- `GET /api/products`는 목록, 검색, 정렬, 페이지네이션 정책이 정해지기 전까지 계획 상태로 둔다.
- Favorite API는 중복 찜 처리와 삭제 정책이 정해진 뒤 구현 상태를 올린다.
