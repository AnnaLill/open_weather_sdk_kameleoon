package org.example.sdk.cache;

import org.example.sdk.model.WeatherResponse;

import java.time.Clock;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * In-memory LRU (Least Recently Used) cache for weather responses with a fixed maximum size and TTL.
 * <p>
 * This implementation uses a LinkedHashMap with access-order iteration to implement LRU eviction.
 * When the cache reaches its maximum size (default: 10 cities), the least recently used entry
 * is automatically removed when a new entry is added.
 * </p>
 * <p>
 * Cache entries expire after the configured TTL (default: 10 minutes). Expired entries are
 * automatically removed when accessed.
 * </p>
 * <p>
 * <b>Thread Safety:</b> This class is not thread-safe. External synchronization must be
 * provided by the caller when used in a multi-threaded environment.
 * </p>
 */
public class WeatherCache implements Cache {
    private static final int DEFAULT_MAX_SIZE = 10;
    private static final Duration DEFAULT_TTL = Duration.ofMinutes(10);

    private final Map<String, CacheEntry> cache;
    private final Duration ttl;
    private final int maxSize;
    private final Clock clock;

    public WeatherCache() {
        this(DEFAULT_TTL, DEFAULT_MAX_SIZE, Clock.systemUTC());
    }

    WeatherCache(Duration ttl, int maxSize, Clock clock) {
        this.ttl = Objects.requireNonNull(ttl, "TTL must not be null");
        if (ttl.isNegative() || ttl.isZero()) {
            throw new IllegalArgumentException("TTL must be greater than zero");
        }
        if (maxSize <= 0) {
            throw new IllegalArgumentException("Max size must be greater than zero");
        }
        this.maxSize = maxSize;
        this.clock = Objects.requireNonNull(clock, "Clock must not be null");
        this.cache = new LinkedHashMap<>(maxSize + 1, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, CacheEntry> eldest) {
                return size() > WeatherCache.this.maxSize;
            }
        };
    }

    @Override
    public WeatherResponse get(String cityName) {
        Objects.requireNonNull(cityName, "City name must not be null");
        CacheEntry entry = cache.get(cityName);
        if (entry == null) {
            return null;
        }

        if (entry.isValid(ttl, clock.instant())) {
            return entry.getWeatherResponse();
        }

        cache.remove(cityName);
        return null;
    }

    @Override
    public void put(String cityName, WeatherResponse weatherResponse) {
        Objects.requireNonNull(cityName, "City name must not be null");
        Objects.requireNonNull(weatherResponse, "WeatherResponse must not be null");
        cache.put(cityName, new CacheEntry(weatherResponse, clock.instant()));
    }

    @Override
    public String[] getAllCities() {
        return cache.keySet().toArray(new String[0]);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public void remove(String cityName) {
        if (cityName == null) {
            return;
        }
        cache.remove(cityName);
    }

    @Override
    public int size() {
        return cache.size();
    }
}
