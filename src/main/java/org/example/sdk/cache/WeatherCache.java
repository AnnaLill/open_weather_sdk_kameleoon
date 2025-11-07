package org.example.sdk.cache;

import org.example.sdk.model.WeatherResponse;

import java.util.LinkedHashMap;
import java.util.Map;

public class WeatherCache implements Cache {
        private static final int MAX_SIZE = 10;
        private static final long CACHE_TTL_MS = 600000; // 10 минут в миллисекундах

        private final Map<String, CacheEntry> cache;

    public WeatherCache() {
            this.cache = new LinkedHashMap<String, CacheEntry>(MAX_SIZE + 1, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, CacheEntry> eldest) {
                    return size() > MAX_SIZE;
                }
            };
        }


        public WeatherResponse get(String cityName) {
            CacheEntry entry = cache.get(cityName);
            if (entry == null) {
                return null;
            }

            if (entry.isValid()) {
                return entry.getWeatherResponse();
            } else {
                // Удаляем устаревшие данные
                cache.remove(cityName);
                return null;
            }
        }


        public void put(String cityName, WeatherResponse weatherResponse) {
            cache.put(cityName, new CacheEntry(weatherResponse));
        }


        public String[] getAllCities() {
            return cache.keySet().toArray(new String[0]);
        }

        public void clear() {
            cache.clear();
        }

        public void remove(String cityName) {
            cache.remove(cityName);
        }

        public int size() {
            return cache.size();
        }
}
