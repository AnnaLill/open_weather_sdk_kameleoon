package org.example.sdk.examples;

import org.example.sdk.Mode;
import org.example.sdk.OpenWeatherSDK;

public class SingletonExample {
    public static void main(String[] args) {
        String apiKey = "YOUR_API_KEY";

        System.out.println("=== Пример 1: Singleton по API ключу ===");
        OpenWeatherSDK sdk1 = OpenWeatherSDK.getInstance(apiKey, Mode.ON_DEMAND);
        OpenWeatherSDK sdk2 = OpenWeatherSDK.getInstance(apiKey, Mode.ON_DEMAND);

        System.out.println("sdk1 == sdk2: " + (sdk1 == sdk2));
        System.out.println("Это один и тот же объект: " + (sdk1.equals(sdk2)));

        System.out.println("\n=== Пример 2: Разные API ключи ===");
        OpenWeatherSDK sdk3 = OpenWeatherSDK.getInstance("key1", Mode.ON_DEMAND);
        OpenWeatherSDK sdk4 = OpenWeatherSDK.getInstance("key2", Mode.ON_DEMAND);

        System.out.println("sdk3 == sdk4: " + (sdk3 == sdk4));
        System.out.println("Это разные объекты");

        System.out.println("\n=== Пример 3: Конфликт режимов ===");
        try {
            OpenWeatherSDK sdk5 = OpenWeatherSDK.getInstance(apiKey, Mode.POLLING);
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка: " + e.getMessage());
            System.err.println("Нельзя создать экземпляр с другим режимом для того же API ключа");
        }

        System.out.println("\n=== Пример 4: Удаление экземпляра ===");
        OpenWeatherSDK sdk6 = OpenWeatherSDK.getInstance("temp_key", Mode.ON_DEMAND);
        System.out.println("Экземпляр создан");

        sdk6.destroy();
        System.out.println("Экземпляр удален через destroy()");

        OpenWeatherSDK sdk7 = OpenWeatherSDK.getInstance("temp_key", Mode.ON_DEMAND);
        System.out.println("Новый экземпляр создан");

        System.out.println("\n=== Пример 5: Удаление через статический метод ===");
        OpenWeatherSDK.destroyInstance("temp_key");
        System.out.println("Экземпляр удален через destroyInstance()");
    }
}
