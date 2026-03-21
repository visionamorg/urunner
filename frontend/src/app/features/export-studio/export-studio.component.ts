import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ActivityService } from '../../core/services/activity.service';
import { UserService } from '../../core/services/user.service';
import { Activity } from '../../core/models/activity.model';
import { ExportTemplateService, ExportTemplateDto } from '../../core/services/export-template.service';
import { AvatarComponent } from '../../shared/components/avatar/avatar.component';
import html2canvas from 'html2canvas';

export type TemplateName = 'clear-info' | 'large-stat' | 'aesthetic-text' | 'typography-poster' | 'story-global';

export interface TemplateOption {
  id: TemplateName;
  name: string;
  description: string;
  icon: string;
}

@Component({
  selector: 'app-export-studio',
  standalone: true,
  imports: [CommonModule, FormsModule, AvatarComponent],
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

  // Marketplace
  showMarketplace = false;
  communityTemplates: ExportTemplateDto[] = [];
  loadingTemplates = false;

  // Video export
  exportingVideo = false;

  // Current user profile (stored as data URL for CORS-safe canvas capture)
  currentUserProfileImageUrl: string | null = null;

  // Branding stamp
  brandColor = '#f59e0b';
  brandSize = 100; // percentage, 60–180
  brandPosition: 'tl' | 'tc' | 'tr' | 'ml' | 'mc' | 'mr' | 'bl' | 'bc' | 'br' = 'tl';

  brandPresetColors = ['#f59e0b', '#ffffff', '#000000', '#3b82f6', '#10b981', '#ef4444', '#8b5cf6', '#f43f5e'];

  brandPositions: { id: 'tl'|'tc'|'tr'|'ml'|'mc'|'mr'|'bl'|'bc'|'br'; label: string; icon: string }[] = [
    { id: 'tl', label: 'Top Left',     icon: 'north_west' },
    { id: 'tc', label: 'Top Center',   icon: 'north' },
    { id: 'tr', label: 'Top Right',    icon: 'north_east' },
    { id: 'ml', label: 'Middle Left',  icon: 'west' },
    { id: 'mc', label: 'Center',       icon: 'filter_center_focus' },
    { id: 'mr', label: 'Middle Right', icon: 'east' },
    { id: 'bl', label: 'Bottom Left',  icon: 'south_west' },
    { id: 'bc', label: 'Bottom Center',icon: 'south' },
    { id: 'br', label: 'Bottom Right', icon: 'south_east' },
  ];

  // Weather stamp
  showWeatherStamp = false;
  weatherCondition = '';
  weatherTemp = '';
  weatherIcon = '';

  templates: TemplateOption[] = [
    { id: 'clear-info', name: 'Clear Info', description: 'Clean frosted-glass card overlay', icon: 'style' },
    { id: 'large-stat', name: 'Large Stat', description: 'Bold numeric overlay', icon: 'format_size' },
    { id: 'aesthetic-text', name: 'Aesthetic Text', description: 'Minimalist with bold title', icon: 'cloud' },
    { id: 'typography-poster', name: 'Typography Poster', description: 'Magazine-style layout', icon: 'text_fields' },
    { id: 'story-global', name: 'Story Global', description: 'Editorial activity story card', icon: 'auto_stories' }
  ];

  constructor(
    private activityService: ActivityService,
    private userService: UserService,
    private exportTemplateService: ExportTemplateService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.userService.getMe().subscribe({
      next: async (profile) => {
        if (profile.profileImageUrl) {
          this.currentUserProfileImageUrl = await this.toDataUrl(profile.profileImageUrl);
        }
      },
      error: () => {}
    });

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

  getDayOfWeek(dateStr: string): string {
    return new Date(dateStr).toLocaleDateString('en-US', { weekday: 'long' }).toUpperCase();
  }

  getBrandPositionStyle(): Record<string, string> {
    const edge = '60px';
    const map: Record<string, Record<string, string>> = {
      tl: { top: edge, left: edge },
      tc: { top: edge, left: '50%', transform: 'translateX(-50%)' },
      tr: { top: edge, right: edge },
      ml: { top: '50%', left: edge, transform: 'translateY(-50%)' },
      mc: { top: '50%', left: '50%', transform: 'translate(-50%, -50%)' },
      mr: { top: '50%', right: edge, transform: 'translateY(-50%)' },
      bl: { bottom: edge, left: edge },
      bc: { bottom: edge, left: '50%', transform: 'translateX(-50%)' },
      br: { bottom: edge, right: edge },
    };
    return map[this.brandPosition] ?? map['tl'];
  }

  /** Convert any image URL to a base64 data URL so html2canvas can render it without CORS issues */
  private toDataUrl(url: string): Promise<string> {
    return new Promise((resolve) => {
      const img = new Image();
      img.crossOrigin = 'anonymous';
      img.onload = () => {
        const c = document.createElement('canvas');
        c.width = img.naturalWidth;
        c.height = img.naturalHeight;
        c.getContext('2d')!.drawImage(img, 0, 0);
        resolve(c.toDataURL('image/png'));
      };
      img.onerror = () => resolve(url); // fallback: keep original url
      img.src = url;
    });
  }

  private async renderCanvas(): Promise<HTMLCanvasElement> {
    const element = this.canvasContainer.nativeElement;
    return html2canvas(element, {
      width: 1080,
      height: 1920,
      scale: 1,
      useCORS: true,
      backgroundColor: '#0a0a0a',
      logging: false,
      onclone: (_doc: Document, clonedElement: HTMLElement) => {
        clonedElement.style.transform = 'none';
        clonedElement.style.transformOrigin = 'top left';
      }
    });
  }

  async exportToImage(): Promise<void> {
    if (!this.canvasContainer || this.exporting) return;
    this.exporting = true;

    try {
      const canvas = await this.renderCanvas();
      const link = document.createElement('a');
      link.download = `urc-export-${this.selectedActivity?.id || 'activity'}.png`;
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

      const file = new File([blob], `urc-export-${this.selectedActivity?.id || 'activity'}.png`, {
        type: 'image/png'
      });

      if (navigator.canShare({ files: [file] })) {
        await navigator.share({
          title: `${this.selectedActivity?.title || 'My Run'} - URC`,
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

  async exportToVideo(): Promise<void> {
    if (!this.canvasContainer || !this.selectedActivity || this.exportingVideo) return;
    this.exportingVideo = true;

    try {
      const activity = this.selectedActivity;
      const targetDist = activity.distanceKm;
      const targetPace = activity.paceMinPerKm;
      const targetDuration = activity.durationMinutes;

      // Create an offscreen canvas for animation
      const offscreen = document.createElement('canvas');
      offscreen.width = 1080;
      offscreen.height = 1920;
      const ctx = offscreen.getContext('2d')!;

      // Get static background from html2canvas
      const bgCanvas = await this.renderCanvas();

      // Set up MediaRecorder
      const stream = offscreen.captureStream(30);
      const recorder = new MediaRecorder(stream, { mimeType: 'video/webm;codecs=vp9' });
      const chunks: Blob[] = [];
      recorder.ondataavailable = (e) => { if (e.data.size > 0) chunks.push(e.data); };

      const recordingDone = new Promise<void>((resolve) => {
        recorder.onstop = () => resolve();
      });

      recorder.start();

      // Animate for 5 seconds (150 frames at 30fps)
      const totalFrames = 150;
      for (let frame = 0; frame < totalFrames; frame++) {
        const progress = frame / totalFrames;

        // Draw the static background
        ctx.drawImage(bgCanvas, 0, 0);

        // Overlay animated count-up numbers
        const animDist = (targetDist * Math.min(progress * 1.5, 1)).toFixed(2);
        const animPaceRaw = targetPace * Math.min(progress * 1.5, 1);
        const pMin = Math.floor(animPaceRaw);
        const pSec = Math.round((animPaceRaw - pMin) * 60);
        const animPace = `${pMin}:${pSec.toString().padStart(2, '0')}`;
        const animDur = Math.floor(targetDuration * Math.min(progress * 1.5, 1));

        // Draw count-up overlay in center during first 3 seconds
        if (progress < 0.6) {
          const alpha = progress < 0.1 ? progress * 10 : (progress > 0.5 ? (0.6 - progress) * 10 : 1);
          ctx.save();
          ctx.globalAlpha = alpha * 0.9;
          ctx.fillStyle = 'rgba(0,0,0,0.6)';
          ctx.fillRect(0, 700, 1080, 500);
          ctx.globalAlpha = alpha;

          ctx.font = '900 120px Inter, sans-serif';
          ctx.fillStyle = '#f59e0b';
          ctx.textAlign = 'center';
          ctx.fillText(`${animDist} km`, 540, 900);

          ctx.font = '600 48px Inter, sans-serif';
          ctx.fillStyle = '#ffffff';
          ctx.fillText(`${animPace} /km  ·  ${animDur}min`, 540, 1000);

          ctx.restore();
        }

        // Wait for next frame
        await new Promise(r => setTimeout(r, 1000 / 30));
      }

      recorder.stop();
      await recordingDone;

      const blob = new Blob(chunks, { type: 'video/webm' });
      const link = document.createElement('a');
      link.download = `urc-export-${activity.id}.webm`;
      link.href = URL.createObjectURL(blob);
      link.click();
      URL.revokeObjectURL(link.href);
    } catch (err) {
      console.error('Video export failed:', err);
    } finally {
      this.exportingVideo = false;
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

  toggleMarketplace(): void {
    this.showMarketplace = !this.showMarketplace;
    if (this.showMarketplace && this.communityTemplates.length === 0) {
      this.loadCommunityTemplates();
    }
  }

  loadCommunityTemplates(): void {
    this.loadingTemplates = true;
    this.exportTemplateService.getAll().subscribe({
      next: (templates) => {
        this.communityTemplates = templates;
        this.loadingTemplates = false;
      },
      error: () => { this.loadingTemplates = false; }
    });
  }

  voteTemplate(templateId: number): void {
    this.exportTemplateService.toggleVote(templateId).subscribe({
      next: (updated) => {
        const idx = this.communityTemplates.findIndex(t => t.id === templateId);
        if (idx >= 0) this.communityTemplates[idx] = updated;
      }
    });
  }

  generateWeatherStamp(): void {
    if (!this.selectedActivity) return;
    const date = new Date(this.selectedActivity.activityDate);
    const month = date.getMonth(); // 0-11

    // Simulate weather based on month/season
    const conditions: { condition: string; temp: string; icon: string }[] = [];

    if (month >= 11 || month <= 1) {
      // Winter
      conditions.push(
        { condition: 'FREEZING', temp: `${Math.floor(Math.random() * 8) - 5}°C`, icon: 'ac_unit' },
        { condition: 'COLD & CRISP', temp: `${Math.floor(Math.random() * 6) + 2}°C`, icon: 'cloudy_snowing' },
        { condition: 'SNOWY', temp: `${Math.floor(Math.random() * 4) - 3}°C`, icon: 'weather_snowy' }
      );
    } else if (month >= 2 && month <= 4) {
      // Spring
      conditions.push(
        { condition: 'FRESH SPRING', temp: `${Math.floor(Math.random() * 8) + 10}°C`, icon: 'partly_cloudy_day' },
        { condition: 'LIGHT RAIN', temp: `${Math.floor(Math.random() * 6) + 12}°C`, icon: 'rainy' },
        { condition: 'BREEZY', temp: `${Math.floor(Math.random() * 6) + 14}°C`, icon: 'air' }
      );
    } else if (month >= 5 && month <= 7) {
      // Summer
      conditions.push(
        { condition: 'SCORCHING', temp: `${Math.floor(Math.random() * 10) + 30}°C`, icon: 'local_fire_department' },
        { condition: 'HOT & SUNNY', temp: `${Math.floor(Math.random() * 8) + 28}°C`, icon: 'wb_sunny' },
        { condition: 'HUMID', temp: `${Math.floor(Math.random() * 6) + 26}°C`, icon: 'water_drop' }
      );
    } else {
      // Autumn
      conditions.push(
        { condition: 'AUTUMN CHILL', temp: `${Math.floor(Math.random() * 8) + 8}°C`, icon: 'eco' },
        { condition: 'OVERCAST', temp: `${Math.floor(Math.random() * 6) + 12}°C`, icon: 'cloud' },
        { condition: 'WINDY', temp: `${Math.floor(Math.random() * 6) + 10}°C`, icon: 'air' }
      );
    }

    const pick = conditions[Math.floor(Math.random() * conditions.length)];
    this.weatherCondition = pick.condition;
    this.weatherTemp = pick.temp;
    this.weatherIcon = pick.icon;
    this.showWeatherStamp = true;
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
      `${dist}km done. My legs are filing a complaint with HR. ${pace}/km never felt so personal. 🦵💀 #URC #NoPainNoGain`,
      `Ran ${dist}km because I saw a croissant at the finish line (there was no croissant). ${dur} of pure betrayal. 🥐😤 #RunnerProblems`,
      `${dist}km in ${dur}. The first 2km were "I love running!" The rest was just survival instinct. 🏃‍♂️💨 #HonestRunner`,
    ];

    // Inspirational
    const inspirationalOptions = [
      `${dist}km. ${dur}. Every step forward is a step toward the best version of yourself. Keep pushing. 💪🔥 #URC #NeverStop`,
      `The road doesn't get easier — you get stronger. ${dist}km at ${pace}/km around ${loc}. 🏃‍♂️✨ #RunningMotivation #Grind`,
      isLong
        ? `${dist}km conquered today. Long runs build more than endurance — they build character. 🦁 #UltraRunner #MentalToughness`
        : `Another ${dist}km in the bank. Small runs, big changes. Consistency is the real superpower. 🔑 #DailyRunner`,
    ];

    // Just the Facts
    const factsOptions = [
      `📊 ${dist}km | ⏱ ${dur} | 🏃 ${pace}/km${a.location ? ` | 📍 ${a.location}` : ''}\n#URC #RunStats`,
      `Today's session: ${dist}km at ${pace}/km (${dur}). ${isFast ? 'Tempo day.' : isLong ? 'Long run day.' : 'Easy miles.'} #RunData`,
      `${a.title || 'Run'} complete — ${dist}km, ${pace}/km, ${dur}. Tracked with @URC 📈 #Running`,
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
    return `URC-${Math.abs(hash).toString(36).toUpperCase().slice(0, 8)}`;
  }

  getVerificationUrl(): string {
    if (!this.selectedActivity) return '';
    return `urc.run/verify/${this.selectedActivity.id}`;
  }

  goBack(): void {
    this.router.navigate(['/activities']);
  }
}
