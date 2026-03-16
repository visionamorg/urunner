import { Component, OnInit } from '@angular/core';
import { CommonModule, TitleCasePipe } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
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
  imports: [CommonModule, TitleCasePipe, ReactiveFormsModule],
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
  form: FormGroup;
  authUser = this.authService.getCurrentUser();

  constructor(
    private userService: UserService,
    private badgeService: BadgeService,
    private activityService: ActivityService,
    private authService: AuthService,
    private fb: FormBuilder,
    private snackBar: MatSnackBar
  ) {
    this.form = this.fb.group({
      firstName: [''],
      lastName: [''],
      bio: ['']
    });
  }

  ngOnInit(): void {
    this.userService.getMe().subscribe(u => {
      this.user = u;
      this.form.patchValue({ firstName: u.firstName, lastName: u.lastName, bio: u.bio });
      this.loading = false;
    });
    this.badgeService.getMyBadges().subscribe(b => this.badges = b);
    this.activityService.getMyStats().subscribe(s => this.stats = s);
  }

  save(): void {
    this.userService.updateMe(this.form.value).subscribe({
      next: u => {
        this.user = u;
        this.editMode = false;
        this.snackBar.open('Profile updated!', 'Close', { duration: 2000 });
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
        this.activityService.getMyStats().subscribe(s => this.stats = s);
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

  getProviderColor(): string {
    const p = this.authUser?.provider;
    if (p === 'STRAVA') return 'bg-orange-500/20 text-orange-400 border-orange-500/30';
    if (p === 'GARMIN') return 'bg-blue-500/20 text-blue-400 border-blue-500/30';
    return 'bg-brand-surface text-slate-400 border-brand-border';
  }
}
