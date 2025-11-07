package org.example.sdk.exception;

public class CityNotFoundException extends WeatherSDKException {
    public CityNotFoundException(String cityName) {
        super("Город '" + cityName + "' не найден");
    }
}
