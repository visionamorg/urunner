import { Component, OnInit, OnDestroy, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ActivityService } from '../../../core/services/activity.service';
import { Activity, ActivityInsight, ActivityChatMessage, ActivitySplit } from '../../../core/models/activity.model';
import * as L from 'leaflet';

@Component({
  selector: 'app-activity-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './activity-detail.component.html',
  styleUrl: './activity-detail.component.scss'
})
export class ActivityDetailComponent implements OnInit, OnDestroy {
  @ViewChild('chatContainer') chatContainer!: ElementRef;
  @ViewChild('mapContainer') mapContainer!: ElementRef;

  activity: Activity | null = null;
  insight: ActivityInsight | null = null;
  loading = true;
  analyzing = false;
  chatMessages: ActivityChatMessage[] = [];
  chatInput = '';
  chatLoading = false;
  showChat = false;
  copiedCaption = false;

  private map: L.Map | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private activityService: ActivityService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) {
      this.router.navigate(['/activities']);
      return;
    }

    this.activityService.getActivityById(id).subscribe({
      next: a => {
        this.activity = a;
        this.loading = false;
        this.loadInsight(id);
        setTimeout(() => this.initMap(), 100);
      },
      error: () => {
        this.loading = false;
        this.router.navigate(['/activities']);
      }
    });
  }

  ngOnDestroy(): void {
    if (this.map) {
      this.map.remove();
      this.map = null;
    }
  }

  initMap(): void {
    if (!this.activity?.mapPolyline || !this.mapContainer) return;

    const coords = this.decodePolyline(this.activity.mapPolyline);
    if (coords.length === 0) return;

    this.map = L.map(this.mapContainer.nativeElement, {
      zoomControl: true,
      attributionControl: false
    });

    L.tileLayer('https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png', {
      maxZoom: 19
    }).addTo(this.map);

    const polyline = L.polyline(coords, {
      color: '#f97316',
      weight: 4,
      opacity: 0.9
    }).addTo(this.map);

    this.map.fitBounds(polyline.getBounds(), { padding: [30, 30] });

    // Start/end markers
    const startIcon = L.divIcon({
      html: '<div style="width:12px;height:12px;background:#22c55e;border-radius:50%;border:2px solid #fff;"></div>',
      className: '',
      iconSize: [12, 12]
    });
    const endIcon = L.divIcon({
      html: '<div style="width:12px;height:12px;background:#ef4444;border-radius:50%;border:2px solid #fff;"></div>',
      className: '',
      iconSize: [12, 12]
    });
    L.marker(coords[0], { icon: startIcon }).addTo(this.map);
    L.marker(coords[coords.length - 1], { icon: endIcon }).addTo(this.map);
  }

  decodePolyline(encoded: string): L.LatLngTuple[] {
    const coords: L.LatLngTuple[] = [];
    let index = 0, lat = 0, lng = 0;
    while (index < encoded.length) {
      let b: number, shift = 0, result = 0;
      do {
        b = encoded.charCodeAt(index++) - 63;
        result |= (b & 0x1f) << shift;
        shift += 5;
      } while (b >= 0x20);
      lat += (result & 1) ? ~(result >> 1) : (result >> 1);

      shift = 0; result = 0;
      do {
        b = encoded.charCodeAt(index++) - 63;
        result |= (b & 0x1f) << shift;
        shift += 5;
      } while (b >= 0x20);
      lng += (result & 1) ? ~(result >> 1) : (result >> 1);

      coords.push([lat / 1e5, lng / 1e5]);
    }
    return coords;
  }

  get hasTelemetry(): boolean {
    if (!this.activity) return false;
    return !!(this.activity.elevationGainMeters || this.activity.avgHeartRate ||
              this.activity.avgCadence || this.activity.mapPolyline);
  }

  get fastestSplit(): ActivitySplit | null {
    if (!this.activity?.splits?.length) return null;
    return this.activity.splits.reduce((f, s) =>
      s.splitPace > 0 && (f.splitPace === 0 || s.splitPace < f.splitPace) ? s : f
    );
  }

  loadInsight(id: number): void {
    this.activityService.getActivityInsight(id).subscribe({
      next: insight => this.insight = insight,
      error: () => {}
    });
  }

  analyzeActivity(): void {
    if (!this.activity || this.analyzing) return;
    this.analyzing = true;
    this.activityService.analyzeActivity(this.activity.id).subscribe({
      next: insight => {
        this.insight = insight;
        this.analyzing = false;
      },
      error: () => { this.analyzing = false; }
    });
  }

  sendChatMessage(): void {
    if (!this.activity || !this.chatInput.trim() || this.chatLoading) return;

    const userMsg = this.chatInput.trim();
    this.chatMessages.push({ role: 'user', content: userMsg });
    this.chatInput = '';
    this.chatLoading = true;

    this.scrollChatToBottom();

    const history = this.chatMessages.slice(0, -1);

    this.activityService.chatAboutActivity(this.activity.id, {
      message: userMsg,
      history
    }).subscribe({
      next: res => {
        this.chatMessages.push({ role: 'assistant', content: res.reply });
        this.chatLoading = false;
        this.scrollChatToBottom();
      },
      error: () => {
        this.chatMessages.push({ role: 'assistant', content: 'Sorry, I could not process your question right now. Please try again.' });
        this.chatLoading = false;
        this.scrollChatToBottom();
      }
    });
  }

  scrollChatToBottom(): void {
    setTimeout(() => {
      if (this.chatContainer) {
        this.chatContainer.nativeElement.scrollTop = this.chatContainer.nativeElement.scrollHeight;
      }
    }, 50);
  }

  copyCaption(): void {
    if (!this.insight?.socialCaption) return;
    navigator.clipboard.writeText(this.insight.socialCaption);
    this.copiedCaption = true;
    setTimeout(() => this.copiedCaption = false, 2000);
  }

  formatPace(pace: number): string {
    if (!pace) return '--';
    const min = Math.floor(pace);
    const sec = Math.round((pace - min) * 60);
    return `${min}:${sec.toString().padStart(2, '0')} /km`;
  }

  formatPaceShort(pace: number): string {
    if (!pace) return '--';
    const min = Math.floor(pace);
    const sec = Math.round((pace - min) * 60);
    return `${min}:${sec.toString().padStart(2, '0')}`;
  }

  formatDuration(min: number): string {
    const h = Math.floor(min / 60);
    const m = min % 60;
    return h > 0 ? `${h}h ${m}m` : `${m}m`;
  }

  getIntensityColor(intensity: string): string {
    const colors: Record<string, string> = {
      'EASY': 'bg-green-500/20 text-green-400 border-green-500/30',
      'MODERATE': 'bg-blue-500/20 text-blue-400 border-blue-500/30',
      'TEMPO': 'bg-yellow-500/20 text-yellow-400 border-yellow-500/30',
      'THRESHOLD': 'bg-orange-500/20 text-orange-400 border-orange-500/30',
      'HARD': 'bg-red-500/20 text-red-400 border-red-500/30',
      'RACE': 'bg-purple-500/20 text-purple-400 border-purple-500/30'
    };
    return colors[intensity] || colors['MODERATE'];
  }

  getSplitBarWidth(split: ActivitySplit): number {
    if (!this.activity?.splits?.length || !split.splitPace) return 0;
    const paces = this.activity.splits.filter(s => s.splitPace > 0).map(s => s.splitPace);
    const min = Math.min(...paces);
    const max = Math.max(...paces);
    if (max === min) return 50;
    // Invert: fastest = widest bar
    return 20 + (1 - (split.splitPace - min) / (max - min)) * 80;
  }

  getSplitBarColor(split: ActivitySplit): string {
    if (!this.activity?.splits?.length || !split.splitPace) return 'bg-blue-500';
    const paces = this.activity.splits.filter(s => s.splitPace > 0).map(s => s.splitPace);
    const min = Math.min(...paces);
    const max = Math.max(...paces);
    if (max === min) return 'bg-blue-500';
    const ratio = (split.splitPace - min) / (max - min);
    if (ratio < 0.33) return 'bg-green-500';
    if (ratio < 0.66) return 'bg-yellow-500';
    return 'bg-orange-500';
  }

  openExportStudio(): void {
    if (this.activity) {
      this.router.navigate(['/export-studio'], { queryParams: { activityId: this.activity.id } });
    }
  }
}
