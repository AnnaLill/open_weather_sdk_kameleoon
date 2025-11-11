package org.example.sdk.exception;

/**
 * Exception thrown when a requested city is not found by the OpenWeatherMap API.
 */
public class CityNotFoundException extends WeatherSDKException {
    /**
     * Creates a new CityNotFoundException for the specified city.
     *
     * @param cityName name of the city that was not found
     */
    public CityNotFoundException(String cityName) {
        super("City '" + cityName + "' not found");
    }
}
