package org.serviceEngineering.adrViewer.exceptions;

public class ServiceException extends RuntimeException {

    public ServiceException(String format, Object... objects) {
        super(String.format(format, objects));
    }

}