import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { AppNotification } from '../models/notification.model';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private unreadCount$ = new BehaviorSubject<number>(0);
  unreadCount = this.unreadCount$.asObservable();

  constructor(private http: HttpClient) {}

  getAll(): Observable<AppNotification[]> {
    return this.http.get<AppNotification[]>('/api/notifications');
  }

  refreshUnreadCount(): void {
    this.http.get<{ count: number }>('/api/notifications/unread-count').subscribe({
      next: (res) => this.unreadCount$.next(res.count),
      error: () => {}
    });
  }

  markRead(id: number): Observable<void> {
    return this.http.put<void>(`/api/notifications/${id}/read`, {});
  }

  markAllRead(): Observable<void> {
    return this.http.put<void>('/api/notifications/read-all', {});
  }
}
