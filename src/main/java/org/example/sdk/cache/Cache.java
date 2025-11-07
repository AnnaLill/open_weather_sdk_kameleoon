package org.example.sdk.cache;

import org.example.sdk.model.WeatherResponse;

public interface Cache {
    WeatherResponse get(String cityName);

    void put(String cityName, WeatherResponse weatherResponse);

    String[] getAllCities();

    void clear();

    void remove(String cityName);
    int size();
}
