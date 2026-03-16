import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Community, CommunityMember, CreateCommunityRequest } from '../models/community.model';
import { Post, PageResponse } from '../models/post.model';

@Injectable({ providedIn: 'root' })
export class CommunityService {
  constructor(private http: HttpClient) {}

  getAll(): Observable<Community[]> {
    return this.http.get<Community[]>('/api/communities');
  }

  getOne(id: number): Observable<Community> {
    return this.http.get<Community>(`/api/communities/${id}`);
  }

  getById(id: number): Observable<Community> {
    return this.getOne(id);
  }

  create(data: Partial<Community> | CreateCommunityRequest): Observable<Community> {
    return this.http.post<Community>('/api/communities', data);
  }

  update(id: number, data: Partial<Community>): Observable<Community> {
    return this.http.put<Community>(`/api/communities/${id}`, data);
  }

  join(id: number): Observable<any> {
    return this.http.post<any>(`/api/communities/${id}/join`, {});
  }

  leave(id: number): Observable<any> {
    return this.http.delete<any>(`/api/communities/${id}/leave`);
  }

  getMembers(id: number): Observable<CommunityMember[]> {
    return this.http.get<CommunityMember[]>(`/api/communities/${id}/members`);
  }

  getFeed(id: number, page: number = 0): Observable<PageResponse<Post>> {
    const params = new HttpParams().set('page', page);
    return this.http.get<PageResponse<Post>>(`/api/communities/${id}/feed`, { params });
  }

  createPost(id: number, data: { content: string; postType?: string; photoUrls?: string[] }): Observable<Post> {
    return this.http.post<Post>(`/api/communities/${id}/feed`, data);
  }

  syncDrive(id: number): Observable<Post> {
    return this.http.post<Post>(`/api/communities/${id}/drive/sync`, {});
  }
}
