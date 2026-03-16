export interface Activity {
  id: number;
  userId: number;
  username: string;
  title: string;
  distanceKm: number;
  durationMinutes: number;
  paceMinPerKm: number;
  activityDate: string;
  location?: string;
  notes?: string;
  createdAt: string;
}

export interface ActivityStats {
  totalDistanceKm: number;
  totalRuns: number;
  totalDurationMinutes: number;
  avgPaceMinPerKm: number;
  weeklyDistanceKm: number;
  monthlyDistanceKm: number;
}

export interface CreateActivityRequest {
  title: string;
  distanceKm: number;
  durationMinutes: number;
  activityDate: string;
  location?: string;
  notes?: string;
}
