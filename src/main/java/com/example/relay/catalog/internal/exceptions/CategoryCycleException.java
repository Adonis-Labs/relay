package com.example.relay.catalog.internal.exceptions;

public class CategoryCycleException extends RuntimeException {
    public CategoryCycleException(String message) {
        super(message);
    }
}
