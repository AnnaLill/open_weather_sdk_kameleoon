package org.example.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Sys {
    @JsonProperty("sunrise")
    private Long sunrise;

    @JsonProperty("sunset")
    private Long sunset;

    public Sys() {
    }

    public Sys(Long sunrise, Long sunset) {
        this.sunrise = sunrise;
        this.sunset = sunset;
    }

    public Long getSunrise() {
        return sunrise;
    }

    public void setSunrise(Long sunrise) {
        this.sunrise = sunrise;
    }

    public Long getSunset() {
        return sunset;
    }

    public void setSunset(Long sunset) {
        this.sunset = sunset;
    }
}
