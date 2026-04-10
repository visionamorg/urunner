package com.runhub.weather.dto;
import lombok.*;
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class WeatherDto {
    private double temperatureC;
    private int humidity;
    private double feelsLikeC;
    private double windKmh;
    private String condition;
}
