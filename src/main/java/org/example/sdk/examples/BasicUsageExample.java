package org.example.sdk.examples;


import org.example.sdk.Mode;
import org.example.sdk.OpenWeatherSDK;
import org.example.sdk.exception.WeatherSDKException;
import org.example.sdk.model.WeatherResponse;

public class BasicUsageExample {
    public static void main(String[] args) {
        String apiKey = "YOUR_API_KEY";

        try {
            OpenWeatherSDK sdk = OpenWeatherSDK.getInstance(apiKey, Mode.ON_DEMAND);

            System.out.println("=== Получение погоды для Москвы ===");
            WeatherResponse weather = sdk.getWeather("Moscow");

            System.out.println("Город: " + weather.getName());
            System.out.println("Температура: " + weather.getTemperature().getTemp() + "°C");
            System.out.println("Ощущается как: " + weather.getTemperature().getFeelsLike() + "°C");
            System.out.println("Погода: " + weather.getWeather().getMain() +
                    " - " + weather.getWeather().getDescription());
            System.out.println("Скорость ветра: " + weather.getWind().getSpeed() + " м/с");
            System.out.println("Видимость: " + weather.getVisibility() + " м");

            System.out.println("\n=== Повторный запрос (из кэша) ===");
            WeatherResponse cachedWeather = sdk.getWeather("Moscow");
            System.out.println("Город: " + cachedWeather.getName());
            System.out.println("Температура: " + cachedWeather.getTemperature().getTemp() + "°C");

        } catch (WeatherSDKException e) {
            System.err.println("Ошибка при получении погоды: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}
