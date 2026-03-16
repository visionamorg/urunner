import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Badge, UserBadge } from '../models/badge.model';

@Injectable({ providedIn: 'root' })
export class BadgeService {
  constructor(private http: HttpClient) {}

  getAll(): Observable<Badge[]> {
    return this.http.get<Badge[]>('/api/badges');
  }

  getMyBadges(): Observable<UserBadge[]> {
    return this.http.get<UserBadge[]>('/api/badges/my');
  }

  getUserBadges(userId: number): Observable<UserBadge[]> {
    return this.http.get<UserBadge[]>(`/api/badges/user/${userId}`);
  }
}
