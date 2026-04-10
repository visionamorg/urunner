import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-weather-widget',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div *ngIf="weather" class="card p-3 flex items-center gap-3">
      <span class="text-2xl">{{ getIcon() }}</span>
      <div>
        <p class="text-sm font-medium text-foreground">{{ weather.temperatureC | number:'1.0-0' }}°C · {{ weather.humidity }}% humidity</p>
        <p class="text-xs text-muted-foreground">Casablanca · Feels like {{ weather.feelsLikeC | number:'1.0-0' }}°C · 💨 {{ weather.windKmh | number:'1.0-0' }} km/h</p>
      </div>
      <div *ngIf="weather.temperatureC > 28" class="ml-auto text-xs px-2 py-1 bg-orange-500/10 text-orange-400 rounded-lg">
        🔥 Heat-adjusted pace active
      </div>
    </div>
  `
})
export class WeatherWidgetComponent implements OnInit {
  weather: any = null;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.http.get<any>('/api/weather/current').subscribe({
      next: w => { this.weather = w; },
      error: () => {}
    });
  }

  getIcon(): string {
    if (!this.weather) return '🌡️';
    if (this.weather.temperatureC > 35) return '🔥';
    if (this.weather.temperatureC > 28) return '☀️';
    if (this.weather.temperatureC > 20) return '⛅';
    return '🌤️';
  }
}
