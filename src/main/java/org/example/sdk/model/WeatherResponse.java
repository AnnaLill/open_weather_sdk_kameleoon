package org.example.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WeatherResponse {
    @JsonProperty("weather")
    private WeatherInfo weather;

    @JsonProperty("temperature")
    private Temperature temperature;

    @JsonProperty("visibility")
    private Integer visibility;

    @JsonProperty("wind")
    private Wind wind;

    @JsonProperty("datetime")
    private Long datetime;

    @JsonProperty("sys")
    private Sys sys;

    @JsonProperty("timezone")
    private Integer timezone;

    @JsonProperty("name")
    private String name;

    public WeatherResponse() {
    }

    public WeatherResponse(WeatherInfo weather, Temperature temperature, Integer visibility,
                           Wind wind, Long datetime, Sys sys, Integer timezone, String name) {
        this.weather = weather;
        this.temperature = temperature;
        this.visibility = visibility;
        this.wind = wind;
        this.datetime = datetime;
        this.sys = sys;
        this.timezone = timezone;
        this.name = name;
    }

    public WeatherInfo getWeather() {
        return weather;
    }

    public void setWeather(WeatherInfo weather) {
        this.weather = weather;
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public void setTemperature(Temperature temperature) {
        this.temperature = temperature;
    }

    public Integer getVisibility() {
        return visibility;
    }

    public void setVisibility(Integer visibility) {
        this.visibility = visibility;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public Long getDatetime() {
        return datetime;
    }

    public void setDatetime(Long datetime) {
        this.datetime = datetime;
    }

    public Sys getSys() {
        return sys;
    }

    public void setSys(Sys sys) {
        this.sys = sys;
    }

    public Integer getTimezone() {
        return timezone;
    }

    public void setTimezone(Integer timezone) {
        this.timezone = timezone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
