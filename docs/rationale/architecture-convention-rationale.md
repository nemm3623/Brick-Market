# 아키텍처 규칙 작성 이유와 예시

이 문서는 아키텍처 규칙을 왜 이렇게 작성했는지 설명한다. 실제 공통 개발 규칙은 `docs/conventions.md`를 기준으로 한다.

# 도메인 중심 패키지

## 왜 필요한가

기능이 늘어나면 계층별 패키지만으로는 관련 파일을 한 번에 보기 어렵다. 도메인 중심 패키지는 하나의 기능을 수정할 때 필요한 파일을 가까운 위치에 둔다.

## 예시

상품 등록 오류를 고칠 때 `product/domain`, `product/service`, `product/repository`를 함께 보면 된다. 모든 Service가 한 패키지에 몰려 있으면 `ProductService`, `MemberService`, `FavoriteService`가 섞여 기능 경계를 파악하기 어렵다.

# 계층별 책임

## 왜 필요한가

Controller, Service, Domain, Repository의 책임이 섞이면 같은 규칙이 여러 곳에 중복되거나 누락된다. 특히 Controller가 Repository를 직접 호출하면 유스케이스 검증이 우회될 수 있다.

## 예시

- 판매자 존재 확인은 `ProductService`에서 처리한다.
- Controller는 요청 DTO를 받고 Service를 호출한 뒤 응답 DTO를 반환한다.
- Repository는 저장과 조회에 집중하고 상품 등록 가능 여부를 판단하지 않는다.

# Domain 불변식

## 왜 필요한가

Domain은 어떤 경로로 호출되더라도 깨지면 안 되는 규칙을 지켜야 한다. Controller의 Bean Validation만 믿으면 테스트, 배치, 다른 Service에서 직접 생성할 때 잘못된 객체가 만들어질 수 있다.

## 예시

- `Product.register(...)`는 가격이 0 이하이면 `INVALID_PRODUCT_INFO`를 던진다.
- 상품의 초기 상태는 외부에서 받지 않고 Domain 생성 시 `ON_SALE`로 정한다.

# DTO와 Command 분리

## 왜 필요한가

HTTP 요청 DTO를 Service까지 그대로 넘기면 Service가 웹 계층에 묶인다. Command 객체를 사용하면 Service 테스트와 다른 호출 경로에서도 같은 유스케이스 입력을 사용할 수 있다.

## 예시

- Controller 요청 DTO: JSON 검증용 `@NotBlank`, `@Positive` 등을 가진다.
- Service Command: `ProductRegisterCommand`처럼 유스케이스에 필요한 값만 담고 HTTP annotation에는 의존하지 않는다.
