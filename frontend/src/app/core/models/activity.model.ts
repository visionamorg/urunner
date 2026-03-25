export interface ActivitySplit {
  splitKm: number;
  splitPace: number;
  splitElevation: number;
  splitHeartRate: number;
}

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
  elevationGainMeters?: number;
  avgHeartRate?: number;
  maxHeartRate?: number;
  avgCadence?: number;
  mapPolyline?: string;
  splits?: ActivitySplit[];
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

export interface ActivityInsight {
  id: number;
  activityId: number;
  summaryText: string;
  intensity: string;
  nextRunSuggestion: string;
  injuryRiskNotes: string;
  socialCaption: string;
  createdAt: string;
}

export interface ActivityChatMessage {
  role: 'user' | 'assistant';
  content: string;
}

export interface ActivityChatRequest {
  message: string;
  history: ActivityChatMessage[];
}

export interface ActivityChatResponse {
  reply: string;
}
