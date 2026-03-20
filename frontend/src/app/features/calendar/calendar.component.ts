import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { ActivityService } from '../../core/services/activity.service';
import { EventService } from '../../core/services/event.service';
import { Activity } from '../../core/models/activity.model';
import { RunEvent } from '../../core/models/event.model';

interface CalendarDay {
  date: Date;
  isCurrentMonth: boolean;
  isToday: boolean;
  activities: Activity[];
  events: RunEvent[];
}

@Component({
  selector: 'app-calendar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './calendar.component.html',
  styleUrl: './calendar.component.scss'
})
export class CalendarComponent implements OnInit {
  currentDate = new Date();
  displayMonth: Date;
  weeks: CalendarDay[][] = [];
  selectedDay: CalendarDay | null = null;

  allActivities: Activity[] = [];
  allEvents: RunEvent[] = [];
  loading = true;

  weekDayLabels = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];

  constructor(
    private activityService: ActivityService,
    private eventService: EventService,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {
    this.displayMonth = new Date(this.currentDate.getFullYear(), this.currentDate.getMonth(), 1);
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (params['date']) {
        const d = new Date(params['date']);
        if (!isNaN(d.getTime())) {
          this.displayMonth = new Date(d.getFullYear(), d.getMonth(), 1);
        }
      }
    });

    this.activityService.getMyActivities().subscribe({
      next: a => {
        this.allActivities = a;
        this.buildCalendar();
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => { this.loading = false; }
    });

    this.eventService.getAll().subscribe({
      next: e => {
        this.allEvents = (e || []).filter((ev: RunEvent) => !ev.isCancelled);
        this.buildCalendar();
        this.cdr.detectChanges();
      },
      error: () => {}
    });
  }

  buildCalendar(): void {
    const year = this.displayMonth.getFullYear();
    const month = this.displayMonth.getMonth();

    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);

    // Start from Monday of the week containing the 1st
    const startDate = new Date(firstDay);
    const dayOfWeek = firstDay.getDay();
    const offset = dayOfWeek === 0 ? 6 : dayOfWeek - 1;
    startDate.setDate(firstDay.getDate() - offset);

    this.weeks = [];
    const current = new Date(startDate);

    while (true) {
      const week: CalendarDay[] = [];
      for (let d = 0; d < 7; d++) {
        const dayDate = new Date(current);
        const dayStr = dayDate.toDateString();
        week.push({
          date: dayDate,
          isCurrentMonth: dayDate.getMonth() === month,
          isToday: dayStr === this.currentDate.toDateString(),
          activities: this.allActivities.filter(a => new Date(a.activityDate).toDateString() === dayStr),
          events: this.allEvents.filter(e => new Date(e.eventDate).toDateString() === dayStr)
        });
        current.setDate(current.getDate() + 1);
      }
      this.weeks.push(week);
      if (current > lastDay && this.weeks.length >= 4) break;
    }

    // Default select today if visible, else first day of month
    if (!this.selectedDay) {
      for (const week of this.weeks) {
        const today = week.find(d => d.isToday);
        if (today) { this.selectedDay = today; break; }
      }
      if (!this.selectedDay) {
        this.selectedDay = this.weeks[0]?.find(d => d.isCurrentMonth) || null;
      }
    }
  }

  prevMonth(): void {
    this.displayMonth = new Date(this.displayMonth.getFullYear(), this.displayMonth.getMonth() - 1, 1);
    this.selectedDay = null;
    this.buildCalendar();
  }

  nextMonth(): void {
    this.displayMonth = new Date(this.displayMonth.getFullYear(), this.displayMonth.getMonth() + 1, 1);
    this.selectedDay = null;
    this.buildCalendar();
  }

  goToToday(): void {
    this.displayMonth = new Date(this.currentDate.getFullYear(), this.currentDate.getMonth(), 1);
    this.selectedDay = null;
    this.buildCalendar();
  }

  selectDay(day: CalendarDay): void {
    this.selectedDay = day;
  }

  formatDuration(min: number): string {
    const h = Math.floor(min / 60);
    const m = min % 60;
    return h > 0 ? `${h}h ${m}m` : `${m}m`;
  }

  formatPace(pace: number): string {
    if (!pace) return '--';
    const min = Math.floor(pace);
    const sec = Math.round((pace - min) * 60);
    return `${min}:${sec.toString().padStart(2, '0')} /km`;
  }

  get monthLabel(): string {
    return this.displayMonth.toLocaleDateString('en-US', { month: 'long', year: 'numeric' });
  }

  get selectedDayLabel(): string {
    if (!this.selectedDay) return '';
    return this.selectedDay.date.toLocaleDateString('en-US', { weekday: 'long', month: 'long', day: 'numeric' });
  }

  isSelectedDay(day: CalendarDay): boolean {
    return !!this.selectedDay && this.selectedDay.date.toDateString() === day.date.toDateString();
  }

  getDayBg(day: CalendarDay): string {
    if (this.isSelectedDay(day) && day.isToday) return 'bg-primary ring-2 ring-primary';
    if (this.isSelectedDay(day)) return 'bg-secondary ring-2 ring-primary';
    if (day.isToday) return 'bg-primary';
    return 'bg-transparent hover:bg-secondary';
  }

  getDayTextColor(day: CalendarDay): string {
    if (day.isToday || this.isSelectedDay(day)) return 'text-foreground';
    return 'text-muted-foreground';
  }

  get monthActivityCount(): number {
    return this.weeks.reduce((sum, week) =>
      sum + week.filter(d => d.isCurrentMonth).reduce((s, d) => s + d.activities.length, 0), 0);
  }

  get monthEventCount(): number {
    return this.weeks.reduce((sum, week) =>
      sum + week.filter(d => d.isCurrentMonth).reduce((s, d) => s + d.events.length, 0), 0);
  }
}
