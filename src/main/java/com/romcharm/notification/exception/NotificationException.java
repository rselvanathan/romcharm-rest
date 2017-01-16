package com.romcharm.notification.exception;

public class NotificationException extends RuntimeException {
    public NotificationException(String message, Exception e) {
        super(message, e);
    }
}
