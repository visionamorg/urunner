import { Component, OnInit } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-segments',
  standalone: true,
  imports: [CommonModule, DecimalPipe],
  template: `
    <div class="max-w-4xl mx-auto">
      <div class="mb-6">
        <h1 class="text-2xl font-bold text-foreground">City Segments</h1>
        <p class="text-muted-foreground mt-1">Official Urban Runners Casablanca routes</p>
      </div>
      <div *ngIf="loading" class="flex items-center justify-center h-40">
        <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
      </div>
      <div class="grid gap-4" *ngIf="!loading">
        <div *ngFor="let seg of segments" class="card p-5 hover:border-primary/30 transition-colors cursor-pointer">
          <div class="flex items-start justify-between">
            <div class="flex-1">
              <div class="flex items-center gap-2 mb-1">
                <h3 class="font-semibold text-foreground">{{ seg.name }}</h3>
                <span class="text-xs px-2 py-0.5 rounded-full font-medium"
                  [ngClass]="seg.difficulty === 'EASY' ? 'bg-green-500/10 text-green-400' :
                              seg.difficulty === 'HARD' ? 'bg-red-500/10 text-red-400' :
                              'bg-yellow-500/10 text-yellow-400'">
                  {{ seg.difficulty }}
                </span>
              </div>
              <p class="text-sm text-muted-foreground mb-3">{{ seg.description }}</p>
              <div class="flex gap-4 text-sm">
                <span class="text-muted-foreground">📏 {{ seg.distanceKm }} km</span>
                <span *ngIf="seg.komUsername" class="text-yellow-400">
                  👑 KOM: {{ seg.komUsername }} — {{ formatTime(seg.komElapsedSeconds) }}
                </span>
                <span *ngIf="!seg.komUsername" class="text-muted-foreground">No efforts yet</span>
              </div>
            </div>
            <div *ngIf="seg.myBestSeconds" class="text-right ml-4">
              <p class="text-xs text-muted-foreground">Your best</p>
              <p class="text-lg font-bold text-primary">{{ formatTime(seg.myBestSeconds) }}</p>
            </div>
          </div>
        </div>
        <div *ngIf="segments.length === 0" class="card p-8 text-center text-muted-foreground">
          <span class="material-icons text-4xl mb-2 block">straighten</span>
          <p>No segments yet. Sync a Garmin activity to record efforts!</p>
        </div>
      </div>
    </div>
  `
})
export class SegmentsComponent implements OnInit {
  segments: any[] = [];
  loading = true;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.http.get<any[]>('/api/segments').subscribe({
      next: s => { this.segments = s; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  formatTime(seconds: number | null): string {
    if (!seconds) return '--';
    const m = Math.floor(seconds / 60);
    const s = seconds % 60;
    return `${m}:${s.toString().padStart(2, '0')}`;
  }
}
