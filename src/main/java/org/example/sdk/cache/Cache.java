package org.example.sdk.cache;

import org.example.sdk.model.WeatherResponse;

/**
 * Contract for caching weather responses.
 */
public interface Cache {

    /**
     * Retrieve cached weather data for the provided city.
     *
     * @param cityName city identifier
     * @return cached response or {@code null} if not present or expired
     */
    WeatherResponse get(String cityName);

    /**
     * Store weather data for the provided city.
     *
     * @param cityName       city identifier
     * @param weatherResponse weather response payload
     */
    void put(String cityName, WeatherResponse weatherResponse);

    /**
     * @return list of cached city identifiers
     */
    String[] getAllCities();

    /**
     * Remove all cached data.
     */
    void clear();

    /**
     * Remove cached data for the provided city, if present.
     *
     * @param cityName city identifier
     */
    void remove(String cityName);

    /**
     * @return number of cached entries
     */
    int size();
}
