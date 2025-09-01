package com.account_service.exception;

public class NotSufficeintMoneyException extends RuntimeException {
    public NotSufficeintMoneyException(String message) {
        super(message);
    }
}
