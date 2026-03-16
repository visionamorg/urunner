import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Message, SendMessageRequest } from '../models/message.model';

@Injectable({ providedIn: 'root' })
export class ChatService {
  constructor(private http: HttpClient) {}

  getMessages(communityId?: number, eventId?: number, roomId?: number): Observable<Message[]> {
    let params = new HttpParams();
    if (communityId) params = params.set('communityId', communityId);
    if (eventId) params = params.set('eventId', eventId);
    if (roomId) params = params.set('roomId', roomId);
    return this.http.get<Message[]>('/api/messages', { params });
  }

  sendMessage(data: SendMessageRequest): Observable<Message> {
    return this.http.post<Message>('/api/messages', data);
  }
}
