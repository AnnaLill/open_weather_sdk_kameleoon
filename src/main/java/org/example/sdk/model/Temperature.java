package org.example.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Temperature {

        @JsonProperty("temp")
        private Double temp;

        @JsonProperty("feels_like")
        private Double feelsLike;

        public Temperature() {
        }

        public Temperature(Double temp, Double feelsLike) {
            this.temp = temp;
            this.feelsLike = feelsLike;
        }

        public Double getTemp() {
            return temp;
        }

        public void setTemp(Double temp) {
            this.temp = temp;
        }

        public Double getFeelsLike() {
            return feelsLike;
        }

        public void setFeelsLike(Double feelsLike) {
            this.feelsLike = feelsLike;
        }
}
