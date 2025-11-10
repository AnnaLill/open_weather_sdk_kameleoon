package org.example.sdk.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.example.sdk.exception.APIException;
import org.example.sdk.exception.CityNotFoundException;
import org.example.sdk.exception.WeatherSDKException;
import org.example.sdk.model.OpenWeatherMapResponse;
import org.example.sdk.model.WeatherResponse;


import java.io.IOException;


public class WeatherApiClient implements ApiClient{
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final int TIMEOUT_SECONDS = 10;

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public WeatherApiClient(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public WeatherResponse getWeatherByCity(String cityName) throws WeatherSDKException {
        try {
            HttpUrl url = HttpUrl.parse(BASE_URL).newBuilder()
                    .addQueryParameter("q", cityName)
                    .addQueryParameter("appid", apiKey)
                    .addQueryParameter("units", "metric")
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    handleErrorResponse(response.code(), cityName);
                }

                String responseBody = response.body() != null ? response.body().string() : null;
                if (responseBody == null || responseBody.isEmpty()) {
                    throw new APIException("Пустой ответ от API", response.code());
                }

                OpenWeatherMapResponse apiResponse;
                try {
                    apiResponse = objectMapper.readValue(
                            responseBody,
                            OpenWeatherMapResponse.class
                    );
                } catch (com.fasterxml.jackson.databind.JsonMappingException e) {
                    throw new APIException(
                            "Ошибка при парсинге ответа от API. Возможно, формат ответа изменился. " +
                                    "Детали: " + e.getMessage(),
                            0,
                            e
                    );
                }

                return convertToWeatherResponse(apiResponse);
            }
        } catch (IOException e) {
            throw new APIException("Ошибка при обращении к API: " + e.getMessage(), 0, e);
        }
    }

    private WeatherResponse convertToWeatherResponse(OpenWeatherMapResponse apiResponse) {
        OpenWeatherMapResponse.WeatherData weatherData = apiResponse.getWeather() != null
                && !apiResponse.getWeather().isEmpty()
                ? apiResponse.getWeather().get(0)
                : null;

        org.example.sdk.model.WeatherInfo weatherInfo = weatherData != null
                ? new org.example.sdk.model.WeatherInfo(weatherData.getMain(), weatherData.getDescription())
                : null;

        org.example.sdk.model.Temperature temperature = apiResponse.getMain() != null
                ? new org.example.sdk.model.Temperature(
                apiResponse.getMain().getTemp(),
                apiResponse.getMain().getFeelsLike())
                : null;

        org.example.sdk.model.Wind wind = apiResponse.getWind() != null
                ? new org.example.sdk.model.Wind(apiResponse.getWind().getSpeed())
                : null;

        org.example.sdk.model.Sys sys = apiResponse.getSys() != null
                ? new org.example.sdk.model.Sys(
                apiResponse.getSys().getSunrise(),
                apiResponse.getSys().getSunset())
                : null;

        return new WeatherResponse(
                weatherInfo,
                temperature,
                apiResponse.getVisibility(),
                wind,
                apiResponse.getDatetime(),
                sys,
                apiResponse.getTimezone(),
                apiResponse.getName()
        );
    }

    private void handleErrorResponse(int statusCode, String cityName) throws WeatherSDKException {
        switch (statusCode) {
            case 404:
                throw new CityNotFoundException(cityName);
            case 401:
                throw new APIException("Неверный API ключ", statusCode);
            case 429:
                throw new APIException("Превышен лимит запросов к API", statusCode);
            default:
                throw new APIException("Ошибка API: HTTP " + statusCode, statusCode);
        }
    }
}
