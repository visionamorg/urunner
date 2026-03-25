import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RunEvent, CreateEventRequest, GalleryPhoto } from '../models/event.model';

@Injectable({ providedIn: 'root' })
export class EventService {
  constructor(private http: HttpClient) {}

  getAll(): Observable<RunEvent[]> {
    return this.http.get<RunEvent[]>('/api/events');
  }

  getById(id: number): Observable<RunEvent> {
    return this.http.get<RunEvent>(`/api/events/${id}`);
  }

  create(data: CreateEventRequest): Observable<RunEvent> {
    return this.http.post<RunEvent>('/api/events', data);
  }

  register(id: number): Observable<void> {
    return this.http.post<void>(`/api/events/${id}/register`, {});
  }

  getParticipants(id: number): Observable<any[]> {
    return this.http.get<any[]>(`/api/events/${id}/participants`);
  }

  // Gallery
  getGallery(eventId: number): Observable<GalleryPhoto[]> {
    return this.http.get<GalleryPhoto[]>(`/api/events/${eventId}/gallery`);
  }

  linkDriveFolder(eventId: number, folderId: string): Observable<void> {
    return this.http.post<void>(`/api/events/${eventId}/gallery/link-drive`, { folderId });
  }

  syncGallery(eventId: number): Observable<{ imported: number }> {
    return this.http.post<{ imported: number }>(`/api/events/${eventId}/gallery/sync`, {});
  }

  addGalleryPhoto(eventId: number, photoUrl: string): Observable<GalleryPhoto> {
    return this.http.post<GalleryPhoto>(`/api/events/${eventId}/gallery`, { photoUrl });
  }
}
