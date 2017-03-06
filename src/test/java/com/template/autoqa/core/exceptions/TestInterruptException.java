package com.template.autoqa.core.exceptions;


public class TestInterruptException extends RuntimeException {
    public TestInterruptException(String message) {
        super(message);
    }

    public TestInterruptException(Exception e) {
        super(e);
    }

    public TestInterruptException(String message, Exception e) {
        super(message, e);
    }
}
