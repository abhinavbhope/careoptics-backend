package com.specsShope.specsBackend.exception;

public class PastUserNotFoundException extends RuntimeException {
    public PastUserNotFoundException(String id) {
        super("Past user not found: " + id);
    }
}
