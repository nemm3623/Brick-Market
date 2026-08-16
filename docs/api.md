# Brick-Market API 현황

이 문서는 Brick-Market API의 구현 상태를 관리한다.

공통 개발 규칙은 `docs/conventions.md`를 따른다.

# 상태 기준

- `구현`: Controller와 요청·응답 DTO가 있으며 관련 테스트로 검증된 상태
- `진행 중`: Domain 또는 Service는 있지만 외부 API가 완성되지 않은 상태
- `계획`: 구현을 시작하지 않았거나 API 계약이 승인되지 않은 상태

# 공통 API 기반

| 항목 | 상태 | 비고 |
|---|---|---|
| `ApiResponse<T>` | 구현 | 성공·실패 공통 응답 구조 |
| `BusinessException` | 구현 | 비즈니스 예외 |
| `ErrorCode` | 구현 | HTTP 상태와 메시지 관리 |
| `GlobalExceptionHandler` | 구현 | `BusinessException` 처리 구현 |
| Bean Validation 오류 처리 | 계획 | `INVALID_REQUEST` 응답 추가 필요 |
| 인증·인가 오류 처리 | 계획 | Spring Security 인증 구현 시 추가 |

# Product API

| Method | Path | 인증 | 상태 | 비고 |
|---|---|---|---|---|
| `POST` | `/api/products` | 필요 | 진행 중 | Product 등록 Domain·Service 구현, Controller 필요 |
| `GET` | `/api/products/{productId}` | 선택 | 진행 중 | Product 단건 조회 Service 구현, Controller 필요 |
| `GET` | `/api/products` | 선택 | 계획 | 목록, 검색, 정렬과 페이지네이션 정책 필요 |
| `PATCH` | `/api/products/{productId}` | 필요 | 계획 | 판매자 소유권 검증 필요 |
| `PATCH` | `/api/products/{productId}/status` | 필요 | 계획 | 허용된 상태 전이 정책 필요 |
| `DELETE` | `/api/products/{productId}` | 필요 | 계획 | 삭제 또는 숨김 정책 결정 필요 |

# Favorite API

| Method | Path | 인증 | 상태 | 비고 |
|---|---|---|---|---|
| `POST` | `/api/products/{productId}/favorites` | 필요 | 계획 | 중복 찜 처리 필요 |
| `DELETE` | `/api/products/{productId}/favorites` | 필요 | 계획 | 존재하지 않는 찜 처리 정책 필요 |
| `GET` | `/api/members/me/favorites` | 필요 | 계획 | 목록과 페이지네이션 정책 필요 |

# 갱신 규칙

- API를 추가하거나 변경한 작업에서 이 문서를 함께 갱신한다.
- Controller와 관련 테스트가 없으면 `구현`으로 표시하지 않는다.
- Domain 또는 Service만 구현되어 있으면 `진행 중`으로 표시한다.
- 승인되지 않은 API 경로나 DTO를 확정된 계약처럼 기록하지 않는다.
- 제거되거나 변경된 API는 관련 코드와 테스트 상태를 확인한 후 갱신한다.
