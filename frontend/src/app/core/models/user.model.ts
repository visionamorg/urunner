export type AuthProvider = 'LOCAL' | 'STRAVA' | 'GARMIN';

export interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  bio?: string;
  profileImageUrl?: string;
  role: 'USER' | 'ADMIN' | 'ORGANIZER';
  authProvider: AuthProvider;
  createdAt: string;
}

export interface AuthResponse {
  token: string;
  username: string;
  email: string;
  role: string;
  userId: number;
  provider: AuthProvider;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}
