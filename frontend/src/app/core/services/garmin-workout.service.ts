import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  GarminWorkout, WorkoutPushResult, AthleteWithGarmin
} from '../models/garmin-workout.model';

@Injectable({ providedIn: 'root' })
export class GarminWorkoutService {

  constructor(private http: HttpClient) {}

  list(): Observable<GarminWorkout[]> {
    return this.http.get<GarminWorkout[]>('/api/garmin/workouts');
  }

  create(workout: Partial<GarminWorkout>): Observable<GarminWorkout> {
    return this.http.post<GarminWorkout>('/api/garmin/workouts', workout);
  }

  update(id: number, workout: Partial<GarminWorkout>): Observable<GarminWorkout> {
    return this.http.put<GarminWorkout>(`/api/garmin/workouts/${id}`, workout);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`/api/garmin/workouts/${id}`);
  }

  pushToSelf(id: number, scheduledDate: string): Observable<any> {
    return this.http.post<any>(`/api/garmin/workouts/${id}/push-self`, { scheduledDate });
  }

  pushToAthletes(id: number, athleteIds: number[], scheduledDate: string): Observable<WorkoutPushResult> {
    return this.http.post<WorkoutPushResult>(`/api/garmin/workouts/${id}/push-athletes`, {
      athleteIds, scheduledDate
    });
  }

  getAthletes(): Observable<AthleteWithGarmin[]> {
    return this.http.get<AthleteWithGarmin[]>('/api/garmin/workouts/athletes');
  }
}
