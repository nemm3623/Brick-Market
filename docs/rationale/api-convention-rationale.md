# API 규칙 작성 이유와 예시

이 문서는 API 규칙을 왜 이렇게 작성했는지 설명한다. 실제 공통 개발 규칙은 `docs/conventions.md`를 기준으로 한다.

# URI 규칙

## 왜 필요한가

URI는 클라이언트가 직접 의존하는 계약이다. 내부 Controller 이름이나 Service 메서드명이 바뀌어도 API 경로는 안정적으로 유지되어야 한다.

## 예시

- 좋은 예: `GET /api/products/{productId}`
- 피해야 할 예: `GET /api/getProduct?id=1`

# 요청과 응답 DTO

## 왜 필요한가

JPA Entity를 API에 직접 노출하면 클라이언트가 알 필요 없는 내부 필드까지 계약에 포함된다. 또한 Entity 변경이 API 변경으로 번질 수 있다.

## 예시

- `Product` Entity에는 `seller`, `status`, 생성 시각 같은 내부 필드가 있을 수 있다.
- 상품 등록 요청 DTO에는 클라이언트가 입력해야 하는 `type`, `title`, `description`, `price`만 받는 편이 안전하다.

# Bean Validation과 INVALID_REQUEST

## 왜 필요한가

요청 형식 오류와 비즈니스 오류는 성격이 다르다. 빈 제목, 길이 초과, 음수 가격 같은 입력 형식 문제는 Controller의 Bean Validation에서 먼저 거르는 편이 명확하다.

현재 `ErrorCode.INVALID_REQUEST`는 아직 구현되어 있지 않으므로, 이 규칙은 구현 완료 상태가 아니라 Controller 작업 시 추가해야 할 기준이다.

## 예시

- 사용자가 `title: ""`로 상품 등록을 요청하면 `INVALID_REQUEST`와 `400 Bad Request`를 반환한다.
- 존재하지 않는 판매자로 상품 등록을 요청하면 요청 형식은 맞지만 비즈니스 대상이 없으므로 `MEMBER_NOT_FOUND`를 반환한다.

# 인증 사용자 식별

## 왜 필요한가

`sellerId`나 `memberId`를 요청 본문에서 그대로 믿으면 사용자가 다른 회원 ID를 넣어 권한을 우회할 수 있다. 운영 API에서는 인증 정보에서 현재 사용자를 식별해야 한다.

## 예시

- 운영: OAuth 로그인 정보에서 현재 회원 ID를 가져와 상품 판매자로 사용한다.
- 임시 개발: `X-MEMBER-ID` 헤더를 쓰더라도 로컬 또는 테스트 전용으로 제한하고 제거 조건을 남긴다.
