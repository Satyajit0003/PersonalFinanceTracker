package com.goal_service.exception;

public class InsufficientBalanceExecption extends RuntimeException {
    public InsufficientBalanceExecption(String message) {
        super(message);
    }
}
