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

  // Background photo(s)
  backgroundImageUrl: string | null = null;
  backgroundImages: string[] = [];
  backgroundOpacity = 100;
  backgroundBlur = 0;
  collageLayout: 'single' | 'vertical-thirds' | 'diagonal' = 'single';

  // Watermark
  showWatermark = true;

  // Dynamic color palette
  accentColor = '#f59e0b';
  accentColorRgb = '245, 158, 11';
  secondaryAccent = '#1e293b';
  useAutoColors = true;

  // AI Captions
  showCaptions = false;
  captions: { style: string; text: string }[] = [];

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

  private async renderCanvas(): Promise<HTMLCanvasElement> {
    const element = this.canvasContainer.nativeElement;
    return html2canvas(element, {
      width: 1080,
      height: 1920,
      scale: 1,
      useCORS: true,
      backgroundColor: '#0a0a0a',
      logging: false
    });
  }

  async exportToImage(): Promise<void> {
    if (!this.canvasContainer || this.exporting) return;
    this.exporting = true;

    try {
      const canvas = await this.renderCanvas();
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

  get canShare(): boolean {
    return !!navigator.share && !!navigator.canShare;
  }

  async shareToOS(): Promise<void> {
    if (!this.canvasContainer || this.exporting) return;
    this.exporting = true;

    try {
      const canvas = await this.renderCanvas();

      const blob = await new Promise<Blob>((resolve) => {
        canvas.toBlob((b) => resolve(b!), 'image/png');
      });

      const file = new File([blob], `runhub-export-${this.selectedActivity?.id || 'activity'}.png`, {
        type: 'image/png'
      });

      if (navigator.canShare({ files: [file] })) {
        await navigator.share({
          title: `${this.selectedActivity?.title || 'My Run'} - RunHub`,
          files: [file]
        });
      } else {
        // Fallback to download
        this.exportToImage();
      }
    } catch (err: any) {
      if (err.name !== 'AbortError') {
        console.error('Share failed:', err);
      }
    } finally {
      this.exporting = false;
    }
  }

  onBackgroundUpload(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const files = Array.from(input.files).slice(0, 3);

    this.backgroundImages = [];
    let loadedCount = 0;

    files.forEach((file) => {
      const reader = new FileReader();
      reader.onload = () => {
        this.backgroundImages.push(reader.result as string);
        loadedCount++;

        if (loadedCount === files.length) {
          this.backgroundImageUrl = this.backgroundImages[0];
          this.collageLayout = this.backgroundImages.length > 1 ? 'vertical-thirds' : 'single';

          if (this.useAutoColors) {
            this.extractColors(this.backgroundImages[0]);
          }
        }
      };
      reader.readAsDataURL(file);
    });

    // Reset input so same file can be re-selected
    input.value = '';
  }

  removeBackground(): void {
    this.backgroundImageUrl = null;
    this.backgroundImages = [];
    this.backgroundOpacity = 100;
    this.backgroundBlur = 0;
    this.collageLayout = 'single';
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

  generateCaptions(): void {
    if (!this.selectedActivity) return;
    const a = this.selectedActivity;
    const dist = a.distanceKm.toFixed(1);
    const pace = this.formatPace(a.paceMinPerKm);
    const dur = this.formatDuration(a.durationMinutes);
    const loc = a.location || 'the streets';

    const isLong = a.distanceKm >= 15;
    const isFast = a.paceMinPerKm < 5.5;
    const isShort = a.distanceKm < 5;

    // Funny/Self-Deprecating
    const funnyOptions = [
      `${dist}km done. My legs are filing a complaint with HR. ${pace}/km never felt so personal. 🦵💀 #RunHub #NoPainNoGain`,
      `Ran ${dist}km because I saw a croissant at the finish line (there was no croissant). ${dur} of pure betrayal. 🥐😤 #RunnerProblems`,
      `${dist}km in ${dur}. The first 2km were "I love running!" The rest was just survival instinct. 🏃‍♂️💨 #HonestRunner`,
    ];

    // Inspirational
    const inspirationalOptions = [
      `${dist}km. ${dur}. Every step forward is a step toward the best version of yourself. Keep pushing. 💪🔥 #RunHub #NeverStop`,
      `The road doesn't get easier — you get stronger. ${dist}km at ${pace}/km around ${loc}. 🏃‍♂️✨ #RunningMotivation #Grind`,
      isLong
        ? `${dist}km conquered today. Long runs build more than endurance — they build character. 🦁 #UltraRunner #MentalToughness`
        : `Another ${dist}km in the bank. Small runs, big changes. Consistency is the real superpower. 🔑 #DailyRunner`,
    ];

    // Just the Facts
    const factsOptions = [
      `📊 ${dist}km | ⏱ ${dur} | 🏃 ${pace}/km${a.location ? ` | 📍 ${a.location}` : ''}\n#RunHub #RunStats`,
      `Today's session: ${dist}km at ${pace}/km (${dur}). ${isFast ? 'Tempo day.' : isLong ? 'Long run day.' : 'Easy miles.'} #RunData`,
      `${a.title || 'Run'} complete — ${dist}km, ${pace}/km, ${dur}. Tracked with @RunHub 📈 #Running`,
    ];

    this.captions = [
      { style: 'Funny', text: funnyOptions[Math.floor(Math.random() * funnyOptions.length)] },
      { style: 'Inspirational', text: inspirationalOptions[Math.floor(Math.random() * inspirationalOptions.length)] },
      { style: 'Just the Facts', text: factsOptions[Math.floor(Math.random() * factsOptions.length)] },
    ];
    this.showCaptions = true;
  }

  copyCaption(text: string): void {
    navigator.clipboard.writeText(text).catch(() => {});
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
