import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CommunityGoal {
  id: number;
  title: string;
  targetKm: number;
  progressKm: number;
  progressPercent: number;
  startDate: string;
  endDate: string;
  active: boolean;
  completed: boolean;
}

export interface CreateGoalRequest {
  title: string;
  targetKm: number;
  startDate: string;
  endDate: string;
}

@Injectable({ providedIn: 'root' })
export class CommunityGoalService {
  constructor(private http: HttpClient) {}

  getGoal(communityId: number): Observable<CommunityGoal> {
    return this.http.get<CommunityGoal>(`/api/communities/${communityId}/goal`);
  }

  setGoal(communityId: number, req: CreateGoalRequest): Observable<CommunityGoal> {
    return this.http.post<CommunityGoal>(`/api/communities/${communityId}/goal`, req);
  }
}
