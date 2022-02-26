package org.elefteria.elefteriasn.exception;

public class MyUnauthorizedException extends RuntimeException{
    public MyUnauthorizedException() {
        super();
    }

    public MyUnauthorizedException(String message) {
        super(message);
    }

    public MyUnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyUnauthorizedException(Throwable cause) {
        super(cause);
    }

    protected MyUnauthorizedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
