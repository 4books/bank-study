package com.naegwon.bank.handler.ex;

//추후에 사용할 예정
public class CustomForbiddenException extends RuntimeException {
    public CustomForbiddenException() {
    }

    public CustomForbiddenException(String message) {
        super(message);
    }

    public CustomForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
