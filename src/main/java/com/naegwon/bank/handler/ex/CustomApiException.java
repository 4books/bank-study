package com.naegwon.bank.handler.ex;

public class CustomApiException extends RuntimeException {
    public CustomApiException() {
    }

    public CustomApiException(String message) {
        super(message);
    }
}
