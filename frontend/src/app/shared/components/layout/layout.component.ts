import { Component, OnInit } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive, Router, RouterModule, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../../core/services/auth.service';
import { UserService } from '../../../core/services/user.service';
import { NotificationService } from '../../../core/services/notification.service';
import { AuthResponse } from '../../../core/models/user.model';
import { ThemeService } from '../../../core/services/theme.service';
import { AvatarComponent } from '../../components/avatar/avatar.component';
import { filter, debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { Subject, of } from 'rxjs';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, RouterOutlet, RouterLinkActive, AvatarComponent],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.scss',
  host: { '[class.studio-mode]': 'isStudioMode' }
})
export class LayoutComponent implements OnInit {
  currentUser: AuthResponse | null = null;
  profileImageUrl: string | null = null;
  sidebarOpen = false;
  isStudioMode = false;

  navItems = [
    { path: '/dashboard', icon: 'dashboard', label: 'Dashboard' },
    { path: '/activities', icon: 'directions_run', label: 'Activities' },
    { path: '/feed', icon: 'dynamic_feed', label: 'Feed' },
    { path: '/communities', icon: 'group', label: 'Communities' },
    { path: '/events', icon: 'event', label: 'Events' },
    { path: '/programs', icon: 'fitness_center', label: 'Programs' },
    { path: '/performance', icon: 'show_chart', label: 'Performance' },
    { path: '/rankings', icon: 'leaderboard', label: 'Rankings' },
    { path: '/notifications', icon: 'notifications', label: 'Notifications' },
    { path: '/profile', icon: 'person', label: 'Profile' },
    { path: '/chat', icon: 'chat', label: 'Chat' },
    { path: '/export-studio', icon: 'photo_camera', label: 'Export Studio' },
    { path: '/coach', icon: 'sports', label: 'Coach Hub' },
    { path: '/garmin-clipboard', icon: 'watch', label: 'Garmin' }
  ];

  mobileNavItems = [
    { path: '/dashboard', icon: 'dashboard', label: 'Home' },
    { path: '/activities', icon: 'directions_run', label: 'Run' },
    { path: '/feed', icon: 'dynamic_feed', label: 'Feed' },
    { path: '/rankings', icon: 'leaderboard', label: 'Ranks' },
    { path: '/profile', icon: 'person', label: 'Profile' }
  ];

  unreadCount = 0;

  // Search
  searchQuery = '';
  searchOpen = false;
  searchResults: any = { users: [], communities: [], events: [] };
  searchLoading = false;
  private searchSubject = new Subject<string>();

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private router: Router,
    private http: HttpClient,
    public themeService: ThemeService,
    public notificationService: NotificationService
  ) {
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(q => {
        if (q.length < 2) return of({ users: [], communities: [], events: [] });
        this.searchLoading = true;
        return this.http.get<any>(`/api/search?q=${encodeURIComponent(q)}`);
      })
    ).subscribe({
      next: r => { this.searchResults = r; this.searchLoading = false; },
      error: () => { this.searchLoading = false; }
    });
  }

  ngOnInit(): void {
    this.isStudioMode = this.router.url.startsWith('/export-studio');
    this.router.events.pipe(
      filter((e): e is NavigationEnd => e instanceof NavigationEnd)
    ).subscribe(e => {
      this.isStudioMode = e.urlAfterRedirects.startsWith('/export-studio');
    });

    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      if (user) {
        this.userService.getMe().subscribe({
          next: (profile) => { this.profileImageUrl = profile.profileImageUrl ?? null; },
          error: () => {}
        });
        this.notificationService.refreshUnreadCount();
      } else {
        this.profileImageUrl = null;
      }
    });

    this.notificationService.unreadCount.subscribe(c => this.unreadCount = c);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  toggleSidebar(): void {
    this.sidebarOpen = !this.sidebarOpen;
  }

  get user(): AuthResponse | null {
    return this.currentUser;
  }

  getInitials(): string {
    if (!this.currentUser) return '?';
    return this.currentUser.username.substring(0, 2).toUpperCase();
  }

  onSearchInput(): void {
    this.searchSubject.next(this.searchQuery);
  }

  openSearch(): void {
    this.searchOpen = true;
  }

  closeSearch(): void {
    this.searchOpen = false;
    this.searchQuery = '';
    this.searchResults = { users: [], communities: [], events: [] };
  }

  navigateResult(type: string, id: number): void {
    this.closeSearch();
    switch (type) {
      case 'user': this.router.navigate(['/profile']); break;
      case 'community': this.router.navigate(['/communities', id]); break;
      case 'event': this.router.navigate(['/events', id]); break;
    }
  }

  hasResults(): boolean {
    return (this.searchResults.users?.length > 0)
      || (this.searchResults.communities?.length > 0)
      || (this.searchResults.events?.length > 0);
  }
}
