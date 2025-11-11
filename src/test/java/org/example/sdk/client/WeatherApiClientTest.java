package org.example.sdk.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WeatherApiClientTest {
    private WeatherApiClient apiClient;
    private static final String TEST_API_KEY = "test-api-key";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        apiClient = new WeatherApiClient(TEST_API_KEY);
    }

    @Test
    void testConstructor_InitializesClient() {
        WeatherApiClient client = new WeatherApiClient(TEST_API_KEY);

        assertNotNull(client);
    }

    @Test
    void testConstructor_ThrowsException_WhenApiKeyIsNull() {
        assertThrows(NullPointerException.class, () -> new WeatherApiClient(null));
    }

    @Test
    void testConstructor_ThrowsException_WhenApiKeyIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new WeatherApiClient("   "));
    }
}


