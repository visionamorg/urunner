import { Component, OnInit } from '@angular/core';
import { CommonModule, TitleCasePipe } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatChipsModule } from '@angular/material/chips';
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
  imports: [
    CommonModule, TitleCasePipe, ReactiveFormsModule,
    MatCardModule, MatButtonModule, MatIconModule,
    MatFormFieldModule, MatInputModule, MatProgressSpinnerModule,
    MatSnackBarModule, MatChipsModule
  ],
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
}
