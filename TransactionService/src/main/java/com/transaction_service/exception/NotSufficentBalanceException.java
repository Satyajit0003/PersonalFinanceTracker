package com.transaction_service.exception;

public class NotSufficentBalanceException extends RuntimeException {
    public NotSufficentBalanceException(String message) {
        super(message);
    }
}
