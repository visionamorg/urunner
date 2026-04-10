package com.runhub.weather.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.runhub.weather.dto.WeatherDto;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    private WeatherDto cached;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String OPEN_METEO_URL =
        "https://api.open-meteo.com/v1/forecast?latitude=33.57&longitude=-7.59" +
        "&current=temperature_2m,relative_humidity_2m,windspeed_10m,apparent_temperature";

    @Scheduled(fixedDelay = 3600000)
    public void refresh() {
        try {
            JsonNode root = restTemplate.getForObject(OPEN_METEO_URL, JsonNode.class);
            if (root != null) {
                JsonNode current = root.path("current");
                cached = WeatherDto.builder()
                    .temperatureC(current.path("temperature_2m").asDouble(20))
                    .humidity(current.path("relative_humidity_2m").asInt(60))
                    .feelsLikeC(current.path("apparent_temperature").asDouble(20))
                    .windKmh(current.path("windspeed_10m").asDouble(0))
                    .build();
            }
        } catch (Exception e) {
            // keep cached value or default
        }
    }

    public WeatherDto getCurrentWeather() {
        if (cached == null) refresh();
        if (cached == null) {
            return WeatherDto.builder().temperatureC(22).humidity(65).feelsLikeC(22).windKmh(10).build();
        }
        return cached;
    }

    public int adjustPace(int targetPaceSecPerKm, double temperatureC, int humidity) {
        int tempBonus = (int)(Math.max(0, (temperatureC - 20) / 5) * 5);
        int humBonus = humidity > 70 ? 3 : 0;
        return targetPaceSecPerKm + tempBonus + humBonus;
    }
}
