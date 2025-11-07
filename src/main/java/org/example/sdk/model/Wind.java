package org.example.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Wind {
    @JsonProperty("speed")
    private Double speed;

    public Wind() {
    }

    public Wind(Double speed) {
        this.speed = speed;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }
}
