package com.romcharm.exceptions;

import com.romcharm.defaults.APIErrorCode;

public class NotFoundException extends APIException {

    public NotFoundException(APIErrorCode apiErrorCode) {
        super(apiErrorCode);
    }
}
