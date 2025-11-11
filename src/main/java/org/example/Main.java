package org.example;

import org.example.sdk.Mode;
import org.example.sdk.OpenWeatherSDK;
import org.example.sdk.exception.WeatherSDKException;
import org.example.sdk.model.WeatherResponse;

/**
 * Main class demonstrating basic SDK usage.
 * <p>
 * To run this example, set your OpenWeatherMap API key as an environment variable
 * or replace "YOUR_API_KEY_HERE" with your actual API key.
 * </p>
 */
public class Main {
    // API returns metric units (°C) by default.

    public static void main(String[] args) {
        // Replace with your API key or set it as an environment variable
        String apiKey = System.getenv("OPENWEATHER_API_KEY");
        if (apiKey == null || apiKey.trim().isEmpty()) {
            apiKey = "YOUR_API_KEY_HERE";
        }

        if ("YOUR_API_KEY_HERE".equals(apiKey)) {
            System.out.println("⚠️  WARNING: Replace YOUR_API_KEY_HERE with your actual API key!");
            System.out.println("Get your API key at: https://openweathermap.org/api");
            return;
        }

        try {
            System.out.println("=== Initializing SDK ===");
            OpenWeatherSDK sdk = OpenWeatherSDK.getInstance(apiKey, Mode.ON_DEMAND);
            System.out.println("SDK initialized in mode: " + sdk.getMode());

            String cityName = args.length > 0 ? args[0] : "New York";
            System.out.println("\n=== Getting weather for " + cityName + " ===");
            WeatherResponse weather = sdk.getWeather(cityName);

            System.out.println("City: " + weather.getName());
            System.out.println("Temperature: " + String.format("%.2f", weather.getTemperature().getTemp()) + "°C");
            System.out.println("Feels like: " + String.format("%.2f", weather.getTemperature().getFeelsLike()) + "°C");
            System.out.println("Weather: " + weather.getWeather().getMain() +
                    " - " + weather.getWeather().getDescription());
            System.out.println("Wind speed: " + weather.getWind().getSpeed() + " m/s");
            System.out.println("Visibility: " + weather.getVisibility() + " m");

            System.out.println("\n=== Demonstrating caching ===");
            System.out.println("Repeated request (data from cache):");
            WeatherResponse cachedWeather = sdk.getWeather("Moscow");
            System.out.println("City: " + cachedWeather.getName());
            System.out.println("Temperature: " + String.format("%.2f", cachedWeather.getTemperature().getTemp()) + "°C");
            System.out.println("Cities in cache: " + sdk.getCacheSize());

        } catch (WeatherSDKException e) {
            System.err.println("Error getting weather: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
