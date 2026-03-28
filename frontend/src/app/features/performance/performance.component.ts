import { Component, OnInit, ElementRef, ViewChild, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivityService } from '../../core/services/activity.service';
import { PerformanceData, DailyMetric } from '../../core/models/activity.model';

@Component({
  selector: 'app-performance',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './performance.component.html'
})
export class PerformanceComponent implements OnInit {
  Math = Math;
  data: PerformanceData | null = null;
  loading = true;
  showTaper = false;

  // Chart dimensions
  chartWidth = 800;
  chartHeight = 300;
  padding = { top: 20, right: 20, bottom: 30, left: 50 };

  constructor(private activityService: ActivityService) {}

  ngOnInit(): void {
    this.activityService.getPerformance().subscribe({
      next: d => { this.data = d; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  toggleTaper(): void {
    this.showTaper = !this.showTaper;
  }

  getZoneColor(): string {
    if (!this.data) return '';
    switch (this.data.trainingZone) {
      case 'OPTIMAL': return 'text-green-400';
      case 'OVERREACHING': return 'text-red-400';
      case 'RECOVERY': return 'text-blue-400';
      case 'DETRAINING': return 'text-yellow-400';
      default: return 'text-muted-foreground';
    }
  }

  getZoneBg(): string {
    if (!this.data) return '';
    switch (this.data.trainingZone) {
      case 'OPTIMAL': return 'bg-green-500/10 border-green-500/20';
      case 'OVERREACHING': return 'bg-red-500/10 border-red-500/20';
      case 'RECOVERY': return 'bg-blue-500/10 border-blue-500/20';
      case 'DETRAINING': return 'bg-yellow-500/10 border-yellow-500/20';
      default: return '';
    }
  }

  getZoneDescription(): string {
    if (!this.data) return '';
    switch (this.data.trainingZone) {
      case 'OPTIMAL': return 'Your fitness and fatigue are balanced. Good for quality training.';
      case 'OVERREACHING': return 'Fatigue exceeds fitness. Consider reducing volume to prevent injury.';
      case 'RECOVERY': return 'High form — you are well-rested. Great time for racing or key sessions.';
      case 'DETRAINING': return 'Training load is low. Gradually increase volume to build fitness.';
      default: return '';
    }
  }

  // ── SVG Chart Helpers ──────────────────────────────────────────

  getVisibleHistory(): DailyMetric[] {
    if (!this.data) return [];
    // Show last 60 days for readability
    return this.data.history.slice(-60);
  }

  getAllPoints(): DailyMetric[] {
    const hist = this.getVisibleHistory();
    if (this.showTaper && this.data?.taperSimulation) {
      return [...hist, ...this.data.taperSimulation];
    }
    return hist;
  }

  getYMin(): number {
    const points = this.getAllPoints();
    if (points.length === 0) return -20;
    return Math.min(...points.map(p => Math.min(p.ctl, p.atl, p.tsb))) - 5;
  }

  getYMax(): number {
    const points = this.getAllPoints();
    if (points.length === 0) return 100;
    return Math.max(...points.map(p => Math.max(p.ctl, p.atl, p.tsb))) + 10;
  }

  scaleX(index: number, total: number): number {
    const w = this.chartWidth - this.padding.left - this.padding.right;
    return this.padding.left + (index / Math.max(1, total - 1)) * w;
  }

  scaleY(value: number): number {
    const h = this.chartHeight - this.padding.top - this.padding.bottom;
    const yMin = this.getYMin();
    const yMax = this.getYMax();
    const range = yMax - yMin || 1;
    return this.padding.top + h - ((value - yMin) / range) * h;
  }

  buildPath(points: DailyMetric[], field: 'ctl' | 'atl' | 'tsb'): string {
    if (points.length === 0) return '';
    return points.map((p, i) => {
      const x = this.scaleX(i, points.length);
      const y = this.scaleY(p[field]);
      return (i === 0 ? 'M' : 'L') + x.toFixed(1) + ',' + y.toFixed(1);
    }).join(' ');
  }

  getCtlPath(): string { return this.buildPath(this.getAllPoints(), 'ctl'); }
  getAtlPath(): string { return this.buildPath(this.getAllPoints(), 'atl'); }
  getTsbPath(): string { return this.buildPath(this.getAllPoints(), 'tsb'); }

  // Taper simulation paths (dashed continuation)
  getTaperCtlPath(): string {
    if (!this.showTaper || !this.data?.taperSimulation) return '';
    const hist = this.getVisibleHistory();
    const startIdx = hist.length - 1;
    const all = this.getAllPoints();
    const taperPoints = all.slice(startIdx);
    return taperPoints.map((p, i) => {
      const x = this.scaleX(startIdx + i, all.length);
      const y = this.scaleY(p.ctl);
      return (i === 0 ? 'M' : 'L') + x.toFixed(1) + ',' + y.toFixed(1);
    }).join(' ');
  }

  getTaperTsbPath(): string {
    if (!this.showTaper || !this.data?.taperSimulation) return '';
    const hist = this.getVisibleHistory();
    const startIdx = hist.length - 1;
    const all = this.getAllPoints();
    const taperPoints = all.slice(startIdx);
    return taperPoints.map((p, i) => {
      const x = this.scaleX(startIdx + i, all.length);
      const y = this.scaleY(p.tsb);
      return (i === 0 ? 'M' : 'L') + x.toFixed(1) + ',' + y.toFixed(1);
    }).join(' ');
  }

  getZeroLineY(): number {
    return this.scaleY(0);
  }

  getYGridLines(): number[] {
    const yMin = this.getYMin();
    const yMax = this.getYMax();
    const step = Math.max(10, Math.round((yMax - yMin) / 5 / 10) * 10);
    const lines: number[] = [];
    for (let v = Math.ceil(yMin / step) * step; v <= yMax; v += step) {
      lines.push(v);
    }
    return lines;
  }

  getXLabels(): { x: number; label: string }[] {
    const points = this.getAllPoints();
    if (points.length === 0) return [];
    const labels: { x: number; label: string }[] = [];
    const step = Math.max(1, Math.floor(points.length / 6));
    for (let i = 0; i < points.length; i += step) {
      const d = new Date(points[i].date);
      labels.push({
        x: this.scaleX(i, points.length),
        label: (d.getMonth() + 1) + '/' + d.getDate()
      });
    }
    return labels;
  }

  getTaperProjectedTSB(): number | null {
    if (!this.data?.taperSimulation || this.data.taperSimulation.length === 0) return null;
    return this.data.taperSimulation[this.data.taperSimulation.length - 1].tsb;
  }
}
