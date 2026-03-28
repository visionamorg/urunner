import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Program, ProgramProgress, ProgramSession, GeneratePlanRequest } from '../models/program.model';

@Injectable({ providedIn: 'root' })
export class ProgramService {
  constructor(private http: HttpClient) {}

  getAll(): Observable<Program[]> {
    return this.http.get<Program[]>('/api/programs');
  }

  getById(id: number): Observable<Program> {
    return this.http.get<Program>(`/api/programs/${id}`);
  }

  getSessions(id: number): Observable<ProgramSession[]> {
    return this.http.get<ProgramSession[]>(`/api/programs/${id}/sessions`);
  }

  startProgram(id: number): Observable<ProgramProgress> {
    return this.http.post<ProgramProgress>(`/api/programs/${id}/start`, {});
  }

  getMyProgress(): Observable<ProgramProgress[]> {
    return this.http.get<ProgramProgress[]>('/api/programs/my-progress');
  }

  generatePlan(request: GeneratePlanRequest): Observable<Program> {
    return this.http.post<Program>('/api/programs/generate', request);
  }

  getTodaySession(programId: number): Observable<ProgramSession> {
    return this.http.get<ProgramSession>(`/api/programs/${programId}/today-session`);
  }

  completeSession(programId: number): Observable<any> {
    return this.http.post(`/api/programs/${programId}/complete-session`, {});
  }
}
