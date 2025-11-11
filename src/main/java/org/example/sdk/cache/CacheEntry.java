package org.example.sdk.cache;

import org.example.sdk.model.WeatherResponse;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * Represents a cached weather response alongside the timestamp when it was stored.
 * <p>
 * This class is used internally by the cache implementation to track when
 * entries were stored and determine if they are still valid based on TTL.
 * </p>
 */
public class CacheEntry {
    private final WeatherResponse weatherResponse;
    private final Instant storedAt;

    public CacheEntry(WeatherResponse weatherResponse, Instant storedAt) {
        this.weatherResponse = Objects.requireNonNull(weatherResponse, "WeatherResponse must not be null");
        this.storedAt = Objects.requireNonNull(storedAt, "StoredAt timestamp must not be null");
    }

    public WeatherResponse getWeatherResponse() {
        return weatherResponse;
    }

    public Instant getStoredAt() {
        return storedAt;
    }

    /**
     * Checks whether the cache entry is still valid under the provided TTL.
     *
     * @param ttl time-to-live for cache entries
     * @param now current timestamp
     * @return {@code true} when entry is still valid, {@code false} when it has expired
     */
    public boolean isValid(Duration ttl, Instant now) {
        Objects.requireNonNull(ttl, "TTL must not be null");
        Objects.requireNonNull(now, "Current timestamp must not be null");
        return storedAt.plus(ttl).isAfter(now) || storedAt.plus(ttl).equals(now);
    }
}
