package com.runhub.weather.controller;

import com.runhub.weather.dto.WeatherDto;
import com.runhub.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/current")
    public ResponseEntity<WeatherDto> getCurrent() {
        return ResponseEntity.ok(weatherService.getCurrentWeather());
    }
}
