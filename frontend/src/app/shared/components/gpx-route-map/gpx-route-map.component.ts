import { Component, Input, OnChanges, SimpleChanges, ElementRef, ViewChild, AfterViewInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import * as L from 'leaflet';

interface GpxPoint {
  lat: number;
  lon: number;
  ele: number;
  dist: number; // cumulative distance in km
}

@Component({
  selector: 'app-gpx-route-map',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="rounded-xl overflow-hidden border border-border">
      <!-- Map -->
      <div #mapContainer class="w-full" style="height: 320px; z-index: 0;"></div>

      <!-- Elevation Profile -->
      @if (points.length > 0) {
        <div class="bg-card border-t border-border p-4">
          <div class="flex items-center justify-between mb-3">
            <div class="flex items-center gap-2">
              <span class="material-icons text-primary text-sm">terrain</span>
              <span class="text-foreground text-xs font-semibold">Elevation Profile</span>
            </div>
            <div class="flex gap-4 text-xs text-muted-foreground">
              <span>↑ {{ elevationGain | number:'1.0-0' }}m gain</span>
              <span>↓ {{ elevationLoss | number:'1.0-0' }}m loss</span>
              <span>{{ totalDistance | number:'1.1-1' }} km</span>
            </div>
          </div>
          <div class="relative h-24 cursor-crosshair" #chartContainer
               (mousemove)="onChartHover($event)" (mouseleave)="hoverIndex = -1">
            <canvas #chartCanvas class="w-full h-full"></canvas>
            @if (hoverIndex >= 0 && hoverIndex < points.length) {
              <div class="absolute top-0 bottom-0 w-px bg-primary/60 pointer-events-none"
                   [style.left.%]="(hoverIndex / (points.length - 1)) * 100">
              </div>
              <div class="absolute top-1 px-2 py-1 bg-card border border-border rounded text-xs text-foreground pointer-events-none whitespace-nowrap"
                   [style.left.px]="hoverX" [class.translate-x-[-100%]]="hoverRight">
                {{ points[hoverIndex].ele | number:'1.0-0' }}m · {{ points[hoverIndex].dist | number:'1.1-1' }}km
              </div>
            }
          </div>
        </div>
      }
    </div>
  `
})
export class GpxRouteMapComponent implements OnChanges, AfterViewInit, OnDestroy {
  @Input() gpxUrl!: string;
  @ViewChild('mapContainer') mapContainer!: ElementRef;
  @ViewChild('chartCanvas') chartCanvas!: ElementRef<HTMLCanvasElement>;
  @ViewChild('chartContainer') chartContainer!: ElementRef;

  private map: L.Map | null = null;
  private routeLayer: L.Polyline | null = null;
  private hoverMarker: L.CircleMarker | null = null;

  points: GpxPoint[] = [];
  elevationGain = 0;
  elevationLoss = 0;
  totalDistance = 0;
  hoverIndex = -1;
  hoverX = 0;
  hoverRight = false;

  constructor(private http: HttpClient) {}

  ngAfterViewInit(): void {
    this.initMap();
    if (this.gpxUrl) this.loadGpx();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['gpxUrl'] && !changes['gpxUrl'].firstChange && this.map) {
      this.loadGpx();
    }
  }

  ngOnDestroy(): void {
    this.map?.remove();
  }

  private initMap(): void {
    this.map = L.map(this.mapContainer.nativeElement, {
      zoomControl: true,
      attributionControl: false
    }).setView([0, 0], 2);

    L.tileLayer('https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png', {
      maxZoom: 19
    }).addTo(this.map);
  }

  private loadGpx(): void {
    this.http.get(this.gpxUrl, { responseType: 'text' }).subscribe({
      next: xml => {
        this.parseAndRender(xml);
      }
    });
  }

  private parseAndRender(xml: string): void {
    const parser = new DOMParser();
    const doc = parser.parseFromString(xml, 'text/xml');
    let trkpts = Array.from(doc.getElementsByTagName('trkpt'));
    if (trkpts.length === 0) trkpts = Array.from(doc.getElementsByTagName('rtept'));

    this.points = [];
    let cumDist = 0;
    this.elevationGain = 0;
    this.elevationLoss = 0;

    for (let i = 0; i < trkpts.length; i++) {
      const pt = trkpts[i];
      const lat = parseFloat(pt.getAttribute('lat') || '0');
      const lon = parseFloat(pt.getAttribute('lon') || '0');
      const eleEl = pt.getElementsByTagName('ele')[0];
      const ele = eleEl ? parseFloat(eleEl.textContent || '0') : 0;

      if (i > 0) {
        const prev = this.points[i - 1];
        cumDist += this.haversine(prev.lat, prev.lon, lat, lon);
        const diff = ele - prev.ele;
        if (diff > 0) this.elevationGain += diff;
        else this.elevationLoss += Math.abs(diff);
      }

      this.points.push({ lat, lon, ele, dist: cumDist });
    }

    this.totalDistance = cumDist;
    this.renderRoute();
    this.renderChart();
  }

  private renderRoute(): void {
    if (!this.map || this.points.length === 0) return;

    if (this.routeLayer) this.map.removeLayer(this.routeLayer);

    const latlngs = this.points.map(p => L.latLng(p.lat, p.lon));
    this.routeLayer = L.polyline(latlngs, {
      color: '#3b82f6',
      weight: 4,
      opacity: 0.9
    }).addTo(this.map);

    // Start/end markers
    const startIcon = L.divIcon({ className: '', html: '<div style="width:12px;height:12px;border-radius:50%;background:#22c55e;border:2px solid white;"></div>' });
    const endIcon = L.divIcon({ className: '', html: '<div style="width:12px;height:12px;border-radius:50%;background:#ef4444;border:2px solid white;"></div>' });
    L.marker(latlngs[0], { icon: startIcon }).addTo(this.map);
    L.marker(latlngs[latlngs.length - 1], { icon: endIcon }).addTo(this.map);

    this.map.fitBounds(this.routeLayer.getBounds(), { padding: [30, 30] });
  }

  private renderChart(): void {
    if (!this.chartCanvas || this.points.length < 2) return;

    const canvas = this.chartCanvas.nativeElement;
    const rect = canvas.parentElement!.getBoundingClientRect();
    canvas.width = rect.width * 2;
    canvas.height = rect.height * 2;
    const ctx = canvas.getContext('2d')!;
    ctx.scale(2, 2);

    const w = rect.width;
    const h = rect.height;
    const eles = this.points.map(p => p.ele);
    const minEle = Math.min(...eles) - 10;
    const maxEle = Math.max(...eles) + 10;
    const range = maxEle - minEle || 1;

    // Fill gradient
    const grad = ctx.createLinearGradient(0, 0, 0, h);
    grad.addColorStop(0, 'rgba(59, 130, 246, 0.3)');
    grad.addColorStop(1, 'rgba(59, 130, 246, 0.02)');

    ctx.beginPath();
    ctx.moveTo(0, h);
    for (let i = 0; i < this.points.length; i++) {
      const x = (i / (this.points.length - 1)) * w;
      const y = h - ((eles[i] - minEle) / range) * (h - 4);
      if (i === 0) ctx.lineTo(x, y);
      else ctx.lineTo(x, y);
    }
    ctx.lineTo(w, h);
    ctx.closePath();
    ctx.fillStyle = grad;
    ctx.fill();

    // Line
    ctx.beginPath();
    for (let i = 0; i < this.points.length; i++) {
      const x = (i / (this.points.length - 1)) * w;
      const y = h - ((eles[i] - minEle) / range) * (h - 4);
      if (i === 0) ctx.moveTo(x, y);
      else ctx.lineTo(x, y);
    }
    ctx.strokeStyle = '#3b82f6';
    ctx.lineWidth = 1.5;
    ctx.stroke();
  }

  onChartHover(event: MouseEvent): void {
    if (this.points.length === 0 || !this.chartContainer) return;
    const rect = this.chartContainer.nativeElement.getBoundingClientRect();
    const x = event.clientX - rect.left;
    const pct = x / rect.width;
    this.hoverIndex = Math.round(pct * (this.points.length - 1));
    this.hoverIndex = Math.max(0, Math.min(this.hoverIndex, this.points.length - 1));
    this.hoverX = x;
    this.hoverRight = x > rect.width * 0.7;

    // Show marker on map
    if (this.map) {
      const pt = this.points[this.hoverIndex];
      if (this.hoverMarker) this.map.removeLayer(this.hoverMarker);
      this.hoverMarker = L.circleMarker([pt.lat, pt.lon], {
        radius: 5, color: '#3b82f6', fillColor: '#fff', fillOpacity: 1, weight: 2
      }).addTo(this.map);
    }
  }

  private haversine(lat1: number, lon1: number, lat2: number, lon2: number): number {
    const R = 6371;
    const dLat = (lat2 - lat1) * Math.PI / 180;
    const dLon = (lon2 - lon1) * Math.PI / 180;
    const a = Math.sin(dLat / 2) ** 2 +
              Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
              Math.sin(dLon / 2) ** 2;
    return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  }
}
