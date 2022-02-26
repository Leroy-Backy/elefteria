package org.elefteria.elefteriasn.exception;

public class MyEntityNotFoundException extends RuntimeException{

    public MyEntityNotFoundException() {
        super();
    }

    public MyEntityNotFoundException(String message) {
        super(message);
    }

    public MyEntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyEntityNotFoundException(Throwable cause) {
        super(cause);
    }

    protected MyEntityNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
