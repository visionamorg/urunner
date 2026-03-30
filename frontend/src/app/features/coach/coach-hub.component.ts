import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-coach-hub',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './coach-hub.component.html'
})
export class CoachHubComponent implements OnInit {

  activeTab: 'feed' | 'readiness' | 'analytics' | 'team' | 'bulk' = 'feed';

  athletes: any[] = [];
  teamFeed: any[] = [];
  readiness: any[] = [];
  analytics: any[] = [];

  loadingFeed = false;
  loadingReadiness = false;
  loadingAnalytics = false;

  inviteUsername = '';
  inviteAccessLevel = 'BASIC';
  inviting = false;
  inviteMessage = '';

  bulkSessionId: number | null = null;
  selectedAthletes: number[] = [];
  bulkPushResult: any = null;
  bulkPushing = false;

  constructor(private http: HttpClient, private authService: AuthService) {}

  ngOnInit(): void {
    this.loadAthletes();
    this.loadTeamFeed();
  }

  loadAthletes(): void {
    this.http.get<any[]>('/api/coaching/my-athletes').subscribe({
      next: data => { this.athletes = data; },
      error: () => {}
    });
  }

  loadTeamFeed(): void {
    this.loadingFeed = true;
    this.http.get<any[]>('/api/coaching/team/feed').subscribe({
      next: data => { this.teamFeed = data; this.loadingFeed = false; },
      error: () => { this.loadingFeed = false; }
    });
  }

  loadReadiness(): void {
    this.loadingReadiness = true;
    this.http.get<any[]>('/api/coaching/team/readiness').subscribe({
      next: data => { this.readiness = data; this.loadingReadiness = false; },
      error: () => { this.loadingReadiness = false; }
    });
  }

  loadAnalytics(): void {
    this.loadingAnalytics = true;
    this.http.get<any[]>('/api/coaching/team/analytics').subscribe({
      next: data => { this.analytics = data; this.loadingAnalytics = false; },
      error: () => { this.loadingAnalytics = false; }
    });
  }

  switchTab(tab: string): void {
    this.activeTab = tab as 'feed' | 'readiness' | 'analytics' | 'team' | 'bulk';
    if (tab === 'readiness' && this.readiness.length === 0) this.loadReadiness();
    if (tab === 'analytics' && this.analytics.length === 0) this.loadAnalytics();
  }

  inviteAthlete(): void {
    if (!this.inviteUsername.trim() || this.inviting) return;
    this.inviting = true;
    this.inviteMessage = '';
    this.http.post<any>('/api/coaching/invite', {
      usernameOrEmail: this.inviteUsername,
      accessLevel: this.inviteAccessLevel
    }).subscribe({
      next: () => {
        this.inviteMessage = 'Invite sent! Token delivered.';
        this.inviteUsername = '';
        this.inviting = false;
        this.loadAthletes();
      },
      error: err => {
        this.inviteMessage = err.error?.message || 'Failed to send invite.';
        this.inviting = false;
      }
    });
  }

  revokeConnection(id: number): void {
    this.http.delete(`/api/coaching/${id}/revoke`).subscribe({
      next: () => { this.athletes = this.athletes.filter((a: any) => a.id !== id); },
      error: () => {}
    });
  }

  toggleAthleteSelection(id: number): void {
    const idx = this.selectedAthletes.indexOf(id);
    if (idx >= 0) {
      this.selectedAthletes.splice(idx, 1);
    } else {
      this.selectedAthletes.push(id);
    }
  }

  isAthleteSelected(id: number): boolean {
    return this.selectedAthletes.includes(id);
  }

  bulkPush(): void {
    if (!this.bulkSessionId || this.selectedAthletes.length === 0 || this.bulkPushing) return;
    this.bulkPushing = true;
    this.bulkPushResult = null;
    this.http.post<any>('/api/garmin/clipboard/bulk-push', {
      sessionId: this.bulkSessionId,
      athleteIds: this.selectedAthletes
    }).subscribe({
      next: result => {
        this.bulkPushResult = result;
        this.bulkPushing = false;
      },
      error: () => { this.bulkPushing = false; }
    });
  }

  getRiskColor(risk: string): string {
    if (risk === 'GREEN') return 'text-green-400';
    if (risk === 'YELLOW') return 'text-yellow-400';
    if (risk === 'RED') return 'text-red-400';
    return 'text-muted-foreground';
  }

  getRiskIcon(risk: string): string {
    return '●';
  }

  formatPace(pace: number): string {
    if (!pace) return '--';
    const min = Math.floor(pace);
    const sec = Math.round((pace - min) * 60);
    return `${min}:${sec.toString().padStart(2, '0')} /km`;
  }

  formatDate(date: string): string {
    if (!date) return '';
    return new Date(date).toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
  }

  getTrendPoints(trend: any[], field: string, width: number, height: number): string {
    if (!trend || trend.length < 2) return '';
    const values = trend.map((p: any) => p[field] as number);
    const min = Math.min(...values);
    const max = Math.max(...values);
    const range = max - min || 1;
    const pad = 5;
    return trend.map((p: any, i: number) => {
      const x = pad + (i / (trend.length - 1)) * (width - pad * 2);
      const y = height - pad - ((p[field] - min) / range) * (height - pad * 2);
      return `${x.toFixed(1)},${y.toFixed(1)}`;
    }).join(' ');
  }
}
