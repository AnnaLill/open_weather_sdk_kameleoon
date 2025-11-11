package org.example.sdk.cache;

import org.example.sdk.model.WeatherResponse;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CacheEntryTest {
    @Test
    void testCacheEntry_IsValid_Within10Minutes() {
        WeatherResponse weather = createTestWeatherResponse("Moscow");
        Instant now = Instant.now();
        CacheEntry entry = new CacheEntry(weather, now);
        Duration ttl = Duration.ofMinutes(10);

        assertTrue(entry.isValid(ttl, now));
        assertNotNull(entry.getWeatherResponse());
        assertEquals("Moscow", entry.getWeatherResponse().getName());
        assertEquals(now, entry.getStoredAt());
    }

    @Test
    void testCacheEntry_IsInvalid_After10Minutes() {
        WeatherResponse weather = createTestWeatherResponse("Moscow");
        Instant storedAt = Instant.now().minus(Duration.ofMinutes(11));
        CacheEntry entry = new CacheEntry(weather, storedAt);
        Duration ttl = Duration.ofMinutes(10);
        Instant now = Instant.now();

        assertFalse(entry.isValid(ttl, now));
    }

    @Test
    void testCacheEntry_GetStoredAt() {
        WeatherResponse weather = createTestWeatherResponse("Moscow");
        Instant storedAt = Instant.now();
        CacheEntry entry = new CacheEntry(weather, storedAt);

        Instant retrieved = entry.getStoredAt();

        assertEquals(storedAt, retrieved);
    }

    private WeatherResponse createTestWeatherResponse(String cityName) {
        WeatherResponse response = new WeatherResponse();
        response.setName(cityName);
        return response;
    }
}


