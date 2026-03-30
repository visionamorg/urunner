export interface WorkoutStep {
  order: number;
  stepType: 'WARMUP' | 'INTERVAL' | 'RECOVERY' | 'REST' | 'COOLDOWN' | 'REPEAT';
  durationUnit: 'TIME' | 'DISTANCE' | 'OPEN' | 'LAP_BUTTON';
  durationValue?: number; // ms for TIME, meters for DISTANCE
  targetType: 'NO_TARGET' | 'PACE' | 'HEART_RATE' | 'CADENCE' | 'POWER' | 'SPEED';
  targetLow?: number;  // PACE: sec/km; HR: bpm; CADENCE: rpm
  targetHigh?: number;
  notes?: string;
  repeatCount?: number;
  children?: WorkoutStep[];
}

export interface GarminWorkout {
  id?: number;
  title: string;
  sport: string;
  description?: string;
  steps: WorkoutStep[];
  template: boolean;
  stepCount?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface AthleteWithGarmin {
  athleteId: number;
  username: string;
  profileImageUrl: string;
  garminConnected: boolean;
}

export interface WorkoutPushResult {
  workoutId: number;
  workoutTitle: string;
  results: AthleteResult[];
}

export interface AthleteResult {
  athleteId: number;
  username: string;
  success: boolean;
  garminWorkoutId?: string;
  error?: string;
}
