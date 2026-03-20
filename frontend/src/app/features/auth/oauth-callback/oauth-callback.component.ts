import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-oauth-callback',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="min-h-screen bg-background flex flex-col items-center justify-center gap-6">
      @if (error) {
        <div class="flex flex-col items-center gap-4 animate-fade-in">
          <div class="w-16 h-16 rounded-full bg-destructive/20 border border-destructive/30 flex items-center justify-center">
            <span class="material-icons text-destructive text-3xl">error_outline</span>
          </div>
          <h2 class="text-xl font-bold text-destructive">Authentication failed</h2>
          <p class="text-muted-foreground text-center max-w-sm">{{ error }}</p>
          <a href="/login" class="btn-primary">Back to login</a>
        </div>
      } @else {
        <div class="flex flex-col items-center gap-4 animate-fade-in">
          <div class="animate-spin rounded-full h-12 w-12 border-2 border-primary border-t-transparent"></div>
          <p class="text-muted-foreground font-medium">Completing sign-in…</p>
        </div>
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
