package org.example.sdk.exception;

import org.example.sdk.Mode;
import org.example.sdk.OpenWeatherSDK;
import org.example.sdk.model.WeatherResponse;

/**
 * Example demonstrating various error handling scenarios in the SDK.
 */
public class ErrorHandlingExample {
    public static void main(String[] args) {
        String apiKey = "YOUR_API_KEY";

        try {
            OpenWeatherSDK sdk = OpenWeatherSDK.getInstance(apiKey, Mode.ON_DEMAND);

            System.out.println("=== Example 1: Non-existent city ===");
            try {
                WeatherResponse weather = sdk.getWeather("NonExistentCity12345");
            } catch (CityNotFoundException e) {
                System.err.println("City not found: " + e.getMessage());
            }

            System.out.println("\n=== Example 2: Invalid API key ===");
            try {
                OpenWeatherSDK invalidSdk = OpenWeatherSDK.getInstance("invalid_key", Mode.ON_DEMAND);
                WeatherResponse weather = invalidSdk.getWeather("Moscow");
            } catch (APIException e) {
                System.err.println("API error: " + e.getMessage());
                System.err.println("HTTP code: " + e.getStatusCode());
            }

            System.out.println("\n=== Example 3: Empty city name ===");
            try {
                WeatherResponse weather = sdk.getWeather("");
            } catch (IllegalArgumentException e) {
                System.err.println("Error: " + e.getMessage());
            }

            System.out.println("\n=== Example 4: Successful data retrieval ===");
            try {
                WeatherResponse weather = sdk.getWeather("Moscow");
                System.out.println("Successfully retrieved weather for: " + weather.getName());
            } catch (WeatherSDKException e) {
                System.err.println("Error: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
