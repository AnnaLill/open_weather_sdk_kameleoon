package org.example.sdk.exception;

import org.example.sdk.Mode;
import org.example.sdk.OpenWeatherSDK;
import org.example.sdk.model.WeatherResponse;

public class ErrorHandlingExample {
    public static void main(String[] args) {
        String apiKey = "YOUR_API_KEY";

        try {
            OpenWeatherSDK sdk = OpenWeatherSDK.getInstance(apiKey, Mode.ON_DEMAND);

            System.out.println("=== Пример 1: Несуществующий город ===");
            try {
                WeatherResponse weather = sdk.getWeather("NonExistentCity12345");
            } catch (CityNotFoundException e) {
                System.err.println("Город не найден: " + e.getMessage());
            }

            System.out.println("\n=== Пример 2: Неверный API ключ ===");
            try {
                OpenWeatherSDK invalidSdk = OpenWeatherSDK.getInstance("invalid_key", Mode.ON_DEMAND);
                WeatherResponse weather = invalidSdk.getWeather("Moscow");
            } catch (APIException e) {
                System.err.println("Ошибка API: " + e.getMessage());
                System.err.println("HTTP код: " + e.getStatusCode());
            }

            System.out.println("\n=== Пример 3: Пустое название города ===");
            try {
                WeatherResponse weather = sdk.getWeather("");
            } catch (IllegalArgumentException e) {
                System.err.println("Ошибка: " + e.getMessage());
            }

            System.out.println("\n=== Пример 4: Успешное получение данных ===");
            try {
                WeatherResponse weather = sdk.getWeather("Moscow");
                System.out.println("Успешно получена погода для: " + weather.getName());
            } catch (WeatherSDKException e) {
                System.err.println("Ошибка: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("Неожиданная ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
