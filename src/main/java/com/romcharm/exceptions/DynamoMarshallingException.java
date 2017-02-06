package com.romcharm.exceptions;

/**
 * @author Romesh Selvan
 */
public class DynamoMarshallingException extends RuntimeException {
    public DynamoMarshallingException(String message, Throwable th) {
        super(message, th);
    }
}
