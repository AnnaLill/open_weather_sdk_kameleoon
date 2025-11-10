package org.example.sdk.cache;

import org.example.sdk.model.WeatherResponse;

public class CacheEntry {
    private final WeatherResponse weatherResponse;
    private final long timestamp;

    public CacheEntry(WeatherResponse weatherResponse) {
        this.weatherResponse = weatherResponse;
        this.timestamp = System.currentTimeMillis();
    }

    public WeatherResponse getWeatherResponse() {
        return weatherResponse;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isValid() {
        long currentTime = System.currentTimeMillis();
        long age = currentTime - timestamp;
        return age < 600000;
    }
}
