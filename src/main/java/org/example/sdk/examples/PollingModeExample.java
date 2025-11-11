package org.example.sdk.examples;

import org.example.sdk.Mode;
import org.example.sdk.OpenWeatherSDK;
import org.example.sdk.exception.WeatherSDKException;
import org.example.sdk.model.WeatherResponse;

/**
 * Example demonstrating polling mode, where the SDK automatically refreshes
 * weather data for all cached cities at regular intervals.
 */
public class PollingModeExample {

    public static void main(String[] args) {
        String apiKey = "YOUR_API_KEY";

        try {
            OpenWeatherSDK sdk = OpenWeatherSDK.getInstance(apiKey, Mode.POLLING);

            System.out.println("=== Adding cities to cache ===");
            sdk.getWeather("Moscow");
            sdk.getWeather("London");
            sdk.getWeather("New York");
            sdk.getWeather("Tokyo");
            sdk.getWeather("Paris");
            sdk.getWeather("Berlin");

            System.out.println("Cities in cache: " + sdk.getCacheSize());
            System.out.println("\nIn polling mode, the SDK will automatically update data every 5 minutes");

            System.out.println("\n=== Getting data from cache ===");
            WeatherResponse moscowWeather = sdk.getWeather("Moscow");
            System.out.println("Moscow: " + String.format("%.2f", moscowWeather.getTemperature().getTemp()) + "°C");

            WeatherResponse londonWeather = sdk.getWeather("London");
            System.out.println("London: " + String.format("%.2f", londonWeather.getTemperature().getTemp()) + "°C");

            WeatherResponse newYorkWeather = sdk.getWeather("New York");
            System.out.println("New York: " + String.format("%.2f", newYorkWeather.getTemperature().getTemp()) + "°C");

            WeatherResponse tokyoWeather = sdk.getWeather("Tokyo");
            System.out.println("Tokyo: " + String.format("%.2f", tokyoWeather.getTemperature().getTemp()) + "°C");

            WeatherResponse parisWeather = sdk.getWeather("Paris");
            System.out.println("Paris: " + String.format("%.2f", parisWeather.getTemperature().getTemp()) + "°C");

            WeatherResponse berlinWeather = sdk.getWeather("Berlin");
            System.out.println("Berlin: " + String.format("%.2f", berlinWeather.getTemperature().getTemp()) + "°C");

            System.out.println("\nWaiting 30 seconds...");
            Thread.sleep(30000);

            sdk.destroy();
            System.out.println("SDK stopped");

        } catch (WeatherSDKException e) {
            System.err.println("Error getting weather: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("Wait interrupted");
            Thread.currentThread().interrupt();
        }
    }
}
