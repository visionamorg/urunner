import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { NotificationService } from '../../core/services/notification.service';
import { CommunityService } from '../../core/services/community.service';
import { AppNotification } from '../../core/models/notification.model';
import { InviteDto } from '../../core/models/community.model';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="max-w-3xl mx-auto space-y-8">

      <!-- Pending Invites -->
      <section>
        <h2 class="text-xl font-bold text-foreground mb-4">Pending Invites</h2>
        @if (invitesLoading) {
          <div class="space-y-3">
            @for (_ of [1,2]; track _) {
              <div class="bg-card rounded-xl p-4 border border-border animate-pulse">
                <div class="h-4 bg-muted rounded w-2/3 mb-2"></div>
                <div class="h-3 bg-muted rounded w-1/3"></div>
              </div>
            }
          </div>
        } @else if (invites.length === 0) {
          <div class="bg-card rounded-xl p-6 border border-border text-center text-muted-foreground">
            No pending invites
          </div>
        } @else {
          <div class="space-y-3">
            @for (invite of invites; track invite.id) {
              <div class="bg-card rounded-xl p-4 border border-border flex items-center justify-between gap-4">
                <div class="flex-1 min-w-0">
                  <p class="text-foreground font-semibold">{{ invite.communityName }}</p>
                  <p class="text-sm text-muted-foreground">
                    Invited by <span class="text-foreground">{{ invite.invitedByUsername }}</span>
                    &middot; {{ invite.createdAt | date:'mediumDate' }}
                  </p>
                  <p class="text-xs text-muted-foreground mt-1">
                    Expires {{ invite.expiresAt | date:'mediumDate' }}
                  </p>
                </div>
                <div class="flex gap-2 flex-shrink-0">
                  <button (click)="acceptInvite(invite)"
                    [disabled]="invite.token === respondingToken"
                    class="px-4 py-2 bg-primary text-white rounded-lg text-sm font-medium hover:bg-primary/90 disabled:opacity-50 transition-colors">
                    Accept
                  </button>
                  <button (click)="declineInvite(invite)"
                    [disabled]="invite.token === respondingToken"
                    class="px-4 py-2 bg-card text-muted-foreground border border-border rounded-lg text-sm font-medium hover:text-foreground hover:border-foreground/30 disabled:opacity-50 transition-colors">
                    Decline
                  </button>
                </div>
              </div>
            }
          </div>
        }
      </section>

      <!-- All Notifications -->
      <section>
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-xl font-bold text-foreground">Notifications</h2>
          @if (notifications.length > 0) {
            <button (click)="markAllRead()"
              class="text-sm text-primary hover:underline">
              Mark all read
            </button>
          }
        </div>
        @if (notificationsLoading) {
          <div class="space-y-3">
            @for (_ of [1,2,3]; track _) {
              <div class="bg-card rounded-xl p-4 border border-border animate-pulse">
                <div class="h-4 bg-muted rounded w-1/2 mb-2"></div>
                <div class="h-3 bg-muted rounded w-3/4"></div>
              </div>
            }
          </div>
        } @else if (notifications.length === 0) {
          <div class="bg-card rounded-xl p-6 border border-border text-center text-muted-foreground">
            No notifications yet
          </div>
        } @else {
          <div class="space-y-2">
            @for (n of notifications; track n.id) {
              <div [class]="'bg-card rounded-xl p-4 border transition-colors cursor-pointer ' +
                (n.isRead ? 'border-border' : 'border-primary/30 bg-primary/5')"
                (click)="onNotificationClick(n)">
                <div class="flex items-start gap-3">
                  <span class="material-icons text-xl mt-0.5" [class]="getTypeColor(n.type)">
                    {{ getTypeIcon(n.type) }}
                  </span>
                  <div class="flex-1 min-w-0">
                    <p class="text-foreground font-medium text-sm">{{ n.title }}</p>
                    <p class="text-muted-foreground text-sm mt-0.5">{{ n.message }}</p>
                    <p class="text-xs text-muted-foreground mt-1">{{ n.createdAt | date:'medium' }}</p>
                  </div>
                  @if (!n.isRead) {
                    <div class="w-2.5 h-2.5 rounded-full bg-primary flex-shrink-0 mt-1.5"></div>
                  }
                </div>
              </div>
            }
          </div>
        }
      </section>
    </div>
  `
})
export class NotificationsComponent implements OnInit {
  notifications: AppNotification[] = [];
  invites: InviteDto[] = [];
  notificationsLoading = true;
  invitesLoading = true;
  respondingToken = '';

  constructor(
    private notificationService: NotificationService,
    private communityService: CommunityService,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.notificationService.getAll().subscribe({
      next: (n) => { this.notifications = n; this.notificationsLoading = false; },
      error: () => { this.notificationsLoading = false; }
    });
    this.communityService.getMyInvites().subscribe({
      next: (i) => { this.invites = i; this.invitesLoading = false; },
      error: () => { this.invitesLoading = false; }
    });
  }

  acceptInvite(invite: InviteDto): void {
    this.respondingToken = invite.token;
    this.communityService.acceptInvite(invite.token).subscribe({
      next: () => {
        this.invites = this.invites.filter(i => i.id !== invite.id);
        this.respondingToken = '';
        this.toast.success('Joined ' + invite.communityName);
        this.notificationService.refreshUnreadCount();
      },
      error: () => { this.respondingToken = ''; this.toast.error('Failed to accept invite'); }
    });
  }

  declineInvite(invite: InviteDto): void {
    this.respondingToken = invite.token;
    this.communityService.declineInvite(invite.token).subscribe({
      next: () => {
        this.invites = this.invites.filter(i => i.id !== invite.id);
        this.respondingToken = '';
        this.toast.info('Invite declined');
      },
      error: () => { this.respondingToken = ''; this.toast.error('Failed to decline invite'); }
    });
  }

  onNotificationClick(n: AppNotification): void {
    if (!n.isRead) {
      n.isRead = true;
      this.notificationService.markRead(n.id).subscribe({
        next: () => this.notificationService.refreshUnreadCount()
      });
    }
  }

  markAllRead(): void {
    this.notificationService.markAllRead().subscribe({
      next: () => {
        this.notifications.forEach(n => n.isRead = true);
        this.notificationService.refreshUnreadCount();
      }
    });
  }

  getTypeIcon(type: string): string {
    switch (type) {
      case 'INVITE': return 'group_add';
      case 'BADGE': return 'emoji_events';
      case 'EVENT': return 'event';
      default: return 'notifications';
    }
  }

  getTypeColor(type: string): string {
    switch (type) {
      case 'INVITE': return 'text-blue-400';
      case 'BADGE': return 'text-yellow-400';
      case 'EVENT': return 'text-green-400';
      default: return 'text-muted-foreground';
    }
  }
}
