import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule, TitleCasePipe } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { UserService } from '../../core/services/user.service';
import { BadgeService } from '../../core/services/badge.service';
import { ActivityService } from '../../core/services/activity.service';
import { AuthService } from '../../core/services/auth.service';
import { User } from '../../core/models/user.model';
import { UserBadge } from '../../core/models/badge.model';
import { ActivityStats } from '../../core/models/activity.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, TitleCasePipe, ReactiveFormsModule, MatSnackBarModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent implements OnInit {
  user: User | null = null;
  badges: UserBadge[] = [];
  stats: ActivityStats | null = null;
  loading = true;
  editMode = false;
  syncing = false;
  saving = false;
  form: FormGroup;
  authUser = this.authService.getCurrentUser();

  readonly categories = ['TRAIL', 'MARATHON', 'SPRINT', 'ULTRA', 'ROAD', 'CASUAL', 'TRACK'];
  readonly genders = ['Male', 'Female', 'Non-binary', 'Prefer not to say'];

  constructor(
    private userService: UserService,
    private badgeService: BadgeService,
    private activityService: ActivityService,
    private authService: AuthService,
    private fb: FormBuilder,
    private snackBar: MatSnackBar,
    private cdr: ChangeDetectorRef
  ) {
    this.form = this.fb.group({
      firstName: [''],
      lastName: [''],
      profileImageUrl: [''],
      bio: [''],
      passion: [''],
      location: [''],
      runningCategory: [''],
      gender: [''],
      yearsRunning: [null],
      weeklyGoalKm: [null],
      pb5k: [''],
      pb10k: [''],
      pbHalfMarathon: [''],
      pbMarathon: [''],
      instagramHandle: ['']
    });
  }

  ngOnInit(): void {
    this.userService.getMe().subscribe(u => {
      this.user = u;
      this.patchForm(u);
      this.loading = false;
      this.cdr.detectChanges();
    });
    this.badgeService.getMyBadges().subscribe(b => { this.badges = b; this.cdr.detectChanges(); });
    this.activityService.getMyStats().subscribe(s => { this.stats = s; this.cdr.detectChanges(); });
  }

  private patchForm(u: User): void {
    this.form.patchValue({
      firstName: u.firstName,
      lastName: u.lastName,
      profileImageUrl: u.profileImageUrl || '',
      bio: u.bio || '',
      passion: u.passion || '',
      location: u.location || '',
      runningCategory: u.runningCategory || '',
      gender: u.gender || '',
      yearsRunning: u.yearsRunning ?? null,
      weeklyGoalKm: u.weeklyGoalKm ?? null,
      pb5k: u.pb5k || '',
      pb10k: u.pb10k || '',
      pbHalfMarathon: u.pbHalfMarathon || '',
      pbMarathon: u.pbMarathon || '',
      instagramHandle: u.instagramHandle || ''
    });
  }

  save(): void {
    this.saving = true;
    const val = this.form.value;
    const payload: any = {
      firstName: val.firstName,
      lastName: val.lastName,
      profileImageUrl: val.profileImageUrl || null,
      bio: val.bio || null,
      passion: val.passion || null,
      location: val.location || null,
      runningCategory: val.runningCategory || null,
      gender: val.gender || null,
      yearsRunning: val.yearsRunning || null,
      weeklyGoalKm: val.weeklyGoalKm || null,
      pb5k: val.pb5k || null,
      pb10k: val.pb10k || null,
      pbHalfMarathon: val.pbHalfMarathon || null,
      pbMarathon: val.pbMarathon || null,
      instagramHandle: val.instagramHandle || null
    };
    this.userService.updateMe(payload).subscribe({
      next: u => {
        this.user = u;
        this.patchForm(u);
        this.editMode = false;
        this.saving = false;
        this.snackBar.open('Profile updated!', 'Close', { duration: 2000 });
        this.cdr.detectChanges();
      },
      error: () => {
        this.saving = false;
        this.snackBar.open('Failed to save profile', 'Close', { duration: 3000 });
      }
    });
  }

  syncActivities(): void {
    const provider = this.authUser?.provider;
    if (!provider || provider === 'LOCAL') return;
    this.syncing = true;
    const sync$ = provider === 'STRAVA'
        ? this.activityService.syncStrava()
        : this.activityService.syncGarmin();
    sync$.subscribe({
      next: result => {
        this.snackBar.open(result.message, 'Close', { duration: 4000 });
        this.activityService.getMyStats().subscribe(s => { this.stats = s; this.cdr.detectChanges(); });
        this.syncing = false;
      },
      error: err => {
        this.snackBar.open(err.error?.message || 'Sync failed', 'Close', { duration: 3000 });
        this.syncing = false;
      }
    });
  }

  connectStrava(): void { this.authService.connectStrava(); }
  connectGarmin(): void { this.authService.connectGarmin(); }

  getInitials(): string {
    if (!this.user) return '?';
    const first = this.user.firstName?.[0] || '';
    const last = this.user.lastName?.[0] || '';
    return (first + last).toUpperCase() || this.user.username.substring(0, 2).toUpperCase();
  }

  getCategoryColor(cat?: string): string {
    const map: Record<string, string> = {
      TRAIL: 'bg-green-500/20 text-green-400 border-green-500/30',
      MARATHON: 'bg-blue-500/20 text-blue-400 border-blue-500/30',
      SPRINT: 'bg-yellow-500/20 text-yellow-400 border-yellow-500/30',
      ULTRA: 'bg-purple-500/20 text-purple-400 border-purple-500/30',
      ROAD: 'bg-primary/20 text-primary border-primary/30',
      CASUAL: 'bg-muted text-muted-foreground border-border',
      TRACK: 'bg-red-500/20 text-red-400 border-red-500/30'
    };
    return map[cat || ''] || 'bg-secondary text-muted-foreground border-border';
  }

  getCategoryIcon(cat?: string): string {
    const map: Record<string, string> = {
      TRAIL: 'landscape',
      MARATHON: 'directions_run',
      SPRINT: 'flash_on',
      ULTRA: 'terrain',
      ROAD: 'route',
      CASUAL: 'self_improvement',
      TRACK: 'speed'
    };
    return map[cat || ''] || 'directions_run';
  }

  hasPbs(): boolean {
    return !!(this.user?.pb5k || this.user?.pb10k || this.user?.pbHalfMarathon || this.user?.pbMarathon);
  }

  getProviderColor(): string {
    const p = this.authUser?.provider;
    if (p === 'STRAVA') return 'bg-primary/20 text-primary border-primary/30';
    if (p === 'GARMIN') return 'bg-blue-500/20 text-blue-400 border-blue-500/30';
    return 'bg-secondary text-muted-foreground border-border';
  }

  get previewImageUrl(): string {
    return this.form.get('profileImageUrl')?.value || '';
  }
}
