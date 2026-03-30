import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GarminWorkoutService } from '../../core/services/garmin-workout.service';
import {
  GarminWorkout, WorkoutStep,
  AthleteWithGarmin, AthleteResult, WorkoutPushResult
} from '../../core/models/garmin-workout.model';

@Component({
  selector: 'app-garmin-clipboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './garmin-clipboard.component.html'
})
export class GarminClipboardComponent implements OnInit {

  workouts: GarminWorkout[] = [];
  loading = true;

  // Builder state
  builderOpen = false;
  editingId: number | null = null;
  builderTitle = '';
  builderSport = 'RUNNING';
  builderDescription = '';
  builderTemplate = false;
  builderSteps: WorkoutStep[] = [];
  saving = false;

  // Push-self state
  pushSelfWorkout: GarminWorkout | null = null;
  pushSelfDate = '';
  pushingSelf = false;
  pushSelfResult: any = null;
  pushSelfError = '';

  // Push-athletes dialog
  pushAthletesWorkout: GarminWorkout | null = null;
  pushAthletesDate = '';
  athletes: AthleteWithGarmin[] = [];
  selectedAthleteIds: Set<number> = new Set();
  pushingAthletes = false;
  pushAthletesResult: WorkoutPushResult | null = null;
  loadingAthletes = false;

  readonly sports = ['RUNNING', 'CYCLING', 'SWIMMING', 'OTHER'];
  readonly stepTypes = ['WARMUP', 'INTERVAL', 'RECOVERY', 'REST', 'COOLDOWN', 'REPEAT'];
  readonly durationUnits = ['TIME', 'DISTANCE', 'OPEN', 'LAP_BUTTON'];
  readonly targetTypes = ['NO_TARGET', 'PACE', 'HEART_RATE', 'CADENCE'];

  constructor(private garminWorkoutService: GarminWorkoutService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.garminWorkoutService.list().subscribe({
      next: ws => { this.workouts = ws; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  // ── Builder ─────────────────────────────────────────────────────────────

  openNewBuilder(): void {
    this.editingId = null;
    this.builderTitle = 'My Workout';
    this.builderSport = 'RUNNING';
    this.builderDescription = '';
    this.builderTemplate = false;
    this.builderSteps = [this.newStep('WARMUP'), this.newStep('INTERVAL'), this.newStep('COOLDOWN')];
    this.builderOpen = true;
  }

  openEditBuilder(w: GarminWorkout): void {
    this.editingId = w.id ?? null;
    this.builderTitle = w.title;
    this.builderSport = w.sport;
    this.builderDescription = w.description ?? '';
    this.builderTemplate = w.template;
    this.builderSteps = JSON.parse(JSON.stringify(w.steps)); // deep copy
    this.builderOpen = true;
  }

  closeBuilder(): void {
    this.builderOpen = false;
  }

  newStep(type: WorkoutStep['stepType'] = 'INTERVAL'): WorkoutStep {
    const order = this.builderSteps.length + 1;
    const base: WorkoutStep = {
      order,
      stepType: type,
      durationUnit: type === 'REPEAT' ? 'OPEN' : 'TIME',
      durationValue: type === 'REPEAT' ? undefined : 300000, // 5 min default
      targetType: 'NO_TARGET',
      notes: ''
    };
    if (type === 'REPEAT') {
      base.repeatCount = 3;
      base.children = [this.newChildStep('INTERVAL'), this.newChildStep('RECOVERY')];
    }
    return base;
  }

  newChildStep(type: WorkoutStep['stepType'] = 'INTERVAL'): WorkoutStep {
    return {
      order: 1,
      stepType: type,
      durationUnit: 'TIME',
      durationValue: 180000, // 3 min
      targetType: 'NO_TARGET',
      notes: ''
    };
  }

  addStep(): void {
    this.builderSteps.push(this.newStep('INTERVAL'));
    this.reorderSteps(this.builderSteps);
  }

  addRepeatGroup(): void {
    this.builderSteps.push(this.newStep('REPEAT'));
    this.reorderSteps(this.builderSteps);
  }

  removeStep(index: number): void {
    this.builderSteps.splice(index, 1);
    this.reorderSteps(this.builderSteps);
  }

  addChildStep(step: WorkoutStep): void {
    if (!step.children) step.children = [];
    step.children.push(this.newChildStep('INTERVAL'));
  }

  removeChildStep(step: WorkoutStep, ci: number): void {
    step.children?.splice(ci, 1);
  }

  moveStepUp(index: number): void {
    if (index === 0) return;
    [this.builderSteps[index - 1], this.builderSteps[index]] =
      [this.builderSteps[index], this.builderSteps[index - 1]];
    this.reorderSteps(this.builderSteps);
  }

  moveStepDown(index: number): void {
    if (index >= this.builderSteps.length - 1) return;
    [this.builderSteps[index], this.builderSteps[index + 1]] =
      [this.builderSteps[index + 1], this.builderSteps[index]];
    this.reorderSteps(this.builderSteps);
  }

  private reorderSteps(steps: WorkoutStep[]): void {
    steps.forEach((s, i) => s.order = i + 1);
  }

  saveWorkout(): void {
    if (!this.builderTitle.trim()) return;
    this.saving = true;
    const payload: Partial<GarminWorkout> = {
      title: this.builderTitle,
      sport: this.builderSport,
      description: this.builderDescription,
      steps: this.builderSteps,
      template: this.builderTemplate
    };
    const obs = this.editingId
      ? this.garminWorkoutService.update(this.editingId, payload)
      : this.garminWorkoutService.create(payload);

    obs.subscribe({
      next: () => { this.saving = false; this.builderOpen = false; this.load(); },
      error: () => { this.saving = false; }
    });
  }

  deleteWorkout(w: GarminWorkout): void {
    if (!w.id || !confirm(`Delete "${w.title}"?`)) return;
    this.garminWorkoutService.delete(w.id).subscribe(() => this.load());
  }

  // ── Push to self ─────────────────────────────────────────────────────────

  openPushSelf(w: GarminWorkout): void {
    this.pushSelfWorkout = w;
    this.pushSelfDate = new Date().toISOString().split('T')[0];
    this.pushSelfResult = null;
    this.pushSelfError = '';
  }

  closePushSelf(): void {
    this.pushSelfWorkout = null;
  }

  confirmPushSelf(): void {
    if (!this.pushSelfWorkout?.id) return;
    this.pushingSelf = true;
    this.pushSelfResult = null;
    this.pushSelfError = '';
    this.garminWorkoutService.pushToSelf(this.pushSelfWorkout.id, this.pushSelfDate).subscribe({
      next: r => { this.pushingSelf = false; this.pushSelfResult = r; },
      error: err => {
        this.pushingSelf = false;
        this.pushSelfError = err?.error?.message || err?.error || 'Push failed';
      }
    });
  }

  // ── Push to athletes ──────────────────────────────────────────────────────

  openPushAthletes(w: GarminWorkout): void {
    this.pushAthletesWorkout = w;
    this.pushAthletesDate = new Date().toISOString().split('T')[0];
    this.selectedAthleteIds = new Set();
    this.pushAthletesResult = null;
    this.loadingAthletes = true;
    this.garminWorkoutService.getAthletes().subscribe({
      next: a => {
        this.athletes = a;
        // Pre-select Garmin-connected athletes
        a.filter(x => x.garminConnected).forEach(x => this.selectedAthleteIds.add(x.athleteId));
        this.loadingAthletes = false;
      },
      error: () => { this.loadingAthletes = false; }
    });
  }

  closePushAthletes(): void {
    this.pushAthletesWorkout = null;
  }

  toggleAthlete(id: number): void {
    if (this.selectedAthleteIds.has(id)) this.selectedAthleteIds.delete(id);
    else this.selectedAthleteIds.add(id);
  }

  selectAllAthletes(): void {
    this.athletes.filter(a => a.garminConnected).forEach(a => this.selectedAthleteIds.add(a.athleteId));
  }

  confirmPushAthletes(): void {
    if (!this.pushAthletesWorkout?.id || this.selectedAthleteIds.size === 0) return;
    this.pushingAthletes = true;
    this.pushAthletesResult = null;
    this.garminWorkoutService.pushToAthletes(
      this.pushAthletesWorkout.id,
      Array.from(this.selectedAthleteIds),
      this.pushAthletesDate
    ).subscribe({
      next: r => { this.pushingAthletes = false; this.pushAthletesResult = r; },
      error: () => { this.pushingAthletes = false; }
    });
  }

  athleteResult(athleteId: number): AthleteResult | undefined {
    return this.pushAthletesResult?.results.find(r => r.athleteId === athleteId);
  }

  // ── Estimates ─────────────────────────────────────────────────────────────

  estimatedDistance(steps: WorkoutStep[]): string {
    const meters = this.calcMeters(steps);
    return meters > 0 ? (meters / 1000).toFixed(1) + ' km' : '';
  }

  estimatedTime(steps: WorkoutStep[]): string {
    const ms = this.calcMs(steps);
    if (ms <= 0) return '';
    const min = Math.round(ms / 60000);
    return min >= 60 ? `${Math.floor(min / 60)}h ${min % 60}m` : `${min} min`;
  }

  private calcMeters(steps: WorkoutStep[]): number {
    return steps.reduce((acc, s) => {
      if (s.stepType === 'REPEAT' && s.children) {
        return acc + (s.repeatCount ?? 1) * this.calcMeters(s.children);
      }
      if (s.durationUnit === 'DISTANCE' && s.durationValue) return acc + s.durationValue;
      return acc;
    }, 0);
  }

  private calcMs(steps: WorkoutStep[]): number {
    return steps.reduce((acc, s) => {
      if (s.stepType === 'REPEAT' && s.children) {
        return acc + (s.repeatCount ?? 1) * this.calcMs(s.children);
      }
      if (s.durationUnit === 'TIME' && s.durationValue) return acc + s.durationValue;
      return acc;
    }, 0);
  }

  // ── Formatting helpers ────────────────────────────────────────────────────

  formatDuration(step: WorkoutStep): string {
    if (step.durationUnit === 'OPEN') return 'Open';
    if (step.durationUnit === 'LAP_BUTTON') return 'Lap button';
    if (step.durationUnit === 'DISTANCE' && step.durationValue)
      return (step.durationValue / 1000).toFixed(2) + ' km';
    if (step.durationUnit === 'TIME' && step.durationValue) {
      const sec = Math.round(step.durationValue / 1000);
      const m = Math.floor(sec / 60), s = sec % 60;
      return `${m}:${s.toString().padStart(2, '0')}`;
    }
    return '—';
  }

  formatTarget(step: WorkoutStep): string {
    if (!step.targetType || step.targetType === 'NO_TARGET') return 'Open';
    const lo = step.targetLow, hi = step.targetHigh;
    if (step.targetType === 'PACE' && lo) {
      const fmt = (v: number) => `${Math.floor(v / 60)}:${(v % 60).toString().padStart(2, '0')}`;
      return hi ? `${fmt(lo)}–${fmt(hi)} /km` : `${fmt(lo)} /km`;
    }
    if (step.targetType === 'HEART_RATE' && lo)
      return hi ? `${lo}–${hi} bpm` : `${lo} bpm`;
    if (step.targetType === 'CADENCE' && lo)
      return hi ? `${lo}–${hi} rpm` : `${lo} rpm`;
    return step.targetType;
  }

  sportIcon(sport: string): string {
    const icons: Record<string, string> = {
      RUNNING: 'directions_run', CYCLING: 'directions_bike',
      SWIMMING: 'pool', OTHER: 'fitness_center'
    };
    return icons[sport] ?? 'fitness_center';
  }

  stepColor(type: string): string {
    const colors: Record<string, string> = {
      WARMUP: 'text-yellow-400', INTERVAL: 'text-blue-400',
      RECOVERY: 'text-green-400', REST: 'text-gray-400',
      COOLDOWN: 'text-purple-400', REPEAT: 'text-orange-400'
    };
    return colors[type] ?? 'text-white';
  }
}
