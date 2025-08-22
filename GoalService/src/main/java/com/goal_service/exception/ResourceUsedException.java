package com.goal_service.exception;

public class ResourceUsedException extends RuntimeException {
    public ResourceUsedException(String message) {
        super(message);
    }
}
