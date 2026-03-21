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

  goBack(): void {
    this.router.navigate(['/activities']);
  }
}
