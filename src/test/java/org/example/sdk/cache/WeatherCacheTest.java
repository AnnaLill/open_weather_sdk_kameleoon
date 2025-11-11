package org.example.sdk.cache;

import org.example.sdk.model.WeatherResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WeatherCacheTest {
    private WeatherCache cache;

    @BeforeEach
    void setUp() {
        cache = new WeatherCache();
    }

    @Test
    void testPutAndGet() {
        String cityName = "Moscow";
        WeatherResponse weather = createTestWeatherResponse(cityName);

        cache.put(cityName, weather);
        WeatherResponse retrieved = cache.get(cityName);

        assertNotNull(retrieved);
        assertEquals(cityName, retrieved.getName());
    }

    @Test
    void testGet_ReturnsNull_WhenCityNotCached() {
        WeatherResponse result = cache.get("NonExistentCity");

        assertNull(result);
    }

    @Test
    void testMaxSize_LRU() {
        for (int i = 1; i <= 11; i++) {
            String cityName = "City" + i;
            cache.put(cityName, createTestWeatherResponse(cityName));
        }

        assertNull(cache.get("City1"));
        assertNotNull(cache.get("City11"));
        assertEquals(10, cache.size());
    }

    @Test
    void testRemove() {
        String cityName = "Moscow";
        cache.put(cityName, createTestWeatherResponse(cityName));

        cache.remove(cityName);

        assertNull(cache.get(cityName));
        assertEquals(0, cache.size());
    }

    @Test
    void testClear() {
        cache.put("Moscow", createTestWeatherResponse("Moscow"));
        cache.put("London", createTestWeatherResponse("London"));

        cache.clear();

        assertEquals(0, cache.size());
        assertNull(cache.get("Moscow"));
        assertNull(cache.get("London"));
    }

    @Test
    void testGetAllCities() {
        cache.put("Moscow", createTestWeatherResponse("Moscow"));
        cache.put("London", createTestWeatherResponse("London"));

        String[] cities = cache.getAllCities();

        assertEquals(2, cities.length);
        assertTrue(java.util.Arrays.asList(cities).contains("Moscow"));
        assertTrue(java.util.Arrays.asList(cities).contains("London"));
    }

    @Test
    void testSize() {
        assertEquals(0, cache.size());

        cache.put("Moscow", createTestWeatherResponse("Moscow"));
        assertEquals(1, cache.size());

        cache.put("London", createTestWeatherResponse("London"));
        assertEquals(2, cache.size());
    }

    private WeatherResponse createTestWeatherResponse(String cityName) {
        WeatherResponse response = new WeatherResponse();
        response.setName(cityName);
        return response;
    }
}


