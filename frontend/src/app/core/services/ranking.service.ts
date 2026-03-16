import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Ranking } from '../models/ranking.model';

@Injectable({ providedIn: 'root' })
export class RankingService {
  constructor(private http: HttpClient) {}

  getGlobal(type: 'weekly' | 'monthly' | 'alltime' = 'alltime'): Observable<Ranking[]> {
    const params = new HttpParams().set('type', type);
    return this.http.get<Ranking[]>('/api/rankings/global', { params });
  }

  getCommunityRanking(communityId: number): Observable<Ranking[]> {
    return this.http.get<Ranking[]>(`/api/rankings/community/${communityId}`);
  }
}
