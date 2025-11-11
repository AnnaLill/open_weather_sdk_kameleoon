package org.example.sdk;

/**
 * SDK operating mode.
 * <p>
 * The SDK supports two modes:
 * <ul>
 *   <li>{@link #ON_DEMAND}: Weather data is fetched only when requested by the client</li>
 *   <li>{@link #POLLING}: Weather data is automatically refreshed for all cached cities
 *       at regular intervals to ensure zero-latency responses</li>
 * </ul>
 * </p>
 */
public enum Mode {
    /**
     * On-demand mode: weather data is fetched only when explicitly requested.
     */
    ON_DEMAND,

    /**
     * Polling mode: weather data is automatically refreshed for all cached cities
     * at regular intervals (every 5 minutes) to ensure fast responses.
     */
    POLLING
}
