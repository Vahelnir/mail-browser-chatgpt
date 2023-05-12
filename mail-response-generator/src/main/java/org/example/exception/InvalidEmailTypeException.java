package org.example.exception;


public class InvalidEmailTypeException extends Exception {
    public InvalidEmailTypeException(String message) {
        super(message);
    }
}