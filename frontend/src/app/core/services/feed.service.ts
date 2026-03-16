import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Comment, PageResponse, Post } from '../models/post.model';

@Injectable({ providedIn: 'root' })
export class FeedService {
  constructor(private http: HttpClient) {}

  getPosts(page = 0, size = 20): Observable<PageResponse<Post>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<Post>>('/api/posts', { params });
  }

  createPost(data: { content: string; communityId?: number }): Observable<Post> {
    return this.http.post<Post>('/api/posts', data);
  }

  likePost(id: number): Observable<Post> {
    return this.http.post<Post>(`/api/posts/${id}/like`, {});
  }

  getComments(postId: number): Observable<Comment[]> {
    return this.http.get<Comment[]>(`/api/posts/${postId}/comments`);
  }

  addComment(postId: number, content: string): Observable<Comment> {
    return this.http.post<Comment>(`/api/posts/${postId}/comments`, { content });
  }
}
