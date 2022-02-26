package org.elefteria.elefteriasn.exception;

public class MyForbiddenException extends RuntimeException{
    public MyForbiddenException() {
        super();
    }

    public MyForbiddenException(String message) {
        super(message);
    }

    public MyForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyForbiddenException(Throwable cause) {
        super(cause);
    }

    protected MyForbiddenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
