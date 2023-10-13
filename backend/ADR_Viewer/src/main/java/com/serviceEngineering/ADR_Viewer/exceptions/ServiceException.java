package com.serviceEngineering.ADR_Viewer.exceptions;

public class ServiceException extends RuntimeException {

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String format, Object... objects) {
        super(String.format(format, objects));
    }

}