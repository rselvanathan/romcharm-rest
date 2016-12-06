package com.romcharm.exceptions;

import com.romcharm.defaults.APIErrorCode;

/**
 * TODO add comment
 */
public class NotFoundException extends APIException {

    public NotFoundException(APIErrorCode apiErrorCode) {
        super(apiErrorCode);
    }
}
