package com.sportygroup.aviation.api.wrapper.exception;

/**
 * Thrown when the external API (provider) fails (timeout, 5xx, etc.).
 */
public class ExternalServiceException extends RuntimeException {

    public ExternalServiceException(String message) {
        super(message);
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
