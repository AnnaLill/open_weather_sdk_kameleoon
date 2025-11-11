package org.example.sdk.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.example.sdk.exception.APIException;
import org.example.sdk.exception.CityNotFoundException;
import org.example.sdk.exception.WeatherSDKException;
import org.example.sdk.model.OpenWeatherMapResponse;
import org.example.sdk.model.Sys;
import org.example.sdk.model.Temperature;
import org.example.sdk.model.WeatherInfo;
import org.example.sdk.model.WeatherResponse;
import org.example.sdk.model.Wind;

import java.io.IOException;
import java.util.Objects;

/**
 * HTTP client implementation for accessing OpenWeatherMap API.
 * <p>
 * This class handles HTTP communication with the OpenWeatherMap API,
 * deserializes JSON responses, and converts them to the SDK's internal
 * {@link WeatherResponse} format.
 * </p>
 */
public class WeatherApiClient implements ApiClient {
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String API_KEY_PARAM = "appid";
    private static final String CITY_PARAM = "q";
    private static final String UNITS_PARAM = "units";
    private static final int HTTP_NOT_FOUND = 404;
    private static final int HTTP_UNAUTHORIZED = 401;

    private final String apiKey;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String units;

    /**
     * Creates a new WeatherApiClient with the provided API key.
     *
     * @param apiKey OpenWeatherMap API key (must not be null or blank)
     */
    public WeatherApiClient(String apiKey) {
        this.apiKey = Objects.requireNonNull(apiKey, "API key must not be null");
        if (apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("API key must not be blank");
        }
        this.httpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
        this.units = "metric";
    }

    /**
     * Creates a new WeatherApiClient with custom HTTP client (primarily for testing).
     *
     * @param apiKey      OpenWeatherMap API key
     * @param httpClient  custom HTTP client instance
     */
    WeatherApiClient(String apiKey, OkHttpClient httpClient) {
        this.apiKey = Objects.requireNonNull(apiKey, "API key must not be null");
        if (apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("API key must not be blank");
        }
        this.httpClient = Objects.requireNonNull(httpClient, "HTTP client must not be null");
        this.objectMapper = new ObjectMapper();
        this.units = "metric";
    }

    /**
     * Creates a new WeatherApiClient with explicit units.
     *
     * @param apiKey OpenWeatherMap API key
     * @param units  units to use: "metric", "imperial", or "standard"
     */
    public WeatherApiClient(String apiKey, String units) {
        this.apiKey = Objects.requireNonNull(apiKey, "API key must not be null");
        if (apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("API key must not be blank");
        }
        this.httpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
        this.units = (units == null || units.isBlank()) ? "metric" : units;
    }

    /**
     * Retrieves weather information for the specified city from OpenWeatherMap API.
     *
     * @param cityName name of the city to query
     * @return weather response containing current weather data
     * @throws WeatherSDKException if the API request fails or city is not found
     */
    @Override
    public WeatherResponse getWeatherByCity(String cityName) throws WeatherSDKException {
        Objects.requireNonNull(cityName, "City name must not be null");
        if (cityName.trim().isEmpty()) {
            throw new IllegalArgumentException("City name must not be blank");
        }

        HttpUrl url = HttpUrl.parse(BASE_URL).newBuilder()
                .addQueryParameter(CITY_PARAM, cityName)
                .addQueryParameter(API_KEY_PARAM, apiKey)
                .addQueryParameter(UNITS_PARAM, units)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            return handleResponse(response, cityName);
        } catch (IOException e) {
            throw new WeatherSDKException("Failed to execute HTTP request: " + e.getMessage(), e);
        }
    }

    private WeatherResponse handleResponse(Response response, String cityName) throws WeatherSDKException, IOException {
        int statusCode = response.code();
        String responseBody = response.body() != null ? response.body().string() : "";

        if (!response.isSuccessful()) {
            if (statusCode == HTTP_NOT_FOUND) {
                throw new CityNotFoundException(cityName);
            }
            if (statusCode == HTTP_UNAUTHORIZED) {
                throw new APIException("Invalid API key", statusCode);
            }
            throw new APIException("API request failed with status " + statusCode, statusCode);
        }

        try {
            OpenWeatherMapResponse apiResponse = objectMapper.readValue(responseBody, OpenWeatherMapResponse.class);
            return convertToWeatherResponse(apiResponse);
        } catch (Exception e) {
            throw new WeatherSDKException("Failed to parse API response: " + e.getMessage(), e);
        }
    }

    private WeatherResponse convertToWeatherResponse(OpenWeatherMapResponse apiResponse) {
        WeatherResponse response = new WeatherResponse();

        if (apiResponse.getWeather() != null && !apiResponse.getWeather().isEmpty()) {
            OpenWeatherMapResponse.WeatherData weatherData = apiResponse.getWeather().get(0);
            WeatherInfo weatherInfo = new WeatherInfo(weatherData.getMain(), weatherData.getDescription());
            response.setWeather(weatherInfo);
        }

        if (apiResponse.getMain() != null) {
            Temperature temperature = new Temperature(
                    apiResponse.getMain().getTemp(),
                    apiResponse.getMain().getFeelsLike()
            );
            response.setTemperature(temperature);
        }

        if (apiResponse.getWind() != null) {
            Wind wind = new Wind(apiResponse.getWind().getSpeed());
            response.setWind(wind);
        }

        if (apiResponse.getSys() != null) {
            Sys sys = new Sys(apiResponse.getSys().getSunrise(), apiResponse.getSys().getSunset());
            response.setSys(sys);
        }

        response.setVisibility(apiResponse.getVisibility());
        response.setDatetime(apiResponse.getDatetime());
        response.setTimezone(apiResponse.getTimezone());
        response.setName(apiResponse.getName());

        return response;
    }
}

