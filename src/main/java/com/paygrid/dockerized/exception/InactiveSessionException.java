package com.paygrid.dockerized.exception;

public class InactiveSessionException extends RuntimeException {
    public InactiveSessionException(String message) {
        super(message);
    }
} 