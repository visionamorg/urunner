import { Component, Input, OnInit, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RunEvent, CreateEventRequest } from '../../../core/models/event.model';
import { CommunityService } from '../../../core/services/community.service';

interface CalendarDay {
  date: Date;
  isCurrentMonth: boolean;
  isToday: boolean;
  events: RunEvent[];
}

@Component({
  selector: 'app-community-calendar',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './community-calendar.component.html',
  styleUrl: './community-calendar.component.scss'
})
export class CommunityCalendarComponent implements OnInit {
  @Input() communityId!: number;
  @Input() isAdmin = false;
  @Output() eventCreated = new EventEmitter<RunEvent>();

  events: RunEvent[] = [];
  calendarDays: CalendarDay[] = [];
  selectedDay: CalendarDay | null = null;

  currentYear = new Date().getFullYear();
  currentMonth = new Date().getMonth();
  selectedDate: Date = new Date();

  viewMode: 'month' | 'week' | 'day' | 'agenda' = 'month';
  weekDays: { date: Date; events: RunEvent[] }[] = [];
  dayEvents: RunEvent[] = [];
  agendaEvents: RunEvent[] = [];

  showCreateForm = false;
  creating = false;
  createError = '';

  newEvent: CreateEventRequest = {
    name: '',
    description: '',
    eventDate: '',
    location: '',
    distanceKm: 0,
    price: 0
  };

  readonly MONTH_NAMES = [
    'January', 'February', 'March', 'April', 'May', 'June',
    'July', 'August', 'September', 'October', 'November', 'December'
  ];
  readonly DAY_NAMES = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

  constructor(private communityService: CommunityService) {}

  ngOnInit(): void {
    this.loadEvents();
  }

  loadEvents(): void {
    this.communityService.getCommunityEvents(this.communityId).subscribe({
      next: (events) => {
        this.events = events;
        this.buildCurrentView();
      },
      error: () => { this.buildCurrentView(); }
    });
  }

  setView(mode: 'month' | 'week' | 'day' | 'agenda'): void {
    this.viewMode = mode;
    this.buildCurrentView();
  }

  buildCurrentView(): void {
    if (this.viewMode === 'month') this.buildCalendar();
    else if (this.viewMode === 'week') this.buildWeekView();
    else if (this.viewMode === 'day') this.buildDayView();
    else this.buildAgendaView();
  }

  buildWeekView(): void {
    const startOfWeek = new Date(this.selectedDate);
    const day = startOfWeek.getDay();
    const diff = day === 0 ? -6 : 1 - day;
    startOfWeek.setDate(startOfWeek.getDate() + diff);

    this.weekDays = [];
    for (let i = 0; i < 7; i++) {
      const date = new Date(startOfWeek);
      date.setDate(startOfWeek.getDate() + i);
      const dayEventsForDate = this.events.filter(e => {
        const ed = new Date(e.eventDate);
        return ed.toDateString() === date.toDateString();
      });
      this.weekDays.push({ date, events: dayEventsForDate });
    }
  }

  buildDayView(): void {
    this.dayEvents = this.events.filter(e => {
      const ed = new Date(e.eventDate);
      return ed.toDateString() === this.selectedDate.toDateString();
    });
  }

  buildAgendaView(): void {
    const now = new Date();
    this.agendaEvents = [...this.events]
      .filter(e => new Date(e.eventDate) >= now)
      .sort((a, b) => new Date(a.eventDate).getTime() - new Date(b.eventDate).getTime());
  }

  buildCalendar(): void {
    const firstDay = new Date(this.currentYear, this.currentMonth, 1);
    const lastDay = new Date(this.currentYear, this.currentMonth + 1, 0);
    const today = new Date();

    const days: CalendarDay[] = [];

    // Fill leading days from previous month
    const startDow = firstDay.getDay();
    for (let i = startDow - 1; i >= 0; i--) {
      const date = new Date(this.currentYear, this.currentMonth, -i);
      days.push({ date, isCurrentMonth: false, isToday: false, events: [] });
    }

    // Fill current month days
    for (let d = 1; d <= lastDay.getDate(); d++) {
      const date = new Date(this.currentYear, this.currentMonth, d);
      const isToday = date.toDateString() === today.toDateString();
      const dayEvents = this.events.filter(e => {
        const ed = new Date(e.eventDate);
        return ed.getFullYear() === this.currentYear &&
               ed.getMonth() === this.currentMonth &&
               ed.getDate() === d;
      });
      days.push({ date, isCurrentMonth: true, isToday, events: dayEvents });
    }

    // Fill trailing days to complete 6 rows
    const remaining = 42 - days.length;
    for (let i = 1; i <= remaining; i++) {
      const date = new Date(this.currentYear, this.currentMonth + 1, i);
      days.push({ date, isCurrentMonth: false, isToday: false, events: [] });
    }

    this.calendarDays = days;
  }

  prevMonth(): void {
    if (this.viewMode === 'week') {
      this.selectedDate = new Date(this.selectedDate);
      this.selectedDate.setDate(this.selectedDate.getDate() - 7);
      this.buildWeekView();
    } else if (this.viewMode === 'day') {
      this.selectedDate = new Date(this.selectedDate);
      this.selectedDate.setDate(this.selectedDate.getDate() - 1);
      this.buildDayView();
    } else {
      if (this.currentMonth === 0) {
        this.currentMonth = 11;
        this.currentYear--;
      } else {
        this.currentMonth--;
      }
      this.selectedDate = new Date(this.currentYear, this.currentMonth, 1);
      this.selectedDay = null;
      if (this.viewMode === 'agenda') this.buildAgendaView();
      else this.buildCalendar();
    }
  }

  nextMonth(): void {
    if (this.viewMode === 'week') {
      this.selectedDate = new Date(this.selectedDate);
      this.selectedDate.setDate(this.selectedDate.getDate() + 7);
      this.buildWeekView();
    } else if (this.viewMode === 'day') {
      this.selectedDate = new Date(this.selectedDate);
      this.selectedDate.setDate(this.selectedDate.getDate() + 1);
      this.buildDayView();
    } else {
      if (this.currentMonth === 11) {
        this.currentMonth = 0;
        this.currentYear++;
      } else {
        this.currentMonth++;
      }
      this.selectedDate = new Date(this.currentYear, this.currentMonth, 1);
      this.selectedDay = null;
      if (this.viewMode === 'agenda') this.buildAgendaView();
      else this.buildCalendar();
    }
  }

  selectDay(day: CalendarDay): void {
    this.selectedDay = day;
    this.selectedDate = day.date;
    this.showCreateForm = false;
    this.createError = '';
  }

  openCreateForDay(day: CalendarDay): void {
    this.selectedDay = day;
    this.showCreateForm = true;
    this.createError = '';
    const d = day.date;
    const pad = (n: number) => n.toString().padStart(2, '0');
    this.newEvent = {
      name: '',
      description: '',
      eventDate: `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T08:00`,
      location: '',
      distanceKm: 0,
      price: 0
    };
  }

  createEvent(): void {
    if (!this.newEvent.name.trim() || !this.newEvent.location.trim()) {
      this.createError = 'Name and location are required';
      return;
    }
    this.creating = true;
    this.createError = '';

    this.communityService.createCommunityEvent(this.communityId, this.newEvent).subscribe({
      next: (event) => {
        this.events.push(event);
        this.buildCalendar();
        this.showCreateForm = false;
        this.creating = false;
        this.eventCreated.emit(event);
        // Refresh selected day events
        if (this.selectedDay) {
          const ed = new Date(event.eventDate);
          if (ed.toDateString() === this.selectedDay.date.toDateString()) {
            this.selectedDay.events = [...this.selectedDay.events, event];
          }
        }
      },
      error: (err) => {
        this.createError = err.error?.message || 'Failed to create event';
        this.creating = false;
      }
    });
  }

  formatTime(dateStr: string): string {
    return new Date(dateStr).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  }

  formatDate(date: Date): string {
    return date.toLocaleDateString([], { weekday: 'long', month: 'long', day: 'numeric' });
  }

  getViewLabel(): string {
    if (this.viewMode === 'month') return `${this.MONTH_NAMES[this.currentMonth]} ${this.currentYear}`;
    if (this.viewMode === 'week') {
      if (this.weekDays.length === 0) return '';
      const start = this.weekDays[0].date;
      const end = this.weekDays[6].date;
      return `${start.toLocaleDateString([], { month: 'short', day: 'numeric' })} – ${end.toLocaleDateString([], { month: 'short', day: 'numeric' })}`;
    }
    if (this.viewMode === 'day') return this.selectedDate.toLocaleDateString([], { weekday: 'long', month: 'long', day: 'numeric', year: 'numeric' });
    return 'Upcoming Events';
  }

  isSelectedDate(date: Date): boolean {
    return date.toDateString() === this.selectedDate.toDateString();
  }

  isToday(date: Date): boolean {
    return date.toDateString() === new Date().toDateString();
  }
}
