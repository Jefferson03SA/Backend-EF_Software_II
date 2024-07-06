package com.paygrid.dockerized.exception;

public class DeudaNotFoundException extends RuntimeException {
    public DeudaNotFoundException(String message) {
        super(message);
    }
}

