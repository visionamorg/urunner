# US-015 — Weather-Adaptive Training Suggestions

**Status:** [ ] Pending
**Priority:** 🟢 Low

---

## Problem

As described in the strategy doc, Casablanca can be hot and humid. A 4:30/km pace in 20°C becomes physiologically equivalent to ~4:45/km at 30°C. Runners following a training plan don't know to adjust. RunHub can be the first platform to give weather-adjusted target paces automatically.

---

## Story

As a **runner training in Casablanca**, I want my training program sessions to show a weather-adjusted target pace for today's conditions, so I train at the right physiological effort regardless of temperature.

---

## Acceptance Criteria

### Weather Widget on Dashboard
- [ ] A small weather widget on the dashboard showing: Temperature, Humidity, Feels Like, Wind
- [ ] Weather data for Casablanca (lat: 33.5731, lng: -7.5898)
- [ ] Uses Open-Meteo API (free, no key required): `https://api.open-meteo.com/v1/forecast?latitude=33.57&longitude=-7.59&current=temperature_2m,relative_humidity_2m,windspeed_10m,apparent_temperature`

### Heat-Adjusted Pace on Training Sessions
- [ ] Training programs page: when viewing today's session, shows original target pace AND heat-adjusted pace
- [ ] Heat adjustment formula:
  - Base: each +5°C above 20°C adds ~5 seconds/km to target pace
  - Humidity modifier: if humidity > 70%, add 3 additional seconds/km
  - Example: 20°C = 4:30/km → 30°C = 4:40/km → 30°C + 80% humidity = 4:43/km
- [ ] Displayed as: "Target pace: 4:30/km | Today's adjusted: 4:43/km (30°C, 80% humidity)"
- [ ] Adjustment badge: green if no change, orange if adjusted, red if very hot (> 35°C)

### AI Coach Integration
- [ ] When current temperature > 28°C, AI coach context (US-009) automatically includes a weather note
- [ ] AI responses factor in heat when suggesting workouts

### Settings — Location
- [ ] User can set their city in profile settings (default: Casablanca)
- [ ] Weather API call uses the city's coordinates

---

## Technical Notes

### Backend
- `WeatherService`:
  - Calls Open-Meteo API (no key needed)
  - Caches response for 1 hour in memory (simple `@Scheduled` refresh or cache-aside)
  - Returns `WeatherDto { temperatureC, humidity, feelsLikeC, windKmh }`
- `GET /api/weather/current` endpoint — proxied through backend to avoid CORS in frontend
- `PaceAdjustmentService.adjust(targetPaceSecPerKm, temperatureC, humidity)` — pure function

### Frontend
- `WeatherService` in `core/services/weather.service.ts`
- Weather widget component `shared/components/weather-widget/weather-widget.component.ts`
- Training programs page: inject `WeatherService`, calculate adjusted pace inline
- No external Angular library needed

---

## No DB Migration Required

Weather is fetched live and pace adjustment is calculated in-memory.
