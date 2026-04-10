import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-public-profile',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="max-w-2xl mx-auto space-y-6" *ngIf="profile">
      <div class="card p-6">
        <div class="flex items-start gap-4">
          <div class="w-16 h-16 rounded-2xl bg-primary flex items-center justify-center text-white text-2xl font-black flex-shrink-0 overflow-hidden">
            <img *ngIf="profile.profileImageUrl" [src]="profile.profileImageUrl" class="w-full h-full object-cover" alt="avatar" />
            <span *ngIf="!profile.profileImageUrl">{{ getInitials(profile) }}</span>
          </div>
          <div class="flex-1">
            <h1 class="text-2xl font-bold text-foreground">{{ profile.firstName }} {{ profile.lastName }}</h1>
            <p class="text-muted-foreground">&#64;{{ profile.username }}</p>
            <p class="text-sm text-muted-foreground mt-1" *ngIf="profile.location">📍 {{ profile.location }}</p>
            <p class="text-sm mt-2" *ngIf="profile.bio">{{ profile.bio }}</p>
          </div>
          <button *ngIf="!isOwnProfile"
            (click)="toggleFollow()"
            [disabled]="followLoading"
            class="px-4 py-2 rounded-lg font-medium transition-all flex-shrink-0"
            [ngClass]="profile.following ? 'bg-secondary text-foreground border border-border hover:bg-red-500/10 hover:text-red-400 hover:border-red-500/30' : 'btn-primary'">
            {{ followLoading ? '...' : (profile.following ? 'Following' : 'Follow') }}
          </button>
        </div>
        <div class="flex gap-6 mt-4 pt-4 border-t border-border">
          <div class="text-center">
            <p class="text-xl font-bold text-foreground">{{ profile.followerCount }}</p>
            <p class="text-xs text-muted-foreground">Followers</p>
          </div>
          <div class="text-center">
            <p class="text-xl font-bold text-foreground">{{ profile.followingCount }}</p>
            <p class="text-xs text-muted-foreground">Following</p>
          </div>
          <div class="text-center" *ngIf="profile.totalKm">
            <p class="text-xl font-bold text-foreground">{{ profile.totalKm | number:'1.0-0' }} km</p>
            <p class="text-xs text-muted-foreground">Total Distance</p>
          </div>
        </div>
      </div>
    </div>
    <div *ngIf="loading" class="flex items-center justify-center h-40">
      <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
    </div>
    <div *ngIf="!loading && !profile" class="card p-8 text-center text-muted-foreground">
      <span class="material-icons text-4xl mb-2 block">person_off</span>
      <p>User not found</p>
    </div>
  `
})
export class PublicProfileComponent implements OnInit {
  profile: any = null;
  loading = true;
  followLoading = false;
  isOwnProfile = false;

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const username = this.route.snapshot.paramMap.get('username');
    const me = this.authService.getCurrentUser();
    this.isOwnProfile = me?.username === username;
    this.http.get<any>(`/api/users/by-username/${username}`).subscribe({
      next: p => { this.profile = p; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  toggleFollow(): void {
    if (!this.profile) return;
    this.followLoading = true;
    const method = this.profile.following ? 'delete' : 'post';
    this.http.request(method, `/api/users/${this.profile.username}/follow`).subscribe({
      next: () => {
        this.profile.following = !this.profile.following;
        this.profile.followerCount += this.profile.following ? 1 : -1;
        this.followLoading = false;
      },
      error: () => { this.followLoading = false; }
    });
  }

  getInitials(profile: any): string {
    const f = profile.firstName?.[0] || '';
    const l = profile.lastName?.[0] || '';
    return (f + l).toUpperCase() || profile.username.substring(0, 2).toUpperCase();
  }
}
