package com.romcharm.defaults;

public enum APIErrorCode {
    EMAIL_NOT_FOUND("Email was not found."),
    FAMILY_EXISTS("The Family has already been added associated with the username provided"),
    USER_NOT_FOUND("User was not found"),
    PASSWORD_INCORRECT("Password is incorrect"),
    USER_EXISTS("User already exists");

    private String reason;

    APIErrorCode(String reason) {this.reason = reason;}

    public String getReason() {return reason;}
}
