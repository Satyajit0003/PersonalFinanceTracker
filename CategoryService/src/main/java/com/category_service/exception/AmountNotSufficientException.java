package com.category_service.exception;

public class AmountNotSufficientException extends RuntimeException {
    public AmountNotSufficientException(String message) {
        super(message);
    }
}
