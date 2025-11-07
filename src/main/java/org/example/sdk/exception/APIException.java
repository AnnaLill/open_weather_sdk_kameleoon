package org.example.sdk.exception;

public class APIException extends WeatherSDKException {
    private final int statusCode;

    public APIException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public APIException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
