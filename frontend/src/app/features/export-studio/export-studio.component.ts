import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule, TitleCasePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ActivityService } from '../../core/services/activity.service';
import { UserService } from '../../core/services/user.service';
import { Activity } from '../../core/models/activity.model';
import { ExportTemplateService, ExportTemplateDto } from '../../core/services/export-template.service';
import { AvatarComponent } from '../../shared/components/avatar/avatar.component';
import html2canvas from 'html2canvas';

export type TemplateName =
  // Street
  | 'cyberpunk' | 'graffiti' | 'brutalist' | 'vhs-tape'
  // Elite
  | 'race-bib' | 'podium' | 'aesthetic-text' | 'cloud-text'
  // Minimal
  | 'clear-info' | 'large-stat' | 'receipt' | 'polaroid'
  // Editorial
  | 'newspaper' | 'story-global' | 'typography-poster' | 'annual-wrapped'
  // High-Concept
  | 'minimalist-peak' | 'split-screen-pro' | 'magazine-cover';

export type TemplateCategory = 'all' | 'street' | 'elite' | 'minimal' | 'editorial' | 'concept';

export type CardFormat = '9:16' | '1:1' | '16:9';

export interface TemplateOption {
  id: TemplateName;
  name: string;
  description: string;
  icon: string;
  category: TemplateCategory;
}

export interface FormatOption {
  id: CardFormat;
  label: string;
  icon: string;
  width: number;
  height: number;
}

@Component({
  selector: 'app-export-studio',
  standalone: true,
  imports: [CommonModule, FormsModule, AvatarComponent, TitleCasePipe],
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
  showCalories = false;
  showHeartRate = false;
  showElevation = false;
  showActivityName = true;
  showDateTime = true;
  showClubName = true;
  showRunnerName = false;

  // Background photo(s)
  backgroundImageUrl: string | null = null;
  backgroundImages: string[] = [];
  backgroundOpacity = 100;
  backgroundBlur = 0;
  backgroundBrightness = 100;
  backgroundContrast = 100;
  backgroundSaturation = 100;
  collageLayout: 'single' | 'vertical-thirds' | 'diagonal' = 'single';

  // Gradient presets
  selectedGradient: string | null = null;
  gradientPresets = [
    { id: 'sunset-run', css: 'linear-gradient(135deg, #f97316 0%, #ec4899 50%, #8b5cf6 100%)' },
    { id: 'night-road', css: 'linear-gradient(180deg, #0f172a 0%, #1e293b 40%, #334155 100%)' },
    { id: 'ocean-pace', css: 'linear-gradient(135deg, #06b6d4 0%, #3b82f6 50%, #1e40af 100%)' },
    { id: 'forest-trail', css: 'linear-gradient(135deg, #065f46 0%, #059669 50%, #34d399 100%)' },
    { id: 'lava-flow', css: 'linear-gradient(135deg, #dc2626 0%, #f97316 50%, #facc15 100%)' },
    { id: 'midnight', css: 'linear-gradient(180deg, #020617 0%, #0f172a 50%, #1e293b 100%)' },
    { id: 'aurora', css: 'linear-gradient(135deg, #a78bfa 0%, #06b6d4 50%, #34d399 100%)' },
    { id: 'steel', css: 'linear-gradient(135deg, #374151 0%, #6b7280 50%, #9ca3af 100%)' },
    { id: 'ember', css: 'linear-gradient(180deg, #1c1917 0%, #78350f 50%, #f59e0b 100%)' },
    { id: 'neon-city', css: 'linear-gradient(135deg, #7c3aed 0%, #ec4899 50%, #06b6d4 100%)' },
    { id: 'dawn', css: 'linear-gradient(180deg, #1e3a5f 0%, #f59e0b 60%, #fde68a 100%)' },
    { id: 'carbon', css: 'linear-gradient(135deg, #18181b 0%, #27272a 50%, #3f3f46 100%)' },
  ];

  // Pattern presets
  selectedPattern: string | null = null;
  patternPresets = [
    { id: 'carbon-fiber', label: 'Carbon', icon: 'grid_4x4' },
    { id: 'track-lanes', label: 'Track', icon: 'straighten' },
    { id: 'topographic', label: 'Topo', icon: 'landscape' },
    { id: 'dots', label: 'Dots', icon: 'blur_on' },
    { id: 'grid', label: 'Grid', icon: 'grid_on' },
  ];

  // Watermark
  showWatermark = true;

  // Dynamic color palette
  accentColor = '#f59e0b';
  accentColorRgb = '245, 158, 11';
  secondaryAccent = '#1e293b';
  useAutoColors = true;

  // Text editor
  customHeadline = '';
  customSubtitle = '';
  selectedFont = 'Inter';
  textColor = '#ffffff';
  readonly fontOptions = ['Inter', 'Orbitron', 'Bebas Neue', 'Playfair Display', 'Permanent Marker', 'Space Grotesk', 'DM Sans', 'Share Tech Mono'];

  // Card format
  cardFormat: CardFormat = '9:16';
  readonly formatOptions: FormatOption[] = [
    { id: '9:16', label: 'Story', icon: 'crop_portrait', width: 1080, height: 1920 },
    { id: '1:1', label: 'Post', icon: 'crop_square', width: 1080, height: 1080 },
    { id: '16:9', label: 'Landscape', icon: 'crop_landscape', width: 1920, height: 1080 },
  ];

  // Template category filter
  templateCategory: TemplateCategory = 'all';

  // AI Captions
  showCaptions = false;
  captions: { style: string; text: string }[] = [];

  // Export
  exportFormat: 'png' | 'jpeg' = 'png';
  jpegQuality = 90;
  videoDuration: 5 | 8 | 12 = 5;
  exportProgress = 0;
  showExportProgress = false;

  // Mobile
  mobileDrawerOpen = false;
  mobileActiveTab: 'templates' | 'style' | 'data' | 'export' = 'templates';

  // Studio tabs
  studioTab: 'builder' | 'explore' = 'builder';

  // Sidebar panel tabs (zero-scroll)
  sidebarTab: 'activity' | 'layout' | 'design' | 'branding' = 'layout';

  // Glassmorphism toggle
  useGlassmorphism = true;

  // Dark/Light mode for template elements
  templateColorMode: 'dark' | 'light' = 'dark';

  // Marketplace / Explore
  showMarketplace = false;
  communityTemplates: ExportTemplateDto[] = [];
  loadingTemplates = false;
  savedTemplateIds = new Set<number>();

  // Publish modal
  showPublishModal = false;
  publishName = '';
  publishDesc = '';
  publishSelectedTags = new Set<string>();
  publishLoading = false;
  publishSuccess = false;
  readonly publishTagOptions = ['Minimalist', 'Editorial', 'Ultra-Running', 'Club Branded', 'Dark Mode', 'Colorful', 'Typography'];

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

  // Cloud Text (Story 37)
  cloudText = 'URC';
  cloudTexture: 'cloud' | '3d' | 'neon' = 'cloud';
  cloudTextColor = '#ffffff';
  cloudFontSize = 180; // px in 1080-wide canvas

  // Annual Wrapped stats
  yearStats: { totalKm: number; totalActivities: number; totalMinutes: number; bestRun: number; topMonth: string } | null = null;

  // Marketplace explore
  marketplaceSort: 'trending' | 'top-rated' | 'newest' = 'trending';
  marketplaceCategory = 'All';
  marketplaceCategories = ['All', 'Minimalist', 'Editorial', 'Ultra-Running', 'Club Branded'];

  templates: TemplateOption[] = [
    // Street
    { id: 'cyberpunk', name: 'Cyberpunk', description: 'Neon glitch hacker aesthetic', icon: 'terminal', category: 'street' },
    { id: 'graffiti', name: 'Graffiti', description: 'Spray paint texture, bold street fonts', icon: 'brush', category: 'street' },
    { id: 'brutalist', name: 'Brutalist', description: 'Raw B&W, massive typography', icon: 'format_bold', category: 'street' },
    { id: 'vhs-tape', name: 'VHS Tape', description: '80s cassette, retro distortion', icon: 'movie', category: 'street' },
    // Elite
    { id: 'race-bib', name: 'Race Bib', description: 'Official race bib with number', icon: 'confirmation_number', category: 'elite' },
    { id: 'podium', name: 'Podium', description: 'Gold medal ceremony feel', icon: 'emoji_events', category: 'elite' },
    { id: 'aesthetic-text', name: 'Aesthetic Text', description: 'Minimalist with bold title', icon: 'cloud', category: 'elite' },
    { id: 'cloud-text', name: 'Cloud Text', description: 'Massive floating 3D typography', icon: 'cloud', category: 'elite' },
    // Minimal
    { id: 'clear-info', name: 'Clear Info', description: 'Clean frosted-glass card overlay', icon: 'style', category: 'minimal' },
    { id: 'large-stat', name: 'Large Stat', description: 'Bold numeric overlay', icon: 'format_size', category: 'minimal' },
    { id: 'receipt', name: 'Receipt', description: 'Thermal receipt printout', icon: 'receipt_long', category: 'minimal' },
    { id: 'polaroid', name: 'Polaroid', description: 'Photo + handwritten caption', icon: 'photo_camera', category: 'minimal' },
    // Editorial
    { id: 'newspaper', name: 'Breaking News', description: 'Newspaper front page', icon: 'newspaper', category: 'editorial' },
    { id: 'story-global', name: 'Magazine', description: 'Glossy editorial layout', icon: 'auto_stories', category: 'editorial' },
    { id: 'typography-poster', name: 'Typography Poster', description: 'Words as visual art', icon: 'text_fields', category: 'editorial' },
    { id: 'annual-wrapped', name: 'Year Wrapped', description: 'Spotify-style year recap', icon: 'celebration', category: 'editorial' },
    // High-Concept
    { id: 'minimalist-peak', name: 'Minimalist Peak', description: 'Giant distance overlaying entire canvas', icon: 'height', category: 'concept' },
    { id: 'split-screen-pro', name: 'Split-Screen', description: '50/50 photo and tech data sheet', icon: 'vertical_split', category: 'concept' },
    { id: 'magazine-cover', name: 'Magazine Cover', description: 'Masthead title with headline stats', icon: 'menu_book', category: 'concept' },
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
        this.computeYearStats(activities);
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
      width: this.canvasWidth,
      height: this.canvasHeight,
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
      offscreen.width = this.canvasWidth;
      offscreen.height = this.canvasHeight;
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

      // Animate for selected duration
      this.showExportProgress = true;
      this.exportProgress = 0;
      const totalFrames = this.videoDuration * 30;
      for (let frame = 0; frame < totalFrames; frame++) {
        const progress = frame / totalFrames;

        // Update progress
        this.exportProgress = Math.round((frame / totalFrames) * 100);

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
      this.showExportProgress = false;
      this.exportProgress = 0;
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

          // Smart collage: auto-select layout based on image count
          if (this.backgroundImages.length === 1) {
            this.collageLayout = 'single';
          } else if (this.backgroundImages.length === 2) {
            this.collageLayout = 'vertical-thirds';
          } else {
            this.collageLayout = 'diagonal';
          }

          // Auto-adapt: apply subtle blur + reduce opacity for depth
          this.backgroundBlur = 3;
          this.backgroundOpacity = 85;

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
    this.backgroundBrightness = 100;
    this.backgroundContrast = 100;
    this.backgroundSaturation = 100;
    this.collageLayout = 'single';
    this.selectedGradient = null;
    this.selectedPattern = null;
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

  switchSidebarTab(tab: 'activity' | 'layout' | 'design' | 'branding'): void {
    this.sidebarTab = tab;
  }

  applyMagicColors(): void {
    if (this.backgroundImages.length > 0) {
      this.extractColors(this.backgroundImages[0]);
    }
  }

  switchTab(tab: 'builder' | 'explore'): void {
    this.studioTab = tab;
    if (tab === 'explore' && this.communityTemplates.length === 0) {
      this.loadCommunityTemplates();
    }
  }

  openPublishModal(): void {
    this.publishName = this.selectedTemplate === 'aesthetic-text' ? this.aestheticTitle : '';
    this.publishDesc = '';
    this.publishSelectedTags.clear();
    this.publishSuccess = false;
    this.showPublishModal = true;
  }

  togglePublishTag(tag: string): void {
    if (this.publishSelectedTags.has(tag)) {
      this.publishSelectedTags.delete(tag);
    } else if (this.publishSelectedTags.size < 3) {
      this.publishSelectedTags.add(tag);
    }
  }

  publishTemplate(): void {
    if (!this.publishName.trim() || this.publishSelectedTags.size === 0) return;
    this.publishLoading = true;

    const config = {
      baseTemplate: this.selectedTemplate,
      accentColor: this.accentColor,
      brandColor: this.brandColor,
      brandPosition: this.brandPosition,
      brandSize: this.brandSize,
    };

    this.exportTemplateService.create({
      name: this.publishName.trim(),
      description: this.publishDesc.trim(),
      cssLayout: JSON.stringify(config),
      tags: Array.from(this.publishSelectedTags).join(','),
    }).subscribe({
      next: (created) => {
        this.publishLoading = false;
        this.publishSuccess = true;
        this.communityTemplates = [created, ...this.communityTemplates];
        setTimeout(() => { this.showPublishModal = false; this.publishSuccess = false; }, 1800);
      },
      error: () => { this.publishLoading = false; }
    });
  }

  toggleSaveTemplate(id: number): void {
    if (this.savedTemplateIds.has(id)) {
      this.savedTemplateIds.delete(id);
    } else {
      this.savedTemplateIds.add(id);
    }
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

    // Motivational
    const motivationalOptions = [
      `${dist}km. ${dur}. Every step forward is a step toward the best version of yourself. Keep pushing. 💪🔥 #URC #NeverStop`,
      `The road doesn't get easier — you get stronger. ${dist}km at ${pace}/km around ${loc}. 🏃‍♂️✨ #RunningMotivation`,
      isLong
        ? `${dist}km conquered today. Long runs build more than endurance — they build character. 🦁 #MentalToughness`
        : `Another ${dist}km in the bank. Small runs, big changes. Consistency is the real superpower. 🔑 #DailyRunner`,
      `Started running. Didn't stop until ${dist}km. That's the only mindset that matters. 🧠🔥 #URC #KeepGoing`,
    ];

    // Aesthetic
    const aestheticOptions = [
      `${dist} km · ${pace}/km · ${loc}\n\nsilence. pavement. rhythm.\n\n#URC #RunAesthetic`,
      `morning light. cold air. ${dist}km of moving meditation.\n${dur} well spent. ✨ #URC`,
      `${dist}km through ${loc}. no music. just footsteps and breathing.\n\nthis is where clarity lives. 🌅 #RunCulture`,
      `some days you don't run for the stats.\nyou run for the stillness after.\n\n${dist}km · ${pace}/km 🤍 #URC`,
    ];

    // Brag
    const bragOptions = [
      `${dist}km at ${pace}/km. ${dur}. No excuses, no shortcuts. While you were sleeping. 😤💨 #URC #DifferentBreed`,
      isFast
        ? `${pace}/km for ${dist}km. Speed doesn't lie. Built different. ⚡️ #URC #SpeedDemon`
        : `${dist}km done before most people hit snooze. ${dur} of pure discipline. 🏃‍♂️🔥 #URC #EarlyBird`,
      isLong
        ? `${dist}km. Yes, you read that right. Marathon distance isn't a dream — it's a Tuesday. 👑 #URC #UltraMode`
        : `Another day, another ${dist}km crushed. ${pace}/km pace. The grind never stops. 💎 #URC #NoRestDays`,
      `${dist}km ✅ ${pace}/km ✅ Excuses ❌\nReceipts don't lie. 🧾🔥 #URC #ProveIt`,
    ];

    this.captions = [
      { style: 'Motivational', text: motivationalOptions[Math.floor(Math.random() * motivationalOptions.length)] },
      { style: 'Aesthetic', text: aestheticOptions[Math.floor(Math.random() * aestheticOptions.length)] },
      { style: 'Brag', text: bragOptions[Math.floor(Math.random() * bragOptions.length)] },
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

  // ── Template filtering ─────────────────────────────────────────────────
  get filteredTemplates(): TemplateOption[] {
    if (this.templateCategory === 'all') return this.templates;
    return this.templates.filter(t => t.category === this.templateCategory);
  }

  // ── Card format ───────────────────────────────────────────────────────
  get canvasWidth(): number {
    return this.formatOptions.find(f => f.id === this.cardFormat)?.width ?? 1080;
  }

  get canvasHeight(): number {
    return this.formatOptions.find(f => f.id === this.cardFormat)?.height ?? 1920;
  }

  selectFormat(format: CardFormat): void {
    this.cardFormat = format;
  }

  // ── Background helpers ────────────────────────────────────────────────
  get backgroundFilter(): string {
    if (this.selectedTemplate === 'newspaper') {
      return `grayscale(100%) contrast(120%)`;
    }
    const parts: string[] = [];
    if (this.backgroundBlur > 0) parts.push(`blur(${this.backgroundBlur}px)`);
    if (this.backgroundBrightness !== 100) parts.push(`brightness(${this.backgroundBrightness}%)`);
    if (this.backgroundContrast !== 100) parts.push(`contrast(${this.backgroundContrast}%)`);
    if (this.backgroundSaturation !== 100) parts.push(`saturate(${this.backgroundSaturation}%)`);
    return parts.length ? parts.join(' ') : 'none';
  }

  selectGradient(id: string): void {
    this.selectedGradient = id;
    this.selectedPattern = null;
    this.backgroundImageUrl = null;
    this.backgroundImages = [];
  }

  selectPattern(id: string): void {
    this.selectedPattern = id;
    this.selectedGradient = null;
    this.backgroundImageUrl = null;
    this.backgroundImages = [];
  }

  clearBackgroundPreset(): void {
    this.selectedGradient = null;
    this.selectedPattern = null;
  }

  getGradientCss(id: string): string {
    return this.gradientPresets.find(g => g.id === id)?.css ?? '';
  }

  // ── Mobile drawer ─────────────────────────────────────────────────────
  openMobileDrawer(tab: 'templates' | 'style' | 'data' | 'export'): void {
    this.mobileActiveTab = tab;
    this.mobileDrawerOpen = true;
  }

  closeMobileDrawer(): void {
    this.mobileDrawerOpen = false;
  }

  // ── Race Bib number ───────────────────────────────────────────────────
  getRaceBibNumber(): string {
    if (!this.selectedActivity) return '0001';
    return this.selectedActivity.id.toString().padStart(4, '0');
  }

  goBack(): void {
    this.router.navigate(['/activities']);
  }

  // ── Story 31: Newspaper ──────────────────────────────────────────────────
  getNewsHeadline(): string {
    if (!this.selectedActivity) return 'RUNNER HITS THE STREETS!';
    const km = this.selectedActivity.distanceKm;
    if (km >= 42) return 'LOCAL RUNNER CONQUERS THE MARATHON!';
    if (km >= 21) return 'HALF-MARATHON CRUSHED IN EPIC FASHION!';
    if (km >= 10) return '10KM SMASHED';
    return (this.selectedActivity.title || 'RUN').toUpperCase() + '!';
  }

  // ── Story 33: Receipt ────────────────────────────────────────────────────
  getReceiptDots(label: string, value: string, total = 38): string {
    const dots = total - label.length - value.length;
    return '.'.repeat(Math.max(2, dots));
  }

  // ── Story 34: Annual Wrapped ─────────────────────────────────────────────
  computeYearStats(activities: Activity[]): void {
    const currentYear = new Date().getFullYear();
    const yearActivities = activities.filter(a => new Date(a.activityDate).getFullYear() === currentYear);

    if (yearActivities.length === 0) {
      this.yearStats = null;
      return;
    }

    const totalKm = yearActivities.reduce((sum, a) => sum + a.distanceKm, 0);
    const totalActivities = yearActivities.length;
    const totalMinutes = yearActivities.reduce((sum, a) => sum + a.durationMinutes, 0);
    const bestRun = Math.max(...yearActivities.map(a => a.distanceKm));

    // Find most-active month
    const monthCounts: Record<number, number> = {};
    yearActivities.forEach(a => {
      const m = new Date(a.activityDate).getMonth();
      monthCounts[m] = (monthCounts[m] || 0) + 1;
    });
    const topMonthIndex = Number(Object.entries(monthCounts).sort((a, b) => b[1] - a[1])[0][0]);
    const monthNames = ['JAN', 'FEB', 'MAR', 'APR', 'MAY', 'JUN', 'JUL', 'AUG', 'SEP', 'OCT', 'NOV', 'DEC'];
    const topMonth = monthNames[topMonthIndex];

    this.yearStats = { totalKm, totalActivities, totalMinutes, bestRun, topMonth };
  }

  getTotalHours(): string {
    if (!this.yearStats) return '0h 0m';
    const h = Math.floor(this.yearStats.totalMinutes / 60);
    const m = this.yearStats.totalMinutes % 60;
    return `${h}h ${m}m`;
  }

  get currentYear(): number {
    return new Date().getFullYear();
  }

  // ── Story 35: Marketplace explore ────────────────────────────────────────
  get filteredCommunityTemplates(): ExportTemplateDto[] {
    let list = [...this.communityTemplates];

    if (this.marketplaceCategory !== 'All') {
      const cat = this.marketplaceCategory.toLowerCase();
      list = list.filter(t =>
        (t.tags || '').toLowerCase().includes(cat) ||
        t.name.toLowerCase().includes(cat) ||
        (t.description || '').toLowerCase().includes(cat)
      );
    }

    if (this.marketplaceSort === 'trending') {
      list.sort((a, b) => b.downloads - a.downloads);
    } else if (this.marketplaceSort === 'top-rated') {
      list.sort((a, b) => b.votes - a.votes);
    } else {
      list.sort((a, b) => {
        const dateA = (a as any).createdAt ? new Date((a as any).createdAt).getTime() : 0;
        const dateB = (b as any).createdAt ? new Date((b as any).createdAt).getTime() : 0;
        return dateB - dateA;
      });
    }

    return list;
  }

  formatDownloadCount(count: number): string {
    if (count >= 1000) return (count / 1000).toFixed(1).replace(/\.0$/, '') + 'k';
    return count.toString();
  }

  // ── Story 37: Cloud Text ──────────────────────────────────────────────────
  get computedCloudFontSize(): number {
    const chars = Math.max(1, this.cloudText.length);
    // auto-fit: target ~85% of 1080px canvas width
    const auto = Math.floor(918 / chars);
    return Math.min(this.cloudFontSize, auto);
  }

  getCloudTextStyle(): Record<string, string> {
    const color = this.cloudTextColor;
    const size = this.computedCloudFontSize;

    const base: Record<string, string> = {
      'font-size': `${size}px`,
      'line-height': '1',
      'letter-spacing': '-0.02em',
    };

    if (this.cloudTexture === 'cloud') {
      return {
        ...base,
        'color': 'transparent',
        'background': 'linear-gradient(180deg, #ffffff 0%, #f0f9ff 25%, #ddeeff 60%, #b8d4ec 100%)',
        '-webkit-background-clip': 'text',
        'background-clip': 'text',
        'filter': [
          'drop-shadow(0 3px 6px rgba(160,205,235,0.95))',
          'drop-shadow(0 8px 22px rgba(130,185,225,0.7))',
          'drop-shadow(0 -3px 10px rgba(255,255,255,1))',
          'drop-shadow(0 18px 40px rgba(100,155,205,0.4))',
        ].join(' '),
      };
    }

    if (this.cloudTexture === '3d') {
      const layers: string[] = [];
      for (let i = 1; i <= 10; i++) {
        layers.push(`${i}px ${i}px 0 ${this.darkenHex(color, i * 6)}`);
      }
      layers.push(`12px 12px 24px rgba(0,0,0,0.55)`);
      return {
        ...base,
        'color': color,
        'text-shadow': layers.join(', '),
      };
    }

    // neon
    return {
      ...base,
      'color': color,
      'text-shadow': [
        `0 0 6px ${color}`,
        `0 0 14px ${color}`,
        `0 0 28px ${color}`,
        `0 0 56px ${color}cc`,
        `0 0 100px ${color}88`,
        `0 0 160px ${color}44`,
      ].join(', '),
      'filter': `drop-shadow(0 0 48px ${color}66)`,
    };
  }

  private darkenHex(hex: string, percent: number): string {
    const clean = hex.replace('#', '');
    const num = parseInt(clean.length === 3
      ? clean.split('').map(c => c + c).join('')
      : clean, 16);
    const amt = Math.round(2.55 * percent);
    const r = Math.max(0, (num >> 16) - amt);
    const g = Math.max(0, ((num >> 8) & 0xff) - amt);
    const b = Math.max(0, (num & 0xff) - amt);
    return `rgb(${r},${g},${b})`;
  }
}
