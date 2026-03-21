import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ActivityService } from '../../core/services/activity.service';
import { Activity } from '../../core/models/activity.model';
import html2canvas from 'html2canvas';

export type TemplateName = 'clear-info' | 'large-stat' | 'aesthetic-text' | 'typography-poster';

export interface TemplateOption {
  id: TemplateName;
  name: string;
  description: string;
  icon: string;
}

@Component({
  selector: 'app-export-studio',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './export-studio.component.html',
  styleUrl: './export-studio.component.scss'
})
export class ExportStudioComponent implements OnInit {
  @ViewChild('canvasContainer') canvasContainer!: ElementRef<HTMLDivElement>;

  activities: Activity[] = [];
  selectedActivity: Activity | null = null;
  selectedTemplate: TemplateName = 'clear-info';
  loading = true;
  exporting = false;
  showActivityPicker = false;
  aestheticTitle = 'MORNING RUN';
  editingTitle = false;

  // Data visibility toggles
  showPace = true;
  showDuration = true;
  showDistance = true;
  showLocation = true;

  // Background photo
  backgroundImageUrl: string | null = null;
  backgroundOpacity = 100;
  backgroundBlur = 0;

  // Watermark
  showWatermark = true;

  // Dynamic color palette
  accentColor = '#f59e0b';
  accentColorRgb = '245, 158, 11';
  secondaryAccent = '#1e293b';
  useAutoColors = true;

  templates: TemplateOption[] = [
    { id: 'clear-info', name: 'Clear Info', description: 'Clean frosted-glass card overlay', icon: 'style' },
    { id: 'large-stat', name: 'Large Stat', description: 'Bold numeric overlay', icon: 'format_size' },
    { id: 'aesthetic-text', name: 'Aesthetic Text', description: 'Minimalist with bold title', icon: 'cloud' },
    { id: 'typography-poster', name: 'Typography Poster', description: 'Magazine-style layout', icon: 'text_fields' }
  ];

  constructor(
    private activityService: ActivityService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.activityService.getMyActivities().subscribe({
      next: (activities) => {
        this.activities = activities;
        this.loading = false;

        const activityId = this.route.snapshot.queryParams['activityId'];
        if (activityId) {
          this.selectedActivity = activities.find(a => a.id === +activityId) || activities[0] || null;
        } else {
          this.selectedActivity = activities[0] || null;
        }
        if (this.selectedActivity) {
          this.aestheticTitle = (this.selectedActivity.title || 'MORNING RUN').toUpperCase();
        }
      },
      error: () => { this.loading = false; }
    });
  }

  selectTemplate(template: TemplateName): void {
    this.selectedTemplate = template;
  }

  selectActivity(activity: Activity): void {
    this.selectedActivity = activity;
    this.showActivityPicker = false;
    this.aestheticTitle = (activity.title || 'MORNING RUN').toUpperCase();
  }

  formatPace(pace: number): string {
    if (!pace) return '--:--';
    const min = Math.floor(pace);
    const sec = Math.round((pace - min) * 60);
    return `${min}:${sec.toString().padStart(2, '0')}`;
  }

  formatDuration(min: number): string {
    const h = Math.floor(min / 60);
    const m = min % 60;
    const s = 0;
    return h > 0
      ? `${h}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
      : `${m}:${s.toString().padStart(2, '0')}`;
  }

  formatDate(dateStr: string): string {
    const d = new Date(dateStr);
    return d.toLocaleDateString('en-US', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });
  }

  formatShortDate(dateStr: string): string {
    const d = new Date(dateStr);
    return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
  }

  async exportToImage(): Promise<void> {
    if (!this.canvasContainer || this.exporting) return;
    this.exporting = true;

    try {
      const element = this.canvasContainer.nativeElement;
      const canvas = await html2canvas(element, {
        width: 1080,
        height: 1920,
        scale: 1,
        useCORS: true,
        backgroundColor: '#0a0a0a',
        logging: false
      });

      const link = document.createElement('a');
      link.download = `runhub-export-${this.selectedActivity?.id || 'activity'}.png`;
      link.href = canvas.toDataURL('image/png');
      link.click();
    } catch (err) {
      console.error('Export failed:', err);
    } finally {
      this.exporting = false;
    }
  }

  onBackgroundUpload(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || !input.files[0]) return;

    const file = input.files[0];
    const reader = new FileReader();
    reader.onload = () => {
      this.backgroundImageUrl = reader.result as string;
      if (this.useAutoColors) {
        this.extractColors(reader.result as string);
      }
    };
    reader.readAsDataURL(file);
  }

  removeBackground(): void {
    this.backgroundImageUrl = null;
    this.backgroundOpacity = 100;
    this.backgroundBlur = 0;
    this.resetColors();
  }

  private extractColors(dataUrl: string): void {
    const img = new Image();
    img.crossOrigin = 'Anonymous';
    img.onload = () => {
      try {
        const canvas = document.createElement('canvas');
        const ctx = canvas.getContext('2d');
        if (!ctx) return;

        // Sample at small size for performance
        const size = 50;
        canvas.width = size;
        canvas.height = size;
        ctx.drawImage(img, 0, 0, size, size);
        const data = ctx.getImageData(0, 0, size, size).data;

        // Simple dominant color extraction: average with saturation weighting
        let rSum = 0, gSum = 0, bSum = 0, count = 0;
        let maxSat = 0, satR = 0, satG = 0, satB = 0;

        for (let i = 0; i < data.length; i += 4) {
          const r = data[i], g = data[i + 1], b = data[i + 2];
          rSum += r; gSum += g; bSum += b; count++;

          // Track most saturated color
          const max = Math.max(r, g, b), min = Math.min(r, g, b);
          const sat = max === 0 ? 0 : (max - min) / max;
          if (sat > maxSat && max > 40) {
            maxSat = sat;
            satR = r; satG = g; satB = b;
          }
        }

        if (maxSat > 0.15) {
          this.accentColor = `rgb(${satR}, ${satG}, ${satB})`;
          this.accentColorRgb = `${satR}, ${satG}, ${satB}`;
        } else {
          const avgR = Math.round(rSum / count);
          const avgG = Math.round(gSum / count);
          const avgB = Math.round(bSum / count);
          this.accentColor = `rgb(${avgR}, ${avgG}, ${avgB})`;
          this.accentColorRgb = `${avgR}, ${avgG}, ${avgB}`;
        }

        // Secondary: average color
        const aR = Math.round(rSum / count);
        const aG = Math.round(gSum / count);
        const aB = Math.round(bSum / count);
        this.secondaryAccent = `rgb(${aR}, ${aG}, ${aB})`;
      } catch (e) {
        console.warn('Color extraction failed:', e);
      }
    };
    img.src = dataUrl;
  }

  private resetColors(): void {
    this.accentColor = '#f59e0b';
    this.accentColorRgb = '245, 158, 11';
    this.secondaryAccent = '#1e293b';
  }

  getVerificationCode(): string {
    if (!this.selectedActivity) return '';
    const a = this.selectedActivity;
    // Simple hash from activity data for verification
    const raw = `${a.id}-${a.distanceKm}-${a.durationMinutes}-${a.activityDate}`;
    let hash = 0;
    for (let i = 0; i < raw.length; i++) {
      hash = ((hash << 5) - hash + raw.charCodeAt(i)) | 0;
    }
    return `RH-${Math.abs(hash).toString(36).toUpperCase().slice(0, 8)}`;
  }

  getVerificationUrl(): string {
    if (!this.selectedActivity) return '';
    return `runhub.app/verify/${this.selectedActivity.id}`;
  }

  goBack(): void {
    this.router.navigate(['/activities']);
  }
}
