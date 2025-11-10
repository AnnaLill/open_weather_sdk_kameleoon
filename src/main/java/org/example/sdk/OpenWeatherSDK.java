package org.example.sdk;

import org.example.sdk.cache.Cache;
import org.example.sdk.cache.WeatherCache;
import org.example.sdk.client.ApiClient;
import org.example.sdk.client.WeatherApiClient;
import org.example.sdk.exception.WeatherSDKException;
import org.example.sdk.model.WeatherResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class OpenWeatherSDK {
    private static final Map<String, OpenWeatherSDK> instances = new ConcurrentHashMap<>();
    private static final ReentrantReadWriteLock instancesLock = new ReentrantReadWriteLock();

    private final String apiKey;
    private final Mode mode;
    private final ApiClient apiClient;
    private final Cache cache;
    private final ScheduledExecutorService scheduler;
    private final ReentrantReadWriteLock cacheLock;

    public OpenWeatherSDK(String apiKey, Mode mode) {
        this(apiKey, mode, new WeatherApiClient(apiKey), new WeatherCache());
    }

    OpenWeatherSDK(String apiKey, Mode mode, ApiClient apiClient, Cache cache) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("API ключ не может быть пустым");
        }
        if (mode == null) {
            throw new IllegalArgumentException("Режим работы не может быть null");
        }
        if (apiClient == null) {
            throw new IllegalArgumentException("ApiClient не может быть null");
        }
        if (cache == null) {
            throw new IllegalArgumentException("Cache не может быть null");
        }

        this.apiKey = apiKey;
        this.mode = mode;
        this.apiClient = apiClient;
        this.cache = cache;
        this.cacheLock = new ReentrantReadWriteLock();

        if (mode == Mode.POLLING) {
            this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread thread = new Thread(r, "OpenWeatherSDK-Polling");
                thread.setDaemon(true);
                return thread;
            });
            scheduler.scheduleAtFixedRate(this::updateAllCachedCities, 0, 10, TimeUnit.MINUTES);
        } else {
            this.scheduler = null;
        }
    }

    public static OpenWeatherSDK getInstance(String apiKey, Mode mode) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("API ключ не может быть пустым");
        }
        if (mode == null) {
            throw new IllegalArgumentException("Режим работы не может быть null");
        }

        String key = apiKey.trim();

        instancesLock.readLock().lock();
        try {
            OpenWeatherSDK existing = instances.get(key);
            if (existing != null) {
                if (existing.mode != mode) {
                    throw new IllegalArgumentException(
                            "Экземпляр SDK с таким API ключом уже существует с другим режимом: " + existing.mode
                    );
                }
                return existing;
            }
        } finally {
            instancesLock.readLock().unlock();
        }

        instancesLock.writeLock().lock();
        try {
            OpenWeatherSDK existing = instances.get(key);
            if (existing != null) {
                if (existing.mode != mode) {
                    throw new IllegalArgumentException(
                            "Экземпляр SDK с таким API ключом уже существует с другим режимом: " + existing.mode
                    );
                }
                return existing;
            }

            OpenWeatherSDK newInstance = new OpenWeatherSDK(key, mode);
            instances.put(key, newInstance);
            return newInstance;
        } finally {
            instancesLock.writeLock().unlock();
        }
    }

    public WeatherResponse getWeather(String cityName) throws WeatherSDKException {
        if (cityName == null || cityName.trim().isEmpty()) {
            throw new IllegalArgumentException("Название города не может быть пустым");
        }

        String normalizedCityName = cityName.trim();

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
            }
        }
    }
    public void destroy() {
        instancesLock.writeLock().lock();
        try {
            instances.remove(this.apiKey);
        } finally {
            instancesLock.writeLock().unlock();
        }

        if (scheduler != null && !scheduler.isShutdown()) {
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

    public static void destroyInstance(String apiKey) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return;
        }

        instancesLock.writeLock().lock();
        try {
            OpenWeatherSDK instance = instances.remove(apiKey.trim());
            if (instance != null) {
                instance.destroy();
            }
        } finally {
            instancesLock.writeLock().unlock();
        }
    }

    public Mode getMode() {
        return mode;
    }

    public int getCacheSize() {
        cacheLock.readLock().lock();
        try {
            return cache.size();
        } finally {
            cacheLock.readLock().unlock();
        }
    }
}
