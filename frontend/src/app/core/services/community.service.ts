import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Community, CommunityMember, CreateCommunityRequest } from '../models/community.model';

@Injectable({ providedIn: 'root' })
export class CommunityService {
  constructor(private http: HttpClient) {}

  getAll(): Observable<Community[]> {
    return this.http.get<Community[]>('/api/communities');
  }

  getById(id: number): Observable<Community> {
    return this.http.get<Community>(`/api/communities/${id}`);
  }

  create(data: CreateCommunityRequest): Observable<Community> {
    return this.http.post<Community>('/api/communities', data);
  }

  join(id: number): Observable<void> {
    return this.http.post<void>(`/api/communities/${id}/join`, {});
  }

  getMembers(id: number): Observable<CommunityMember[]> {
    return this.http.get<CommunityMember[]>(`/api/communities/${id}/members`);
  }
}
