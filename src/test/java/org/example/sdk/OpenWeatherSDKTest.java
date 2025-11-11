package org.example.sdk;

import org.example.sdk.cache.Cache;
import org.example.sdk.client.ApiClient;
import org.example.sdk.exception.CityNotFoundException;
import org.example.sdk.exception.WeatherSDKException;
import org.example.sdk.model.WeatherResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OpenWeatherSDKTest {
    @Mock
    private ApiClient apiClient;

    @Mock
    private Cache cache;

    private OpenWeatherSDK sdk;
    private static final String TEST_API_KEY = "test-api-key";

    @BeforeEach
    void setUp() {
        OpenWeatherSDK.destroyInstance(TEST_API_KEY);
        sdk = new OpenWeatherSDK(TEST_API_KEY, Mode.ON_DEMAND, apiClient, cache);
    }

    @Test
    void testGetWeather_FromCache() throws WeatherSDKException {
        String cityName = "Moscow";
        WeatherResponse cachedResponse = createTestWeatherResponse(cityName);
        when(cache.get(cityName)).thenReturn(cachedResponse);

        WeatherResponse result = sdk.getWeather(cityName);

        assertNotNull(result);
        assertEquals(cityName, result.getName());
        verify(cache, times(1)).get(cityName);
        verify(apiClient, never()).getWeatherByCity(anyString());
    }

    @Test
    void testGetWeather_FromAPI() throws WeatherSDKException {
        String cityName = "Moscow";
        WeatherResponse apiResponse = createTestWeatherResponse(cityName);
        when(cache.get(cityName)).thenReturn(null);
        when(apiClient.getWeatherByCity(cityName)).thenReturn(apiResponse);

        WeatherResponse result = sdk.getWeather(cityName);

        assertNotNull(result);
        assertEquals(cityName, result.getName());
        verify(cache, times(1)).get(cityName);
        verify(apiClient, times(1)).getWeatherByCity(cityName);
        verify(cache, times(1)).put(cityName, apiResponse);
    }

    @Test
    void testGetWeather_ThrowsException_WhenCityNotFound() throws WeatherSDKException {
        String cityName = "NonExistentCity";
        when(cache.get(cityName)).thenReturn(null);
        when(apiClient.getWeatherByCity(cityName)).thenThrow(new CityNotFoundException(cityName));

        assertThrows(CityNotFoundException.class, () -> sdk.getWeather(cityName));
        verify(cache, times(1)).get(cityName);
        verify(apiClient, times(1)).getWeatherByCity(cityName);
        verify(cache, never()).put(anyString(), any());
    }

    @Test
    void testGetWeather_ThrowsException_WhenCityNameIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> sdk.getWeather(""));
        assertThrows(IllegalArgumentException.class, () -> sdk.getWeather("   "));
        assertThrows(IllegalArgumentException.class, () -> sdk.getWeather(null));
    }

    @Test
    void testGetInstance_SingletonPattern() {
        String apiKey = "singleton-test-key";
        OpenWeatherSDK.destroyInstance(apiKey);

        OpenWeatherSDK instance1 = OpenWeatherSDK.getInstance(apiKey, Mode.ON_DEMAND);
        OpenWeatherSDK instance2 = OpenWeatherSDK.getInstance(apiKey, Mode.ON_DEMAND);

        assertSame(instance1, instance2);
    }

    @Test
    void testGetInstance_ThrowsException_WhenDifferentMode() {
        String apiKey = "mode-conflict-key";
        OpenWeatherSDK.destroyInstance(apiKey);
        OpenWeatherSDK.getInstance(apiKey, Mode.ON_DEMAND);

        assertThrows(IllegalArgumentException.class,
                () -> OpenWeatherSDK.getInstance(apiKey, Mode.POLLING));
    }

    @Test
    void testGetCacheSize() {
        when(cache.size()).thenReturn(5);

        int size = sdk.getCacheSize();

        assertEquals(5, size);
        verify(cache, times(1)).size();
    }

    @Test
    void testGetMode() {
        Mode mode = sdk.getMode();

        assertEquals(Mode.ON_DEMAND, mode);
    }

    @Test
    void testDestroy() {
        sdk.destroy();

        OpenWeatherSDK newInstance = OpenWeatherSDK.getInstance(TEST_API_KEY, Mode.ON_DEMAND);
        assertNotSame(sdk, newInstance);
    }

    private WeatherResponse createTestWeatherResponse(String cityName) {
        WeatherResponse response = new WeatherResponse();
        response.setName(cityName);
        return response;
    }
}


