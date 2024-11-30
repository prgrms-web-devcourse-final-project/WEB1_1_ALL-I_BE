package com.JAI.global.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus {
    // 공통 관련 에러
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400"),
    INVALID_LOGIN(HttpStatus.BAD_REQUEST, "COMMON401"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404"),

    // 사용자 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404"),

    // 카테고리 에러
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY404"),

    // 개인 일정 에러
    PERSONAL_EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY404"),
    PERSONAL_EVENT_NOT_OWNER_FOUND(HttpStatus.FORBIDDEN, "CATEGORY403"),
    PERSONAL_EVENT_BAD_REQUEST(HttpStatus.BAD_REQUEST, "CATEGORY400"),

    // 챗봇 에러
    CHATBOT_BAD_REQUEST(HttpStatus.BAD_REQUEST, "CHATBOT400"),
    CHATBOT_FORBIDDEN(HttpStatus.FORBIDDEN, "CHATBOT403"),
    CHATBOT_NOT_FOUND(HttpStatus.NOT_FOUND, "CHATBOT404"),
    CHATBOT_UNPROCESSABLE_ENTITY(HttpStatus.UNPROCESSABLE_ENTITY, "CHATBOT422");

    private final HttpStatus httpStatus;
    private final String code;
}
