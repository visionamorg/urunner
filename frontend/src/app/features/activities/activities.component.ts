import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatTableModule } from '@angular/material/table';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ActivityService } from '../../core/services/activity.service';
import { Activity, ActivityStats } from '../../core/models/activity.model';

@Component({
  selector: 'app-activities',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule,
    MatCardModule, MatButtonModule, MatIconModule,
    MatFormFieldModule, MatInputModule, MatDialogModule,
    MatTableModule, MatProgressSpinnerModule
  ],
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

  constructor(
    private activityService: ActivityService,
    private fb: FormBuilder
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
}
