import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ActivityService } from '../../core/services/activity.service';
import { EventService } from '../../core/services/event.service';
import { RankingService } from '../../core/services/ranking.service';
import { Activity, ActivityStats } from '../../core/models/activity.model';
import { RunEvent } from '../../core/models/event.model';
import { Ranking } from '../../core/models/ranking.model';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  stats: ActivityStats | null = null;
  recentActivities: Activity[] = [];
  upcomingEvents: RunEvent[] = [];
  topRankings: Ranking[] = [];
  loading = true;
  currentUser = this.authService.getCurrentUser();

  constructor(
    private activityService: ActivityService,
    private eventService: EventService,
    private rankingService: RankingService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.activityService.getMyStats().subscribe({
      next: s => { this.stats = s; this.checkLoading(); },
      error: () => this.checkLoading()
    });

    this.activityService.getMyActivities().subscribe({
      next: a => { this.recentActivities = a.slice(0, 5); this.checkLoading(); },
      error: () => this.checkLoading()
    });

    this.eventService.getAll().subscribe({
      next: e => {
        const now = new Date();
        this.upcomingEvents = e.filter(ev => new Date(ev.eventDate) > now).slice(0, 3);
        this.checkLoading();
      },
      error: () => this.checkLoading()
    });

    this.rankingService.getGlobal('weekly').subscribe({
      next: r => { this.topRankings = r.slice(0, 5); this.checkLoading(); },
      error: () => this.checkLoading()
    });
  }

  private loadCount = 0;
  private checkLoading(): void {
    this.loadCount++;
    if (this.loadCount >= 4) this.loading = false;
  }

  formatPace(pace: number): string {
    if (!pace) return '--';
    const min = Math.floor(pace);
    const sec = Math.round((pace - min) * 60);
    return `${min}:${sec.toString().padStart(2, '0')} /km`;
  }

  formatDuration(min: number): string {
    const h = Math.floor(min / 60);
    const m = min % 60;
    return h > 0 ? `${h}h ${m}m` : `${m}m`;
  }

  getGreeting(): string {
    const hour = new Date().getHours();
    if (hour < 12) return 'Good morning';
    if (hour < 17) return 'Good afternoon';
    return 'Good evening';
  }

  getInitials(username: string): string {
    return username.substring(0, 2).toUpperCase();
  }
}
