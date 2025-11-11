package org.example.sdk;

/**
 * Measurement units used by OpenWeatherMap API.
 * METRIC  - Celsius (°C), meters per second (m/s)
 * IMPERIAL - Fahrenheit (°F), miles per hour (mph)
 * STANDARD - Kelvin (K), API default
 */
public enum Units {
    METRIC("metric"),
    IMPERIAL("imperial"),
    STANDARD("standard");

    private final String apiValue;

    Units(String apiValue) {
        this.apiValue = apiValue;
    }

    public String apiValue() {
        return apiValue;
    }
}

package org.example.sdk;

/**
 * Measurement units used by the OpenWeatherMap API.
 * <p>
 * - METRIC: Celsius, meters per second
 * - IMPERIAL: Fahrenheit, miles per hour
 * - STANDARD: Kelvin (default by API)
 * </p>
 */
public enum Units {
    METRIC("metric"),
    IMPERIAL("imperial"),
    STANDARD("standard");

    private final String apiValue;

    Units(String apiValue) {
        this.apiValue = apiValue;
    }

    public String apiValue() {
        return apiValue;
    }
}




