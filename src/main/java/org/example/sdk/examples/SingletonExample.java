package org.example.sdk.examples;

import org.example.sdk.Mode;
import org.example.sdk.OpenWeatherSDK;

/**
 * Example demonstrating the singleton pattern behavior of the SDK,
 * where only one instance per API key is allowed.
 */
public class SingletonExample {
    public static void main(String[] args) {
        String apiKey = "YOUR_API_KEY";

        System.out.println("=== Example 1: Singleton by API key ===");
        OpenWeatherSDK sdk1 = OpenWeatherSDK.getInstance(apiKey, Mode.ON_DEMAND);
        OpenWeatherSDK sdk2 = OpenWeatherSDK.getInstance(apiKey, Mode.ON_DEMAND);

        System.out.println("sdk1 == sdk2: " + (sdk1 == sdk2));
        System.out.println("These are the same object: " + (sdk1.equals(sdk2)));

        System.out.println("\n=== Example 2: Different API keys ===");
        OpenWeatherSDK sdk3 = OpenWeatherSDK.getInstance("key1", Mode.ON_DEMAND);
        OpenWeatherSDK sdk4 = OpenWeatherSDK.getInstance("key2", Mode.ON_DEMAND);

        System.out.println("sdk3 == sdk4: " + (sdk3 == sdk4));
        System.out.println("These are different objects");

        System.out.println("\n=== Example 3: Mode conflict ===");
        try {
            OpenWeatherSDK sdk5 = OpenWeatherSDK.getInstance(apiKey, Mode.POLLING);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.err.println("Cannot create instance with different mode for the same API key");
        }

        System.out.println("\n=== Example 4: Destroying instance ===");
        OpenWeatherSDK sdk6 = OpenWeatherSDK.getInstance("temp_key", Mode.ON_DEMAND);
        System.out.println("Instance created");

        sdk6.destroy();
        System.out.println("Instance destroyed via destroy()");

        OpenWeatherSDK sdk7 = OpenWeatherSDK.getInstance("temp_key", Mode.ON_DEMAND);
        System.out.println("New instance created");

        System.out.println("\n=== Example 5: Destroying via static method ===");
        OpenWeatherSDK.destroyInstance("temp_key");
        System.out.println("Instance destroyed via destroyInstance()");
    }
}
