import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Comment, PageResponse, Post } from '../models/post.model';

@Injectable({ providedIn: 'root' })
export class FeedService {
  constructor(private http: HttpClient) {}

  getPosts(page: number = 0, size: number = 20): Observable<PageResponse<Post>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<Post>>('/api/feed/posts', { params });
  }

  createPost(data: { content: string; communityId?: number; postType?: string; photoUrls?: string[] }): Observable<Post> {
    return this.http.post<Post>('/api/feed/posts', data);
  }

  toggleLike(postId: number): Observable<Post> {
    return this.http.post<Post>(`/api/feed/posts/${postId}/like`, {});
  }

  likePost(id: number): Observable<Post> {
    return this.toggleLike(id);
  }

  getComments(postId: number): Observable<Comment[]> {
    return this.http.get<Comment[]>(`/api/feed/posts/${postId}/comments`);
  }

  addComment(postId: number, content: string): Observable<Comment> {
    return this.http.post<Comment>(`/api/feed/posts/${postId}/comments`, { content });
  }
}
