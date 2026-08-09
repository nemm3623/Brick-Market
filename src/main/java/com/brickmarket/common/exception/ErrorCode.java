package com.brickmarket.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
    FORBIDDEN_PRODUCT_ACCESS(HttpStatus.FORBIDDEN, "상품에 대한 권한이 없습니다."),
    INVALID_PRODUCT_STATUS(HttpStatus.BAD_REQUEST, "상품 상태가 올바르지 않습니다."),
    FAVORITE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 찜한 상품입니다."),
    FAVORITE_NOT_FOUND(HttpStatus.NOT_FOUND, "찜 정보를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
