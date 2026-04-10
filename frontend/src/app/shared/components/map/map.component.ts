import { Component, Input, OnChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-map',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="w-full h-64 rounded-xl overflow-hidden border border-border">
      <iframe
        *ngIf="mapUrl"
        [src]="mapUrl"
        class="w-full h-full"
        frameborder="0"
        scrolling="no"
        marginheight="0"
        marginwidth="0"
        title="Event location map">
      </iframe>
    </div>
  `
})
export class MapComponent implements OnChanges {
  @Input() lat!: number;
  @Input() lng!: number;
  @Input() zoom = 14;

  mapUrl: SafeResourceUrl | null = null;

  constructor(private sanitizer: DomSanitizer) {}

  ngOnChanges(): void {
    if (this.lat && this.lng) {
      const url = `https://www.openstreetmap.org/export/embed.html?bbox=${this.lng - 0.01},${this.lat - 0.01},${this.lng + 0.01},${this.lat + 0.01}&layer=mapnik&marker=${this.lat},${this.lng}`;
      this.mapUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url);
    }
  }
}
