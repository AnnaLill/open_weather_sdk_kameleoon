# OpenWeather SDK for Java

Production-ready SDK to access OpenWeatherMap API with simple usage, unified JSON model, caching, error handling, and two operating modes (on-demand and polling).

## Features

- Simple API: `OpenWeatherSDK.getInstance(apiKey, mode).getWeather("City")`
- Unified response model (`WeatherResponse`) suitable for direct JSON serialization
- Caching: LRU up to 10 cities, TTL 10 minutes
- Modes: ON_DEMAND and POLLING (refreshes cached cities periodically)
- Singleton per API key (+ destroy instance)
- Robust exceptions (`WeatherSDKException`, `APIException`, `CityNotFoundException`)

## Installation

Java 17+, Maven.

```xml
<dependency>
  <groupId>org.example</groupId>
  <artifactId>open_weather_sdk_kameleoon</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

## Quick start

```java
OpenWeatherSDK sdk = OpenWeatherSDK.getInstance("YOUR_API_KEY", Mode.ON_DEMAND);
WeatherResponse weather = sdk.getWeather("London");
System.out.println(weather.getName());
```

### JSON

`WeatherResponse` maps 1:1 to required JSON. To print JSON:

```java
String json = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(weather);
System.out.println(json);
```

## Caching

- Max 10 cities (LRU)
- Valid for 10 minutes
- POLLING keeps cache warm with background refresh

## Errors

- `CityNotFoundException` (404)
- `APIException` (HTTP error codes)
- `WeatherSDKException` (network/parse/etc.)

## Examples

See `src/main/java/org/example/sdk/examples`:

- BasicUsageExample
- PollingModeExample
- SingletonExample
- ErrorHandlingExample

## Notes

- API key required. Get it at `https://openweathermap.org/api`.
- Units: SDK supports metric (°C, m/s), imperial (°F, mph) and standard (K).

### Units usage

By default, `WeatherApiClient` requests `units=metric` so temperatures are in °C.
If you need explicit units, construct your SDK with a client configured for units:

```java
import org.example.sdk.client.WeatherApiClient;
import org.example.sdk.OpenWeatherSDK;
import org.example.sdk.Mode;

WeatherApiClient apiClient = new WeatherApiClient("YOUR_API_KEY", "imperial"); // °F, mph
OpenWeatherSDK sdk = new OpenWeatherSDK("YOUR_API_KEY", Mode.ON_DEMAND, apiClient, new org.example.sdk.cache.WeatherCache());
```


