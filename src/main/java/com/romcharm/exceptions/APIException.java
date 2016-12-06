package com.romcharm.exceptions;

import com.romcharm.defaults.APIErrorCode;

public class APIException extends RuntimeException {
    private APIErrorCode apiErrorCode;

    APIException(APIErrorCode apiErrorCode) {
        this.apiErrorCode = apiErrorCode;
    }

    public APIErrorCode getApiErrorCode() {
        return apiErrorCode;
    }
}
