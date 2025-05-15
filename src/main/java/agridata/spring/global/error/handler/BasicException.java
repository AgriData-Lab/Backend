package agridata.spring.global.error.handler;

import agridata.spring.global.code.BaseErrorCode;
import agridata.spring.global.error.exception.GeneralException;

public class BasicException extends GeneralException {
    public BasicException(BaseErrorCode code) {
        super(code);
    }
}