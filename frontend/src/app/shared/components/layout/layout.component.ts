import { Component, OnInit } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../../../core/services/auth.service';
import { AuthResponse } from '../../../core/models/user.model';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [
    RouterOutlet, RouterLink, RouterLinkActive, CommonModule,
    MatSidenavModule, MatToolbarModule, MatListModule,
    MatIconModule, MatButtonModule
  ],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.scss'
})
export class LayoutComponent implements OnInit {
  currentUser: AuthResponse | null = null;

  navItems = [
    { path: '/dashboard', icon: 'dashboard', label: 'Dashboard' },
    { path: '/activities', icon: 'directions_run', label: 'Activities' },
    { path: '/communities', icon: 'group', label: 'Communities' },
    { path: '/events', icon: 'event', label: 'Events' },
    { path: '/feed', icon: 'dynamic_feed', label: 'Feed' },
    { path: '/programs', icon: 'fitness_center', label: 'Programs' },
    { path: '/rankings', icon: 'leaderboard', label: 'Rankings' },
    { path: '/chat', icon: 'chat', label: 'Chat' },
    { path: '/profile', icon: 'person', label: 'Profile' }
  ];

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  getInitials(): string {
    if (!this.currentUser) return '?';
    return this.currentUser.username.substring(0, 2).toUpperCase();
  }
}
