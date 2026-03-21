import { Component, OnInit } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';
import { UserService } from '../../../core/services/user.service';
import { AuthResponse } from '../../../core/models/user.model';
import { ThemeService } from '../../../core/services/theme.service';
import { AvatarComponent } from '../../components/avatar/avatar.component';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, RouterOutlet, RouterLinkActive, AvatarComponent],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.scss'
})
export class LayoutComponent implements OnInit {
  currentUser: AuthResponse | null = null;
  profileImageUrl: string | null = null;
  sidebarOpen = false;

  navItems = [
    { path: '/dashboard', icon: 'dashboard', label: 'Dashboard' },
    { path: '/activities', icon: 'directions_run', label: 'Activities' },
    { path: '/feed', icon: 'dynamic_feed', label: 'Feed' },
    { path: '/communities', icon: 'group', label: 'Communities' },
    { path: '/events', icon: 'event', label: 'Events' },
    { path: '/programs', icon: 'fitness_center', label: 'Programs' },
    { path: '/rankings', icon: 'leaderboard', label: 'Rankings' },
    { path: '/profile', icon: 'person', label: 'Profile' },
    { path: '/chat', icon: 'chat', label: 'Chat' },
    { path: '/export-studio', icon: 'photo_camera', label: 'Export Studio' }
  ];

  mobileNavItems = [
    { path: '/dashboard', icon: 'dashboard', label: 'Home' },
    { path: '/activities', icon: 'directions_run', label: 'Run' },
    { path: '/feed', icon: 'dynamic_feed', label: 'Feed' },
    { path: '/rankings', icon: 'leaderboard', label: 'Ranks' },
    { path: '/profile', icon: 'person', label: 'Profile' }
  ];

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private router: Router,
    public themeService: ThemeService
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      if (user) {
        this.userService.getMe().subscribe({
          next: (profile) => { this.profileImageUrl = profile.profileImageUrl ?? null; },
          error: () => {}
        });
      } else {
        this.profileImageUrl = null;
      }
    });
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
}
