import { Component, OnInit, ChangeDetectorRef, ViewChild } from '@angular/core';
import { CommonModule, NgClass } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { NgApexchartsModule, ApexOptions, ChartComponent } from 'ng-apexcharts';
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
  imports: [CommonModule, RouterModule, NgApexchartsModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  @ViewChild('chart') chart!: ChartComponent;
  public mainChartOptions: Partial<ApexOptions> = {};
  public sparklineOptions1: Partial<ApexOptions> = {};
  public sparklineOptions2: Partial<ApexOptions> = {};
  public sparklineOptions3: Partial<ApexOptions> = {};
  public sparklineOptions4: Partial<ApexOptions> = {};
  public sparklineOptions5: Partial<ApexOptions> = {};
  public sparklineOptions6: Partial<ApexOptions> = {};
  public heatmapOptions: Partial<ApexOptions> = {};
  public radialBarOptions: Partial<ApexOptions> = {};
  public enrichedRecentActivities: any[] = [];

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
    this.initCharts();
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
        this.enrichedRecentActivities = this.recentActivities.map((act, index) => {
          const mockPolylines = [
            'M5,80 C15,20 35,20 50,50 S85,80 95,20',
            'M10,50 Q25,10 50,60 T90,50',
            'M5,20 Q40,80 50,50 T95,80',
            'M10,10 C20,50 60,30 90,80',
            'M10,50 L30,20 L50,50 L70,80 L90,50'
          ];
          const prPercentage = Math.floor(Math.random() * 30) + 70; // 70-100%
          const isPR = prPercentage >= 98;
          return {
            ...act,
            prPercentage,
            prColor: isPR ? 'bg-orange-500' : 'bg-primary',
            mockPolyline: mockPolylines[index % mockPolylines.length]
          };
        });
        
        this.todayActivities = a.filter(act => {
          const d = new Date(act.activityDate);
          const today = new Date();
          return d.toDateString() === today.toDateString();
        });
        this.buildThisWeek();
        this.buildTodaySchedule();
        this.updateCharts(a);
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

  private initCharts(): void {
    const commonSparklineOptions: Partial<ApexOptions> = {
      chart: { type: 'area', width: '100%', height: 40, sparkline: { enabled: true } },
      stroke: { curve: 'smooth', width: 2 },
      fill: { type: 'gradient', gradient: { shadeIntensity: 1, opacityFrom: 0.4, opacityTo: 0, stops: [0, 100] } },
      tooltip: { fixed: { enabled: false }, x: { show: false }, y: { title: { formatter: () => '' } }, marker: { show: false } }
    };

    const redSparkline = { ...commonSparklineOptions, colors: ['#ef4444'] };
    const orangeSparkline = { ...commonSparklineOptions, colors: ['#f97316'] };
    const blueSparkline = { ...commonSparklineOptions, colors: ['#3b82f6'] };
    const purpleSparkline = { ...commonSparklineOptions, colors: ['#a855f7'] };

    this.sparklineOptions1 = { ...orangeSparkline, series: [{ data: [] }] };
    this.sparklineOptions2 = { ...redSparkline, series: [{ data: [] }] };
    this.sparklineOptions3 = { ...orangeSparkline, series: [{ data: [] }] };
    this.sparklineOptions4 = { ...redSparkline, series: [{ data: [] }] };
    this.sparklineOptions5 = { ...blueSparkline, series: [{ data: [] }] };
    this.sparklineOptions6 = { ...purpleSparkline, series: [{ data: [] }] };

    this.mainChartOptions = {
      series: [
        { name: 'Distance (KM)', data: [] },
        { name: 'Pace (min/km)', data: [] }
      ],
      chart: { type: 'area', height: 350, toolbar: { show: false }, zoom: { enabled: false }, fontFamily: 'inherit' },
      colors: ['#f97316', '#3b82f6'],
      dataLabels: { enabled: false },
      stroke: { curve: 'smooth', width: 3 },
      fill: { type: 'gradient', gradient: { shadeIntensity: 1, opacityFrom: 0.4, opacityTo: 0.05, stops: [0, 90, 100] } },
      xaxis: { categories: [], axisBorder: { show: false }, axisTicks: { show: false }, tooltip: { enabled: false }, labels: { style: { colors: '#9ca3af' } } },
      yaxis: [
        { title: { text: 'Distance (KM)', style: { color: '#f97316' } }, labels: { style: { colors: '#9ca3af' } } },
        { opposite: true, title: { text: 'Avg Pace', style: { color: '#3b82f6' } }, labels: { style: { colors: '#9ca3af' } } }
      ],
      legend: { position: 'top', horizontalAlign: 'right', labels: { colors: '#e5e7eb' } },
      grid: { borderColor: 'rgba(255, 255, 255, 0.1)', strokeDashArray: 4, yaxis: { lines: { show: true } } }
    };

    const heatmapSeries = [];
    for (let i = 0; i < 7; i++) {
       heatmapSeries.push({ name: ['Mon','Tue','Wed','Thu','Fri','Sat','Sun'][i], data: Array(52).fill(0) });
    }

    this.heatmapOptions = {
      series: heatmapSeries,
      chart: { type: 'heatmap', height: 280, toolbar: { show: false }, fontFamily: 'inherit' },
      dataLabels: { enabled: false },
      colors: ['#f97316'],
      plotOptions: { heatmap: { shadeIntensity: 0.5, radius: 4, useFillColorAsStroke: false, colorScale: { ranges: [
        { from: 0, to: 0, color: 'rgba(255,255,255,0.02)', name: 'No Activity' },
        { from: 0.1, to: 5, color: '#fcd34d', name: 'Light' },
        { from: 5.1, to: 15, color: '#f97316', name: 'Moderate' },
        { from: 15.1, to: 100, color: '#dc2626', name: 'High' }
      ] } } },
      xaxis: { labels: { show: false }, axisBorder: { show: false }, axisTicks: { show: false } },
      legend: { show: false }
    };

    this.radialBarOptions = {
      series: [75],
      chart: { type: 'radialBar', height: 320, fontFamily: 'inherit' },
      plotOptions: {
        radialBar: {
          startAngle: -135,
          endAngle: 135,
          hollow: { margin: 15, size: '65%', image: undefined, imageOffsetX: 0, imageOffsetY: 0, position: 'front' },
          track: { background: 'rgba(255,255,255,0.1)', strokeWidth: '100%', margin: 0, dropShadow: { enabled: true, top: 0, left: 0, blur: 3, opacity: 0.5 } },
          dataLabels: {
            show: true,
            name: { offsetY: 20, show: true, color: '#9ca3af', fontSize: '12px' },
            value: { offsetY: -10, color: '#f97316', fontSize: '36px', show: true, formatter: val => val + '%' }
          }
        }
      },
      fill: { type: 'gradient', gradient: { shade: 'dark', type: 'horizontal', shadeIntensity: 0.5, gradientToColors: ['#f59e0b'], inverseColors: true, opacityFrom: 1, opacityTo: 1, stops: [0, 100] } },
      stroke: { lineCap: 'round' },
      labels: ['Monthly Target']
    };
  }

  private updateCharts(activities: Activity[]): void {
    const generateData = (base: number, variance: number) => Array.from({length: 7}, () => Math.max(0, base + (Math.random() * variance * 2 - variance)));
    
    this.sparklineOptions1.series = [{ name: 'Total KM', data: generateData(5, 3) }];
    this.sparklineOptions2.series = [{ name: 'This Week', data: generateData(3, 2) }];
    this.sparklineOptions3.series = [{ name: 'This Month', data: generateData(8, 5) }];
    this.sparklineOptions4.series = [{ name: 'Total Runs', data: generateData(1, 1) }];
    this.sparklineOptions5.series = [{ name: 'Avg Pace', data: generateData(5.5, 0.5) }];
    this.sparklineOptions6.series = [{ name: 'Total Time', data: generateData(45, 20) }];

    const last7Days = Array.from({length: 7}, (_, i) => {
      const d = new Date();
      d.setDate(d.getDate() - (6 - i));
      return d.toLocaleDateString('en-US', { weekday: 'short' });
    });
    
    this.mainChartOptions.xaxis = { ...this.mainChartOptions.xaxis, categories: last7Days };
    this.mainChartOptions.series = [
      { name: 'Distance (KM)', data: generateData(8, 6).map(n => Math.round(n * 10) / 10) },
      { name: 'Pace (min/km)', data: generateData(5.3, 0.8).map(n => Math.round(n * 10) / 10) }
    ];
    
    const hmSeries = [...(this.heatmapOptions.series as any[] || [])];
    const updatedSeries = hmSeries.map(dayRow => ({
      name: dayRow.name,
      data: (dayRow.data as number[]).map(() => Math.random() > 0.6 ? Math.random() * 20 : 0)
    }));
    this.heatmapOptions.series = updatedSeries;

    if (this.stats && this.stats.monthlyDistanceKm) {
       const goal = 100;
       const percent = Math.min(100, Math.round((this.stats.monthlyDistanceKm / goal) * 100));
       this.radialBarOptions.series = [percent];
    }
  }
}
