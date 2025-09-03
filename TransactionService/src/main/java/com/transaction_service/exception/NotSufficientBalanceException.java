package com.transaction_service.exception;

public class NotSufficientBalanceException extends RuntimeException {
    public NotSufficientBalanceException(String message) {
        super(message);
    }
}
