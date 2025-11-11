package org.example.sdk.exception;

/**
 * Base exception class for all SDK-related errors.
 * <p>
 * All exceptions thrown by the SDK extend this class, allowing clients
 * to catch all SDK exceptions with a single catch block if needed.
 * </p>
 */
public class WeatherSDKException extends Exception {
    /**
     * Creates a new WeatherSDKException with the specified message.
     *
     * @param message error message
     */
    public WeatherSDKException(String message) {
        super(message);
    }

    /**
     * Creates a new WeatherSDKException with the specified message and cause.
     *
     * @param message error message
     * @param cause   the cause of this exception
     */
    public WeatherSDKException(String message, Throwable cause) {
        super(message, cause);
    }
}
