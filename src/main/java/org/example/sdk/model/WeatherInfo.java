package org.example.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Weather information model containing main weather condition and description.
 */
public class WeatherInfo {
    @JsonProperty("main")
    private String main;

    @JsonProperty("description")
    private String description;

    public WeatherInfo() {
    }

    public WeatherInfo(String main, String description) {
        this.main = main;
        this.description = description;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
