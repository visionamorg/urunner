import { Component, Input, Output, EventEmitter, ViewChild, ElementRef, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RunEvent } from '../../../core/models/event.model';
import html2canvas from 'html2canvas';

@Component({
  selector: 'app-agenda-image-generator',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './agenda-image-generator.component.html',
  styles: [`
    @import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@700;900&family=Inter:wght@400;600;700;800&display=swap');
    .canvas-wrapper { width: 360px; height: 640px; }
    .canvas-9-16 { width: 1080px; height: 1920px; transform: scale(0.333); transform-origin: top left; }
    .font-playfair { font-family: 'Playfair Display', serif; }
    .font-inter { font-family: 'Inter', sans-serif; }
  `]
})
export class AgendaImageGeneratorComponent implements OnChanges {
  @Input() mode: 'daily' | 'weekly' = 'weekly';
  @Input() events: RunEvent[] = [];
  @Input() communityName = '';
  @Input() communityHandle = '';
  @Input() coverUrl = '';
  @Input() visible = false;
  @Output() visibleChange = new EventEmitter<boolean>();

  @ViewChild('agendaCanvas') agendaCanvas!: ElementRef;

  // Form fields
  backgroundUrl = '';
  headerText = 'AGENDA';
  subHeaderText = '';
  instagramHandle = '';
  hashtag = '';
  downloading = false;

  // Daily-specific
  selectedEvent: RunEvent | null = null;
  meetingPoint = '';
  paceGroups = '';
  dailyTags: string[] = [];
  tagInput = '';

  // Weekly-specific
  startDate = '';
  endDate = '';
  selectedEventIds: Set<number> = new Set();

  get filteredEvents(): RunEvent[] {
    if (this.mode === 'daily') return this.selectedEvent ? [this.selectedEvent] : [];
    if (!this.startDate || !this.endDate) return this.events;
    const s = new Date(this.startDate); s.setHours(0, 0, 0, 0);
    const e = new Date(this.endDate); e.setHours(23, 59, 59, 999);
    return this.events
      .filter(ev => { const d = new Date(ev.eventDate); return d >= s && d <= e; })
      .filter(ev => this.selectedEventIds.has(ev.id))
      .sort((a, b) => new Date(a.eventDate).getTime() - new Date(b.eventDate).getTime());
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['visible'] && this.visible) this.initDefaults();
    if (changes['events'] && this.events.length > 0) {
      this.selectedEventIds = new Set(this.events.map(e => e.id));
    }
  }

  private initDefaults(): void {
    this.backgroundUrl = this.coverUrl || '';
    this.instagramHandle = this.communityHandle || '@' + this.communityName.toLowerCase().replace(/\s+/g, '');
    this.hashtag = '#' + this.communityName.toUpperCase().replace(/[\s:]+/g, '');

    if (this.mode === 'daily') {
      this.headerText = 'NEXT SESSION';
      this.selectedEvent = this.events.find(e => new Date(e.eventDate) > new Date()) || this.events[0] || null;
      if (this.selectedEvent) {
        this.meetingPoint = this.selectedEvent.location;
        this.subHeaderText = "TOMORROW'S RUN";
      }
    } else {
      this.headerText = 'AGENDA';
      const now = new Date();
      const monday = new Date(now);
      monday.setDate(now.getDate() + ((1 - now.getDay() + 7) % 7 || 7));
      const sunday = new Date(monday);
      sunday.setDate(monday.getDate() + 6);
      this.startDate = this.toDateStr(monday);
      this.endDate = this.toDateStr(sunday);
      this.subHeaderText = `OF THE WEEK FROM ${this.formatShort(monday)} TO ${this.formatShort(sunday)}`;
      this.selectedEventIds = new Set(this.events.map(e => e.id));
    }
  }

  onDateChange(): void {
    if (this.startDate && this.endDate) {
      const s = new Date(this.startDate);
      const e = new Date(this.endDate);
      this.subHeaderText = `OF THE WEEK FROM ${this.formatShort(s)} TO ${this.formatShort(e)}`;
    }
  }

  onEventSelect(ev: RunEvent): void {
    this.selectedEvent = ev;
    this.meetingPoint = ev.location;
  }

  toggleEvent(id: number): void {
    if (this.selectedEventIds.has(id)) this.selectedEventIds.delete(id);
    else this.selectedEventIds.add(id);
  }

  onBackgroundFileSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = () => { this.backgroundUrl = reader.result as string; };
    reader.readAsDataURL(file);
  }

  addTag(): void {
    const t = this.tagInput.trim();
    if (t && this.dailyTags.length < 4) { this.dailyTags.push(t); this.tagInput = ''; }
  }

  removeTag(i: number): void { this.dailyTags.splice(i, 1); }

  async download(): Promise<void> {
    if (!this.agendaCanvas) return;
    this.downloading = true;
    try {
      const el = this.agendaCanvas.nativeElement;
      const canvas = await html2canvas(el, {
        width: 1080, height: 1920, scale: 1,
        useCORS: true, allowTaint: true,
        backgroundColor: '#0a0a0a', logging: false,
        windowWidth: 1080, windowHeight: 1920,
        imageTimeout: 15000,
        onclone: (_doc: Document, clonedEl: HTMLElement) => {
          clonedEl.style.transform = 'none';
          clonedEl.style.transformOrigin = 'top left';
          clonedEl.style.width = '1080px';
          clonedEl.style.height = '1920px';
        },
      });
      const link = document.createElement('a');
      link.download = `${this.communityName.replace(/\s+/g, '_')}_${this.mode}_agenda.png`;
      link.href = canvas.toDataURL('image/png');
      link.click();
    } finally {
      this.downloading = false;
    }
  }

  close(): void { this.visible = false; this.visibleChange.emit(false); }

  formatDay(dateStr: string): string {
    return new Date(dateStr).toLocaleDateString('en-US', { weekday: 'short' }).toUpperCase();
  }

  formatDate(dateStr: string): string {
    return new Date(dateStr).toLocaleDateString('en-US', { month: 'short', day: 'numeric' }).toUpperCase();
  }

  formatTime(dateStr: string): string {
    return new Date(dateStr).toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', hour12: false });
  }

  formatFullDate(dateStr: string): string {
    return new Date(dateStr).toLocaleDateString('en-US', { weekday: 'long', month: 'long', day: 'numeric', year: 'numeric' });
  }

  private formatShort(d: Date): string {
    return d.toLocaleDateString('en-US', { month: 'long', day: 'numeric' }).toUpperCase();
  }

  private toDateStr(d: Date): string {
    return d.toISOString().split('T')[0];
  }
}
