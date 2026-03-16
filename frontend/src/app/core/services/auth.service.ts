import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly STORAGE_KEY = 'runhub_auth';
  private currentUserSubject = new BehaviorSubject<AuthResponse | null>(null);
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    const stored = localStorage.getItem(this.STORAGE_KEY);
    if (stored) {
      this.currentUserSubject.next(JSON.parse(stored));
    }
  }

  login(req: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('/api/auth/login', req).pipe(
      tap(res => this.setAuth(res))
    );
  }

  register(req: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('/api/auth/register', req).pipe(
      tap(res => this.setAuth(res))
    );
  }

  logout(): void {
    localStorage.removeItem(this.STORAGE_KEY);
    this.currentUserSubject.next(null);
  }

  getToken(): string | null {
    return this.currentUserSubject.value?.token ?? null;
  }

  isLoggedIn(): boolean {
    return !!this.currentUserSubject.value;
  }

  getCurrentUser(): AuthResponse | null {
    return this.currentUserSubject.value;
  }

  /** Called by OAuthCallbackComponent after provider redirect */
  handleOAuthCallback(params: {
    token: string; username: string; email: string;
    role: string; userId: string; provider: string;
  }): void {
    const res: AuthResponse = {
      token: params.token,
      username: params.username,
      email: params.email,
      role: params.role,
      userId: Number(params.userId),
      provider: params.provider as any
    };
    this.setAuth(res);
  }

  connectStrava(): void {
    window.location.href = '/api/oauth/strava/connect';
  }

  connectGarmin(): void {
    window.location.href = '/api/oauth/garmin/connect';
  }

  private setAuth(res: AuthResponse): void {
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(res));
    this.currentUserSubject.next(res);
  }
}
