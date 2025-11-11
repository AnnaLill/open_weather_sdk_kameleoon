package org.example.sdk;

import org.example.sdk.cache.Cache;
import org.example.sdk.cache.WeatherCache;
import org.example.sdk.client.ApiClient;
import org.example.sdk.client.WeatherApiClient;
import org.example.sdk.exception.WeatherSDKException;
import org.example.sdk.model.WeatherResponse;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Central SDK entry point for retrieving weather information from OpenWeatherMap.
 * <p>
 * Supports {@link Mode#ON_DEMAND} for on-request lookups and {@link Mode#POLLING}
 * for proactive cache refreshes that keep responses warm.
 * Instances are uniquely identified by API key, guaranteeing a single instance per key.
 */
public class OpenWeatherSDK implements AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(OpenWeatherSDK.class.getName());
    private static final Map<String, OpenWeatherSDK> INSTANCES = new ConcurrentHashMap<>();
    private static final ReentrantReadWriteLock INSTANCES_LOCK = new ReentrantReadWriteLock();
    private static final Duration POLLING_INTERVAL = Duration.ofMinutes(5);

    private final String apiKey;
    private final Mode mode;
    private final ApiClient apiClient;
    private final Cache cache;
    private final ScheduledExecutorService scheduler;
    private final ReentrantReadWriteLock cacheLock;
    private final Units units;

    /**
     * Create an SDK instance with default HTTP client and in-memory cache implementations.
     *
     * @param apiKey API key issued by OpenWeatherMap
     * @param mode   desired SDK mode. Must not be {@code null}.
     */
    public OpenWeatherSDK(String apiKey, Mode mode) {
        this(apiKey, mode, new WeatherApiClient(apiKey, Units.METRIC.apiValue()), new WeatherCache(), Units.METRIC);
    }

    /**
     * Constructor for advanced customization and testing.
     * <p>
     * This constructor allows injection of custom {@link ApiClient} and {@link Cache}
     * implementations. For normal usage, prefer {@link #getInstance(String, Mode)} or
     * {@link #OpenWeatherSDK(String, Mode)}.
     * </p>
     *
     * @param apiKey    OpenWeatherMap API key (must not be null or blank)
     * @param mode      SDK operating mode (must not be null)
     * @param apiClient API client implementation (must not be null)
     * @param cache     cache implementation (must not be null)
     */
    public OpenWeatherSDK(String apiKey, Mode mode, ApiClient apiClient, Cache cache) {
        this(apiKey, mode, apiClient, cache, Units.METRIC);
    }

    /**
     * Full constructor with explicit units.
     *
     * @param apiKey    OpenWeatherMap API key (must not be null or blank)
     * @param mode      SDK operating mode (must not be null)
     * @param apiClient API client implementation (must not be null)
     * @param cache     cache implementation (must not be null)
     * @param units     measurement units
     */
    public OpenWeatherSDK(String apiKey, Mode mode, ApiClient apiClient, Cache cache, Units units) {
        this.apiKey = normaliseApiKey(apiKey);
        this.mode = Objects.requireNonNull(mode, "Mode must not be null");
        this.apiClient = Objects.requireNonNull(apiClient, "ApiClient must not be null");
        this.cache = Objects.requireNonNull(cache, "Cache must not be null");
        this.cacheLock = new ReentrantReadWriteLock(true);
        this.units = units == null ? Units.METRIC : units;

        if (mode == Mode.POLLING) {
            this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread thread = new Thread(r, "open-weather-sdk-polling");
                thread.setDaemon(true);
                return thread;
            });
            scheduler.scheduleAtFixedRate(
                    this::updateAllCachedCities,
                    0,
                    POLLING_INTERVAL.toMinutes(),
                    TimeUnit.MINUTES
            );
        } else {
            this.scheduler = null;
        }
    }

    /**
     * Retrieve or create an SDK instance for the provided API key and mode.
     * Only one instance per key is allowed at any given time.
     *
     * @param apiKey API key issued by OpenWeatherMap
     * @param mode   SDK operating mode
     * @return existing or newly created {@link OpenWeatherSDK}
     */
    public static OpenWeatherSDK getInstance(String apiKey, Mode mode) {
        return getInstance(apiKey, mode, Units.METRIC);
    }

    /**
     * Retrieve or create an SDK instance for the provided API key, mode and units.
     * Only one instance per key is allowed at any given time. Units must match for the same key.
     */
    public static OpenWeatherSDK getInstance(String apiKey, Mode mode, Units units) {
        String normalisedKey = normaliseApiKey(apiKey);
        Objects.requireNonNull(mode, "Mode must not be null");
        Units requestedUnits = (units == null ? Units.METRIC : units);

        INSTANCES_LOCK.readLock().lock();
        try {
            OpenWeatherSDK existing = INSTANCES.get(normalisedKey);
            if (existing != null) {
                ensureSameMode(mode, existing);
                ensureSameUnits(requestedUnits, existing);
                return existing;
            }
        } finally {
            INSTANCES_LOCK.readLock().unlock();
        }

        INSTANCES_LOCK.writeLock().lock();
        try {
            OpenWeatherSDK existing = INSTANCES.get(normalisedKey);
            if (existing != null) {
                ensureSameMode(mode, existing);
                ensureSameUnits(requestedUnits, existing);
                return existing;
            }

            OpenWeatherSDK newInstance = new OpenWeatherSDK(normalisedKey, mode, new WeatherApiClient(normalisedKey, requestedUnits.apiValue()), new WeatherCache(), requestedUnits);
            INSTANCES.put(normalisedKey, newInstance);
            return newInstance;
        } finally {
            INSTANCES_LOCK.writeLock().unlock();
        }
    }

    /**
     * Retrieve weather information for the given city, leveraging cache when possible.
     *
     * @param cityName target city name
     * @return the weather response
     * @throws WeatherSDKException when API invocation fails
     */
    public WeatherResponse getWeather(String cityName) throws WeatherSDKException {
        String normalizedCityName = normaliseCityName(cityName);

        cacheLock.readLock().lock();
        try {
            WeatherResponse cached = cache.get(normalizedCityName);
            if (cached != null) {
                return cached;
            }
        } finally {
            cacheLock.readLock().unlock();
        }

        WeatherResponse response = apiClient.getWeatherByCity(normalizedCityName);

        cacheLock.writeLock().lock();
        try {
            cache.put(normalizedCityName, response);
        } finally {
            cacheLock.writeLock().unlock();
        }

        return response;
    }

    /**
     * Destroy this instance, shutting down internal resources and removing it from the registry.
     */
    public void destroy() {
        shutdownScheduler();
        INSTANCES_LOCK.writeLock().lock();
        try {
            OpenWeatherSDK existing = INSTANCES.get(apiKey);
            if (existing == this) {
                INSTANCES.remove(apiKey);
            }
        } finally {
            INSTANCES_LOCK.writeLock().unlock();
        }
    }

    /**
     * Destroy the SDK instance associated with the provided API key, if present.
     *
     * @param apiKey API key whose instance should be removed
     */
    public static void destroyInstance(String apiKey) {
        String normalisedKey = normaliseApiKeyOrNull(apiKey);
        if (normalisedKey == null) {
            return;
        }

        OpenWeatherSDK instance;
        INSTANCES_LOCK.writeLock().lock();
        try {
            instance = INSTANCES.remove(normalisedKey);
        } finally {
            INSTANCES_LOCK.writeLock().unlock();
        }

        if (instance != null) {
            instance.shutdownScheduler();
        }
    }

    /**
     * @return operating mode (on-demand or polling)
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * @return measurement units (metric, imperial, standard)
     */
    public Units getUnits() {
        return units;
    }

    /**
     * @return current number of cached cities
     */
    public int getCacheSize() {
        cacheLock.readLock().lock();
        try {
            return cache.size();
        } finally {
            cacheLock.readLock().unlock();
        }
    }

    @Override
    public void close() {
        destroy();
    }

    private void updateAllCachedCities() {
        cacheLock.readLock().lock();
        String[] cities;
        try {
            cities = cache.getAllCities();
        } finally {
            cacheLock.readLock().unlock();
        }

        for (String city : cities) {
            try {
                WeatherResponse response = apiClient.getWeatherByCity(city);
                cacheLock.writeLock().lock();
                try {
                    cache.put(city, response);
                } finally {
                    cacheLock.writeLock().unlock();
                }
            } catch (WeatherSDKException e) {
                LOGGER.log(Level.WARNING, "Failed to refresh cached weather for city: " + city, e);
            }
        }
    }

    private static void ensureSameMode(Mode requestedMode, OpenWeatherSDK existing) {
        if (existing.mode != requestedMode) {
            throw new IllegalArgumentException(
                    "An SDK instance with this API key already exists in a different mode: " + existing.mode
            );
        }
    }

    private static void ensureSameUnits(Units requestedUnits, OpenWeatherSDK existing) {
        if (existing.units != requestedUnits) {
            throw new IllegalArgumentException(
                    "An SDK instance with this API key already exists with different units: " + existing.units
            );
        }
    }

    private static String normaliseApiKey(String apiKey) {
        String result = normaliseApiKeyOrNull(apiKey);
        if (result == null) {
            throw new IllegalArgumentException("API key must not be null or blank");
        }
        return result;
    }

    private static String normaliseApiKeyOrNull(String apiKey) {
        if (apiKey == null) {
            return null;
        }
        String trimmed = apiKey.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static String normaliseCityName(String cityName) {
        if (cityName == null) {
            throw new IllegalArgumentException("City name must not be null or blank");
        }

        String normalized = cityName.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("City name must not be null or blank");
        }
        return normalized;
    }

    private void shutdownScheduler() {
        if (scheduler == null || scheduler.isShutdown()) {
            return;
        }

        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

