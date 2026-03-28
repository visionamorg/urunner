import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Activity, ActivityStats, CreateActivityRequest, ActivityInsight, ActivityChatRequest, ActivityChatResponse, ReadinessScore, PerformanceData } from '../models/activity.model';

export interface Streak {
  currentStreak: number;
  longestStreak: number;
  activeToday: boolean;
  totalActiveDays: number;
}

export interface SyncResult {
  imported: number;
  skipped: number;
  message: string;
}

@Injectable({ providedIn: 'root' })
export class ActivityService {
  constructor(private http: HttpClient) {}

  getAll(): Observable<Activity[]> {
    return this.http.get<Activity[]>('/api/activities');
  }

  getMyActivities(): Observable<Activity[]> {
    return this.http.get<Activity[]>('/api/activities/user');
  }

  createActivity(data: CreateActivityRequest): Observable<Activity> {
    return this.http.post<Activity>('/api/activities', data);
  }

  getMyStats(): Observable<ActivityStats> {
    return this.http.get<ActivityStats>('/api/activities/stats');
  }

  getMyStreak(): Observable<Streak> {
    return this.http.get<Streak>('/api/activities/streak');
  }

  syncStrava(): Observable<SyncResult> {
    return this.http.post<SyncResult>('/api/sync/strava', {});
  }

  syncGarmin(): Observable<SyncResult> {
    return this.http.post<SyncResult>('/api/sync/garmin', {});
  }

  getActivityById(id: number): Observable<Activity> {
    return this.http.get<Activity>(`/api/activities/${id}`);
  }

  analyzeActivity(id: number): Observable<ActivityInsight> {
    return this.http.post<ActivityInsight>(`/api/activities/${id}/analyze`, {});
  }

  getActivityInsight(id: number): Observable<ActivityInsight> {
    return this.http.get<ActivityInsight>(`/api/activities/${id}/insight`);
  }

  chatAboutActivity(id: number, request: ActivityChatRequest): Observable<ActivityChatResponse> {
    return this.http.post<ActivityChatResponse>(`/api/activities/${id}/chat`, request);
  }

  getReadiness(): Observable<ReadinessScore> {
    return this.http.get<ReadinessScore>('/api/readiness');
  }

  getPerformance(): Observable<PerformanceData> {
    return this.http.get<PerformanceData>('/api/performance');
  }
}
