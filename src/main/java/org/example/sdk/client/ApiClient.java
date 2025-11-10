package org.example.sdk.client;

import org.example.sdk.exception.WeatherSDKException;
import org.example.sdk.model.WeatherResponse;

public interface ApiClient {
    WeatherResponse getWeatherByCity(String cityName) throws WeatherSDKException;
}
