import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ProgramService } from '../../../core/services/program.service';
import { ProgramSession, ProgramProgress } from '../../../core/models/program.model';

interface GeoTrackPoint {
  lat: number;
  lng: number;
  timestamp: number;
  distanceKm: number;
  paceMinPerKm: number | null;
}

@Component({
  selector: 'app-live-session',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './live-session.component.html'
})
export class LiveSessionComponent implements OnInit, OnDestroy {
  Math = Math;
  programId!: number;
  session: ProgramSession | null = null;
  loading = true;
  noSession = false;

  // Live tracking state
  isRunning = false;
  isPaused = false;
  isCompleted = false;

  // Timer
  elapsedSeconds = 0;
  private timerInterval: any;

  // Geolocation
  private watchId: number | null = null;
  trackPoints: GeoTrackPoint[] = [];
  currentPace: number | null = null;  // min/km
  averagePace: number | null = null;
  totalDistanceKm = 0;
  geoError: string | null = null;

  // Target comparison
  targetPaceMinPerKm: number | null = null;
  paceStatus: 'on-target' | 'too-fast' | 'too-slow' | 'unknown' = 'unknown';

  // Post-run
  completedProgress: ProgramProgress | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private programService: ProgramService
  ) {}

  ngOnInit(): void {
    this.programId = +this.route.snapshot.paramMap.get('id')!;
    this.programService.getTodaySession(this.programId).subscribe({
      next: (s) => {
        this.session = s;
        this.loading = false;
        this.calculateTargetPace();
      },
      error: () => {
        this.loading = false;
        this.noSession = true;
      }
    });
  }

  ngOnDestroy(): void {
    this.stopTracking();
  }

  calculateTargetPace(): void {
    if (!this.session) return;
    if (this.session.distanceKm && this.session.durationMinutes && this.session.distanceKm > 0) {
      this.targetPaceMinPerKm = this.session.durationMinutes / this.session.distanceKm;
    }
  }

  // ── Controls ──────────────────────────────────────────────────

  startRun(): void {
    this.isRunning = true;
    this.isPaused = false;
    this.elapsedSeconds = 0;
    this.trackPoints = [];
    this.totalDistanceKm = 0;
    this.startTimer();
    this.startGeolocation();
    this.speak('Starting workout: ' + (this.session?.title || 'Run'));
  }

  pauseRun(): void {
    this.isPaused = true;
    this.stopTimer();
    if (this.watchId !== null) {
      navigator.geolocation.clearWatch(this.watchId);
      this.watchId = null;
    }
  }

  resumeRun(): void {
    this.isPaused = false;
    this.startTimer();
    this.startGeolocation();
    this.speak('Resuming workout');
  }

  finishRun(): void {
    this.isRunning = false;
    this.isCompleted = true;
    this.stopTracking();
    this.speak('Workout complete! Great job!');

    // Mark session as completed
    this.programService.completeSession(this.programId).subscribe({
      next: (progress) => this.completedProgress = progress,
      error: () => {}
    });
  }

  goBack(): void {
    this.router.navigate(['/programs']);
  }

  // ── Timer ──────────────────────────────────────────────────

  private startTimer(): void {
    this.timerInterval = setInterval(() => {
      this.elapsedSeconds++;
    }, 1000);
  }

  private stopTimer(): void {
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
      this.timerInterval = null;
    }
  }

  private stopTracking(): void {
    this.stopTimer();
    if (this.watchId !== null) {
      navigator.geolocation.clearWatch(this.watchId);
      this.watchId = null;
    }
  }

  formatTime(seconds: number): string {
    const h = Math.floor(seconds / 3600);
    const m = Math.floor((seconds % 3600) / 60);
    const s = seconds % 60;
    if (h > 0) return `${h}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
    return `${m}:${s.toString().padStart(2, '0')}`;
  }

  formatPace(pace: number | null): string {
    if (!pace || pace <= 0 || !isFinite(pace)) return '--:--';
    const m = Math.floor(pace);
    const s = Math.round((pace - m) * 60);
    return `${m}:${s.toString().padStart(2, '0')}`;
  }

  // ── Geolocation ──────────────────────────────────────────────

  private startGeolocation(): void {
    if (!navigator.geolocation) {
      this.geoError = 'Geolocation not supported';
      return;
    }

    this.watchId = navigator.geolocation.watchPosition(
      (pos) => this.onPosition(pos),
      (err) => { this.geoError = err.message; },
      { enableHighAccuracy: true, maximumAge: 3000, timeout: 10000 }
    );
  }

  private onPosition(pos: GeolocationPosition): void {
    const point: GeoTrackPoint = {
      lat: pos.coords.latitude,
      lng: pos.coords.longitude,
      timestamp: pos.timestamp,
      distanceKm: 0,
      paceMinPerKm: null
    };

    if (this.trackPoints.length > 0) {
      const last = this.trackPoints[this.trackPoints.length - 1];
      const dist = this.haversine(last.lat, last.lng, point.lat, point.lng);

      // Filter out GPS noise (< 5m or > 500m jumps)
      if (dist < 0.005 || dist > 0.5) return;

      this.totalDistanceKm += dist;
      point.distanceKm = this.totalDistanceKm;

      // Calculate instantaneous pace
      const timeDiffMin = (point.timestamp - last.timestamp) / 60000;
      if (timeDiffMin > 0 && dist > 0) {
        point.paceMinPerKm = timeDiffMin / dist;
        this.currentPace = point.paceMinPerKm;
      }

      // Average pace
      if (this.totalDistanceKm > 0 && this.elapsedSeconds > 0) {
        this.averagePace = (this.elapsedSeconds / 60) / this.totalDistanceKm;
      }

      // Pace status
      this.updatePaceStatus();
    }

    this.trackPoints.push(point);
  }

  private updatePaceStatus(): void {
    if (!this.targetPaceMinPerKm || !this.currentPace) {
      this.paceStatus = 'unknown';
      return;
    }
    const diff = this.currentPace - this.targetPaceMinPerKm;
    const tolerance = 0.3; // 18 seconds per km tolerance
    if (Math.abs(diff) <= tolerance) this.paceStatus = 'on-target';
    else if (diff < -tolerance) this.paceStatus = 'too-fast';
    else this.paceStatus = 'too-slow';
  }

  private haversine(lat1: number, lon1: number, lat2: number, lon2: number): number {
    const R = 6371;
    const dLat = (lat2 - lat1) * Math.PI / 180;
    const dLon = (lon2 - lon1) * Math.PI / 180;
    const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
      Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
      Math.sin(dLon / 2) * Math.sin(dLon / 2);
    return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  }

  // ── Text-to-Speech ──────────────────────────────────────────

  private speak(text: string): void {
    if ('speechSynthesis' in window) {
      const utterance = new SpeechSynthesisUtterance(text);
      utterance.rate = 0.9;
      utterance.lang = 'en-US';
      speechSynthesis.speak(utterance);
    }
  }

  // ── Progress ──────────────────────────────────────────────────

  getDistanceProgress(): number {
    if (!this.session?.distanceKm || this.session.distanceKm <= 0) return 0;
    return Math.min(100, (this.totalDistanceKm / this.session.distanceKm) * 100);
  }

  getTimeProgress(): number {
    if (!this.session?.durationMinutes || this.session.durationMinutes <= 0) return 0;
    return Math.min(100, (this.elapsedSeconds / 60 / this.session.durationMinutes) * 100);
  }

  getPaceStatusColor(): string {
    switch (this.paceStatus) {
      case 'on-target': return 'text-green-400';
      case 'too-fast': return 'text-yellow-400';
      case 'too-slow': return 'text-red-400';
      default: return 'text-muted-foreground';
    }
  }

  getPaceStatusLabel(): string {
    switch (this.paceStatus) {
      case 'on-target': return 'ON TARGET';
      case 'too-fast': return 'TOO FAST';
      case 'too-slow': return 'TOO SLOW';
      default: return '';
    }
  }
}
