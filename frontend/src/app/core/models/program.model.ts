export interface Program {
  id: number;
  name: string;
  description: string;
  level: 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED';
  durationWeeks: number;
  targetDistanceKm: number;
  price?: number;
  sessionsCount: number;
  communityId?: number;
  createdByUsername?: string;
  createdAt: string;
}

export interface ProgramSession {
  id: number;
  weekNumber: number;
  dayNumber: number;
  title: string;
  description: string;
  distanceKm: number;
  durationMinutes: number;
}

export interface ProgramProgress {
  id: number;
  programId: number;
  programName: string;
  programLevel: string;
  durationWeeks: number;
  startedAt: string;
  completedSessions: number;
  totalSessions: number;
  status: string;
}

export interface EnrolleeProgress {
  username: string;
  completedSessions: number;
  totalSessions: number;
  status: string;
}

export interface CreateProgramRequest {
  name: string;
  description: string;
  level: string;
  durationWeeks: number;
  targetDistanceKm?: number;
  sessions?: Partial<ProgramSession>[];
}

export interface GeneratePlanRequest {
  goalType: 'MARATHON' | 'HALF_MARATHON' | '10K' | '5K' | 'BASE_BUILDING';
  targetTime?: string;
  durationWeeks: number;
  daysPerWeek: number;
  currentWeeklyKm?: number;
}
