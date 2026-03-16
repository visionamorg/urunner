import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-oauth-callback',
  standalone: true,
  imports: [CommonModule, MatProgressSpinnerModule, MatIconModule],
  template: `
    <div style="display:flex;flex-direction:column;align-items:center;justify-content:center;height:100vh;gap:16px">
      @if (error) {
        <mat-icon style="font-size:48px;color:#f44336">error</mat-icon>
        <h2 style="color:#f44336">Authentication failed</h2>
        <p>{{ error }}</p>
        <a href="/login">Back to login</a>
      } @else {
        <mat-spinner></mat-spinner>
        <p>Completing sign-in…</p>
      }
    </div>
  `
})
export class OAuthCallbackComponent implements OnInit {
  error = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (params['error']) {
        this.error = params['error'] === 'access_denied'
          ? 'You denied access. Please try again.'
          : 'Something went wrong. Please try again.';
        return;
      }

      if (params['token']) {
        this.authService.handleOAuthCallback({
          token: params['token'],
          username: params['username'],
          email: params['email'],
          role: params['role'],
          userId: params['userId'],
          provider: params['provider']
        });
        this.router.navigate(['/dashboard']);
      } else {
        this.error = 'No token received.';
      }
    });
  }
}
