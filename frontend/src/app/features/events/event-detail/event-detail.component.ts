import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { EventService } from '../../../core/services/event.service';
import { ChatService } from '../../../core/services/chat.service';
import { AuthService } from '../../../core/services/auth.service';
import { RunEvent } from '../../../core/models/event.model';
import { Message } from '../../../core/models/message.model';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-event-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, MatSnackBarModule],
  templateUrl: './event-detail.component.html',
  styleUrl: './event-detail.component.scss'
})
export class EventDetailComponent implements OnInit {
  event: RunEvent | null = null;
  participants: any[] = [];
  messages: Message[] = [];
  chatInput = '';
  loading = true;
  sendingMessage = false;
  registering = false;
  registered = false;
  currentUsername = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private eventService: EventService,
    private chatService: ChatService,
    private authService: AuthService,
    private snackBar: MatSnackBar,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.currentUsername = this.authService.getCurrentUser()?.username || '';
    this.route.params.subscribe(params => {
      const id = +params['id'];
      this.load(id);
    });
  }

  load(id: number): void {
    this.loading = true;
    this.eventService.getById(id).subscribe({
      next: e => {
        this.event = e;
        this.loading = false;
        this.cdr.detectChanges();
        this.loadParticipants(id);
        this.loadMessages(id);
      },
      error: () => {
        this.loading = false;
        this.router.navigate(['/events']);
      }
    });
  }

  loadParticipants(id: number): void {
    this.eventService.getParticipants(id).subscribe({
      next: p => {
        this.participants = p;
        this.registered = p.some((x: any) => x.username === this.currentUsername);
        this.cdr.detectChanges();
      },
      error: () => {}
    });
  }

  loadMessages(id: number): void {
    this.chatService.getMessages(undefined, id, undefined).subscribe({
      next: msgs => { this.messages = msgs; this.cdr.detectChanges(); },
      error: () => {}
    });
  }

  register(): void {
    if (!this.event) return;
    this.registering = true;
    this.eventService.register(this.event.id).subscribe({
      next: () => {
        this.registered = true;
        this.registering = false;
        this.snackBar.open('Successfully registered!', 'Close', { duration: 3000 });
        this.loadParticipants(this.event!.id);
      },
      error: (err) => {
        this.registering = false;
        this.snackBar.open(err.error?.message || 'Registration failed', 'Close', { duration: 3000 });
      }
    });
  }

  sendMessage(): void {
    if (!this.chatInput.trim() || !this.event || this.sendingMessage) return;
    this.sendingMessage = true;
    this.chatService.sendMessage({ eventId: this.event.id, content: this.chatInput.trim() }).subscribe({
      next: msg => {
        this.messages.push(msg);
        this.chatInput = '';
        this.sendingMessage = false;
        this.cdr.detectChanges();
      },
      error: () => { this.sendingMessage = false; }
    });
  }

  onKeyDown(e: KeyboardEvent): void {
    if (e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); this.sendMessage(); }
  }

  isUpcoming(): boolean {
    return !!this.event && new Date(this.event.eventDate) > new Date();
  }

  timeAgo(dateStr: string): string {
    const d = new Date(dateStr);
    const diff = Math.floor((Date.now() - d.getTime()) / 1000);
    if (diff < 60) return 'just now';
    if (diff < 3600) return `${Math.floor(diff / 60)}m ago`;
    if (diff < 86400) return `${Math.floor(diff / 3600)}h ago`;
    return d.toLocaleDateString();
  }

  getInitials(name: string): string {
    if (!name) return '?';
    const parts = name.trim().split(' ');
    if (parts.length >= 2) return (parts[0][0] + parts[1][0]).toUpperCase();
    return name.substring(0, 2).toUpperCase();
  }

  goBack(): void {
    this.router.navigate(['/events']);
  }

  formatDate(dateStr: string): string {
    return new Date(dateStr).toLocaleDateString([], {
      weekday: 'long', year: 'numeric', month: 'long', day: 'numeric',
      hour: '2-digit', minute: '2-digit'
    });
  }
}
