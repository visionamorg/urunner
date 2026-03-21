import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule, NgClass } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { ActivityService, Streak } from '../../core/services/activity.service';
import { EventService } from '../../core/services/event.service';
import { RankingService } from '../../core/services/ranking.service';
import { ProgramService } from '../../core/services/program.service';
import { UserService } from '../../core/services/user.service';
import { Activity, ActivityStats } from '../../core/models/activity.model';
import { RunEvent } from '../../core/models/event.model';
import { Ranking } from '../../core/models/ranking.model';
import { AuthService } from '../../core/services/auth.service';

export interface ScheduleItem {
  id: string;
  type: 'activity' | 'event' | 'training';
  title: string;
  subtitle: string;
  time?: string;
  icon: string;
  color: string;
  routerLink: string | any[];
}

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
  programProgress: any[] = [];
  todayActivities: Activity[] = [];
  todaySchedule: ScheduleItem[] = [];
  streak: Streak | null = null;
  runPoints = 0;
  loading = false;
  currentUser = this.authService.getCurrentUser();

  thisWeekDays: { date: Date; isToday: boolean; hasActivity: boolean }[] = [];

  challenges = [
    { id: 1, title: '100km Month', target: 100, icon: 'emoji_events', color: 'orange', description: 'Run 100km this month' },
    { id: 2, title: '7-Day Streak', target: 7, icon: 'local_fire_department', color: 'red', description: 'Run 7 days in a row' },
    { id: 3, title: '5 Runs Week', target: 5, icon: 'repeat', color: 'blue', description: 'Log 5 runs this week' },
  ];

  constructor(
    private activityService: ActivityService,
    private eventService: EventService,
    private rankingService: RankingService,
    private programService: ProgramService,
    private userService: UserService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.activityService.getMyStreak().subscribe({
      next: s => { this.streak = s; this.cdr.detectChanges(); },
      error: () => {}
    });

    this.userService.getMe().subscribe({
      next: u => { this.runPoints = u.runPoints ?? 0; this.cdr.detectChanges(); },
      error: () => {}
    });

    this.activityService.getMyStats().subscribe({
      next: s => { this.stats = s; this.cdr.detectChanges(); },
      error: () => {}
    });

    this.activityService.getMyActivities().subscribe({
      next: a => {
        this.recentActivities = a.slice(0, 5);
        this.todayActivities = a.filter(act => {
          const d = new Date(act.activityDate);
          const today = new Date();
          return d.toDateString() === today.toDateString();
        });
        this.buildThisWeek();
        this.buildTodaySchedule();
        this.cdr.detectChanges();
      },
      error: () => {}
    });

    this.eventService.getAll().subscribe({
      next: e => {
        this.upcomingEvents = (e || [])
          .filter(ev => !ev.isCancelled)
          .sort((a, b) => new Date(a.eventDate).getTime() - new Date(b.eventDate).getTime())
          .slice(0, 3);
        this.buildTodaySchedule();
        this.cdr.detectChanges();
      },
      error: (err) => { console.error('Events error:', err); }
    });

    this.rankingService.getGlobal('weekly').subscribe({
      next: r => { this.topRankings = r.slice(0, 5); this.cdr.detectChanges(); },
      error: () => {}
    });

    this.programService.getMyProgress().subscribe({
      next: p => { this.programProgress = p.slice(0, 2); this.cdr.detectChanges(); },
      error: () => {}
    });
  }

  buildThisWeek(): void {
    const today = new Date();
    const monday = new Date(today);
    const day = monday.getDay();
    monday.setDate(today.getDate() - (day === 0 ? 6 : day - 1));

    this.thisWeekDays = [];
    for (let i = 0; i < 7; i++) {
      const d = new Date(monday);
      d.setDate(monday.getDate() + i);
      const hasActivity = this.recentActivities.some(a => new Date(a.activityDate).toDateString() === d.toDateString());
      this.thisWeekDays.push({ date: d, isToday: d.toDateString() === today.toDateString(), hasActivity });
    }
  }

  buildTodaySchedule(): void {
    const items: ScheduleItem[] = [];
    const today = new Date();

    // Real activities today
    for (const a of this.todayActivities) {
      items.push({
        id: `activity-${a.id}`,
        type: 'activity',
        title: a.title,
        subtitle: `${a.distanceKm?.toFixed(1)} km · ${this.formatDuration(a.durationMinutes)}`,
        icon: 'directions_run',
        color: 'primary',
        routerLink: '/activities'
      });
    }

    // Real events today
    for (const e of this.upcomingEvents) {
      if (new Date(e.eventDate).toDateString() === today.toDateString()) {
        items.push({
          id: `event-${e.id}`,
          type: 'event',
          title: e.name,
          subtitle: e.location || '',
          icon: 'event',
          color: 'blue',
          routerLink: ['/events', e.id]
        });
      }
    }

    // Demo schedule items for alice_runner when nothing is logged today
    if (items.length === 0 && this.currentUser?.username === 'alice_runner') {
      items.push(
        {
          id: 'demo-1',
          type: 'training',
          title: 'Marathon Prep — Tempo Run',
          subtitle: '8 km at 5:10 /km · Session 8/20',
          time: '07:00',
          icon: 'fitness_center',
          color: 'primary',
          routerLink: '/programs'
        },
        {
          id: 'demo-2',
          type: 'training',
          title: 'Evening Stretching',
          subtitle: '30 min mobility · Recovery',
          time: '19:00',
          icon: 'self_improvement',
          color: 'purple',
          routerLink: '/programs'
        }
      );
    }

    this.todaySchedule = items;
    this.cdr.detectChanges();
  }

  getScheduleItemClass(item: ScheduleItem): string {
    const base = 'flex items-center gap-3 p-2 rounded-xl border transition-all cursor-pointer bg-secondary ';
    if (item.color === 'primary') return base + 'border-primary border-opacity-30 hover:border-opacity-60';
    if (item.color === 'blue') return base + 'border-blue-400 border-opacity-30 hover:border-opacity-60';
    return base + 'border-purple-400 border-opacity-30 hover:border-opacity-60';
  }

  getScheduleIconWrapClass(item: ScheduleItem): string {
    const base = 'w-8 h-8 rounded-lg flex items-center justify-center flex-shrink-0 ';
    if (item.color === 'primary') return base + 'bg-primary bg-opacity-20';
    if (item.color === 'blue') return base + 'bg-blue-500 bg-opacity-20';
    return base + 'bg-purple-500 bg-opacity-20';
  }

  navigateToCalendar(date?: Date): void {
    const d = date || new Date();
    this.router.navigate(['/calendar'], {
      queryParams: { date: d.toISOString().split('T')[0] }
    });
  }

  navigateTo(path: string): void {
    this.router.navigate([path]);
  }

  getChallengeProgress(challenge: any): number {
    if (challenge.id === 1) return Math.min(100, ((this.stats?.monthlyDistanceKm || 0) / challenge.target) * 100);
    if (challenge.id === 2) return 0;
    if (challenge.id === 3) {
      const thisWeekRuns = this.recentActivities.filter(a => {
        const d = new Date(a.activityDate);
        const now = new Date();
        const weekStart = new Date(now);
        weekStart.setDate(now.getDate() - now.getDay());
        return d >= weekStart;
      }).length;
      return Math.min(100, (thisWeekRuns / challenge.target) * 100);
    }
    return 0;
  }

  getChallengeValue(challenge: any): string {
    if (challenge.id === 1) return `${(this.stats?.monthlyDistanceKm || 0).toFixed(1)} km`;
    if (challenge.id === 3) {
      const runs = this.recentActivities.filter(a => {
        const d = new Date(a.activityDate);
        const now = new Date();
        const ws = new Date(now);
        ws.setDate(now.getDate() - now.getDay());
        return d >= ws;
      }).length;
      return `${runs} runs`;
    }
    return '0';
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

  getTotalHours(): string {
    if (!this.stats?.totalDurationMinutes) return '0h';
    const h = Math.floor(this.stats.totalDurationMinutes / 60);
    return `${h}h`;
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

  getProgramProgress(prog: any): number {
    if (!prog.totalSessions) return 0;
    return Math.min(100, ((prog.completedSessions || 0) / prog.totalSessions) * 100);
  }
}
