package com.goal_service.exception;

public class MoneyNotSentException extends RuntimeException {
    public MoneyNotSentException(String message) {
        super(message);
    }
}
