package org.example.sdk.exception;

/**
 * Exception thrown when the OpenWeatherMap API returns an error response.
 */
public class APIException extends WeatherSDKException {
    private final int statusCode;

    /**
     * Creates a new APIException with the specified message and HTTP status code.
     *
     * @param message    error message
     * @param statusCode HTTP status code from the API response
     */
    public APIException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    /**
     * Creates a new APIException with the specified message, HTTP status code, and cause.
     *
     * @param message    error message
     * @param statusCode HTTP status code from the API response
     * @param cause      the cause of this exception
     */
    public APIException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    /**
     * Returns the HTTP status code from the API response.
     *
     * @return HTTP status code
     */
    public int getStatusCode() {
        return statusCode;
    }
}
