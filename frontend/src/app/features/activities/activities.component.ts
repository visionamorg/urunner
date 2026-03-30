import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { ActivityService } from '../../core/services/activity.service';
import { Activity, ActivityStats } from '../../core/models/activity.model';

@Component({
  selector: 'app-activities',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './activities.component.html',
  styleUrl: './activities.component.scss'
})
export class ActivitiesComponent implements OnInit {
  activities: Activity[] = [];
  stats: ActivityStats | null = null;
  loading = true;
  showForm = false;
  form: FormGroup;
  submitting = false;
  uploadingFit = false;
  fitFile: File | null = null;

  constructor(
    private activityService: ActivityService,
    private fb: FormBuilder,
    public router: Router,
    private http: HttpClient
  ) {
    const today = new Date().toISOString().split('T')[0];
    this.form = this.fb.group({
      title: ['', Validators.required],
      distanceKm: ['', [Validators.required, Validators.min(0.1)]],
      durationMinutes: ['', [Validators.required, Validators.min(1)]],
      activityDate: [today, Validators.required],
      location: [''],
      notes: ['']
    });
  }

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.activityService.getMyActivities().subscribe({
      next: a => { this.activities = a; this.loading = false; },
      error: () => { this.loading = false; }
    });
    this.activityService.getMyStats().subscribe({
      next: s => this.stats = s
    });
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.submitting = true;
    this.activityService.createActivity(this.form.value).subscribe({
      next: () => {
        this.showForm = false;
        this.form.reset({ activityDate: new Date().toISOString().split('T')[0] });
        this.submitting = false;
        this.loadData();
      },
      error: () => { this.submitting = false; }
    });
  }

  formatPace(pace: number): string {
    if (!pace) return '--';
    const min = Math.floor(pace);
    const sec = Math.round((pace - min) * 60);
    return `${min}:${sec.toString().padStart(2, '0')} /km`;
  }

  formatDuration(min: number): string {
    const h = Math.floor(min / 60);
    const m = min % 60;
    return h > 0 ? `${h}h ${m}m` : `${m}m`;
  }

  openExportStudio(activity: Activity): void {
    this.router.navigate(['/export-studio'], { queryParams: { activityId: activity.id } });
  }

  onFitFileSelect(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.fitFile = input.files[0];
      this.uploadFit();
    }
    // Reset input so same file can be re-selected after an error
    input.value = '';
  }

  uploadFit(): void {
    if (!this.fitFile || this.uploadingFit) return;
    this.uploadingFit = true;
    const formData = new FormData();
    formData.append('file', this.fitFile);
    this.http.post<{ imported: number; skipped: number; message: string }>('/api/fit/upload', formData).subscribe({
      next: result => {
        this.uploadingFit = false;
        this.fitFile = null;
        if (result.imported > 0) {
          this.loadData();
        }
      },
      error: () => {
        this.uploadingFit = false;
        this.fitFile = null;
      }
    });
  }
}
