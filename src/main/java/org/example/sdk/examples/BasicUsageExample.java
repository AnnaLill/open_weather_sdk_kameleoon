package org.example.sdk.examples;

import org.example.sdk.Mode;
import org.example.sdk.OpenWeatherSDK;
import org.example.sdk.exception.WeatherSDKException;
import org.example.sdk.model.WeatherResponse;

/**
 * Basic usage example demonstrating how to retrieve weather information for a city.
 */
public class BasicUsageExample {

    public static void main(String[] args) {
        String apiKey = "YOUR_API_KEY";

        try {
            OpenWeatherSDK sdk = OpenWeatherSDK.getInstance(apiKey, Mode.ON_DEMAND);

            System.out.println("=== Getting weather for Moscow ===");
            WeatherResponse weather = sdk.getWeather("Moscow");

            System.out.println("City: " + weather.getName());
            System.out.println("Temperature: " + String.format("%.2f", weather.getTemperature().getTemp()) + "°C");
            System.out.println("Feels like: " + String.format("%.2f", weather.getTemperature().getFeelsLike()) + "°C");
            System.out.println("Weather: " + weather.getWeather().getMain() +
                    " - " + weather.getWeather().getDescription());
            System.out.println("Wind speed: " + weather.getWind().getSpeed() + " m/s");
            System.out.println("Visibility: " + weather.getVisibility() + " m");

            System.out.println("\n=== Repeated request (from cache) ===");
            WeatherResponse cachedWeather = sdk.getWeather("Moscow");
            System.out.println("City: " + cachedWeather.getName());
            System.out.println("Temperature: " + String.format("%.2f", cachedWeather.getTemperature().getTemp()) + "°C");

        } catch (WeatherSDKException e) {
            System.err.println("Error getting weather: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
