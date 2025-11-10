package org.example.sdk.examples;

import org.example.sdk.Mode;
import org.example.sdk.OpenWeatherSDK;
import org.example.sdk.exception.WeatherSDKException;
import org.example.sdk.model.WeatherResponse;

public class PollingModeExample {
    public static void main(String[] args) {
        String apiKey = "YOUR_API_KEY";

        try {
            OpenWeatherSDK sdk = OpenWeatherSDK.getInstance(apiKey, Mode.POLLING);

            System.out.println("=== Добавление городов в кэш ===");
            sdk.getWeather("Moscow");
            sdk.getWeather("London");
            sdk.getWeather("New York");
            sdk.getWeather("Tokyo");

            System.out.println("Городов в кэше: " + sdk.getCacheSize());
            System.out.println("\nВ режиме polling SDK будет автоматически обновлять данные каждые 10 минут");

            System.out.println("\n=== Получение данных из кэша ===");
            WeatherResponse moscowWeather = sdk.getWeather("Moscow");
            System.out.println("Москва: " + moscowWeather.getTemperature().getTemp() + "°C");

            WeatherResponse londonWeather = sdk.getWeather("London");
            System.out.println("Лондон: " + londonWeather.getTemperature().getTemp() + "°C");

            System.out.println("\nОжидание 30 секунд...");
            Thread.sleep(30000);

            sdk.destroy();
            System.out.println("SDK остановлен");

        } catch (WeatherSDKException e) {
            System.err.println("Ошибка при получении погоды: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("Прервано ожидание");
            Thread.currentThread().interrupt();
        }
    }
}
