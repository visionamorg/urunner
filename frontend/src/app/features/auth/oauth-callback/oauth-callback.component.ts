import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-oauth-callback',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="min-h-screen bg-brand-bg flex flex-col items-center justify-center gap-6">
      @if (error) {
        <div class="flex flex-col items-center gap-4 animate-fade-in">
          <div class="w-16 h-16 rounded-full bg-red-500/20 border border-red-500/30 flex items-center justify-center">
            <span class="material-icons text-red-400 text-3xl">error_outline</span>
          </div>
          <h2 class="text-xl font-bold text-red-400">Authentication failed</h2>
          <p class="text-slate-400 text-center max-w-sm">{{ error }}</p>
          <a href="/login" class="btn-primary">Back to login</a>
        </div>
      } @else {
        <div class="flex flex-col items-center gap-4 animate-fade-in">
          <div class="animate-spin rounded-full h-12 w-12 border-2 border-orange-500 border-t-transparent"></div>
          <p class="text-slate-400 font-medium">Completing sign-in…</p>
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
