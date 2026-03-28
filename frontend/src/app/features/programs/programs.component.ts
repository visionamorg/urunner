import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ProgramService } from '../../core/services/program.service';
import { Program, ProgramProgress, ProgramSession, GeneratePlanRequest } from '../../core/models/program.model';

@Component({
  selector: 'app-programs',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './programs.component.html',
  styleUrl: './programs.component.scss'
})
export class ProgramsComponent implements OnInit {
  Math = Math;
  programs: Program[] = [];
  myProgress: ProgramProgress[] = [];
  loading = true;

  // AI Generator
  showGenerator = false;
  generatorStep = 1; // 1=goal, 2=schedule, 3=generating
  generating = false;
  generatedProgram: Program | null = null;

  planRequest: GeneratePlanRequest = {
    goalType: 'HALF_MARATHON',
    targetTime: '',
    durationWeeks: 12,
    daysPerWeek: 4,
    currentWeeklyKm: undefined
  };

  // Plan detail view
  selectedProgram: Program | null = null;
  programSessions: ProgramSession[] = [];
  sessionsLoading = false;

  goalOptions = [
    { value: '5K', label: '5K', icon: 'sprint', weeks: 8 },
    { value: '10K', label: '10K', icon: 'directions_run', weeks: 10 },
    { value: 'HALF_MARATHON', label: 'Half Marathon', icon: 'hiking', weeks: 12 },
    { value: 'MARATHON', label: 'Marathon', icon: 'landscape', weeks: 16 },
    { value: 'BASE_BUILDING', label: 'Base Building', icon: 'fitness_center', weeks: 8 }
  ];

  constructor(
    private programService: ProgramService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.programService.getAll().subscribe({
      next: p => { this.programs = p; this.loading = false; },
      error: () => { this.loading = false; }
    });
    this.programService.getMyProgress().subscribe({
      next: p => this.myProgress = p
    });
  }

  // ── AI Generator ──────────────────────────────────────────────────

  openGenerator(): void {
    this.showGenerator = true;
    this.generatorStep = 1;
    this.generatedProgram = null;
    this.planRequest = {
      goalType: 'HALF_MARATHON',
      targetTime: '',
      durationWeeks: 12,
      daysPerWeek: 4,
      currentWeeklyKm: undefined
    };
  }

  closeGenerator(): void {
    this.showGenerator = false;
    this.generatorStep = 1;
    this.generating = false;
  }

  selectGoal(goalType: string): void {
    this.planRequest.goalType = goalType as any;
    const goal = this.goalOptions.find(g => g.value === goalType);
    if (goal) this.planRequest.durationWeeks = goal.weeks;
  }

  nextStep(): void {
    if (this.generatorStep < 2) {
      this.generatorStep++;
    } else {
      this.generatePlan();
    }
  }

  prevStep(): void {
    if (this.generatorStep > 1) this.generatorStep--;
  }

  generatePlan(): void {
    this.generatorStep = 3;
    this.generating = true;

    const req = { ...this.planRequest };
    if (!req.targetTime || req.targetTime.trim() === '') {
      delete (req as any).targetTime;
    }
    if (!req.currentWeeklyKm) {
      delete (req as any).currentWeeklyKm;
    }

    this.programService.generatePlan(req).subscribe({
      next: (program) => {
        this.generating = false;
        this.generatedProgram = program;
        this.programs.unshift(program);
        this.snackBar.open('Training plan generated!', 'Close', { duration: 3000 });
      },
      error: (err) => {
        this.generating = false;
        this.generatorStep = 2;
        this.snackBar.open(err.error?.message || 'Failed to generate plan', 'Close', { duration: 4000 });
      }
    });
  }

  enrollInGenerated(): void {
    if (!this.generatedProgram) return;
    this.startProgram(this.generatedProgram.id);
    this.closeGenerator();
  }

  // ── Plan Detail View ──────────────────────────────────────────────

  openPlanDetail(program: Program): void {
    this.selectedProgram = program;
    this.sessionsLoading = true;
    this.programService.getSessions(program.id).subscribe({
      next: s => { this.programSessions = s; this.sessionsLoading = false; },
      error: () => { this.sessionsLoading = false; }
    });
  }

  closePlanDetail(): void {
    this.selectedProgram = null;
    this.programSessions = [];
  }

  getSessionsByWeek(): { week: number; sessions: ProgramSession[] }[] {
    const map = new Map<number, ProgramSession[]>();
    for (const s of this.programSessions) {
      if (!map.has(s.weekNumber)) map.set(s.weekNumber, []);
      map.get(s.weekNumber)!.push(s);
    }
    return Array.from(map.entries())
      .sort((a, b) => a[0] - b[0])
      .map(([week, sessions]) => ({
        week,
        sessions: sessions.sort((a, b) => a.dayNumber - b.dayNumber)
      }));
  }

  getWeekDistance(sessions: ProgramSession[]): number {
    return sessions.reduce((sum, s) => sum + (s.distanceKm || 0), 0);
  }

  getDayName(dayNumber: number): string {
    const days = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
    return days[(dayNumber - 1) % 7] || 'Day ' + dayNumber;
  }

  // ── Existing methods ──────────────────────────────────────────────

  startProgram(id: number): void {
    this.programService.startProgram(id).subscribe({
      next: (progress) => {
        this.myProgress.push(progress);
        this.snackBar.open('Program started! Good luck!', 'Close', { duration: 3000 });
      },
      error: (err) => {
        this.snackBar.open(err.error?.message || 'Could not start program', 'Close', { duration: 3000 });
      }
    });
  }

  isEnrolled(programId: number): boolean {
    return this.myProgress.some(p => p.programId === programId && p.status === 'ACTIVE');
  }

  getProgress(programId: number): ProgramProgress | undefined {
    return this.myProgress.find(p => p.programId === programId);
  }

  progressPercent(p: ProgramProgress): number {
    if (!p.totalSessions) return 0;
    return Math.round((p.completedSessions / p.totalSessions) * 100);
  }

  getLevelColor(level: string): string {
    if (level === 'BEGINNER') return 'bg-green-500/20 text-green-400 border-green-500/30';
    if (level === 'INTERMEDIATE') return 'bg-primary/20 text-primary border-primary/30';
    if (level === 'ADVANCED') return 'bg-red-500/20 text-red-400 border-red-500/30';
    return 'bg-secondary text-muted-foreground border-border';
  }

  getActivePrograms(): ProgramProgress[] {
    return this.myProgress.filter(p => p.status === 'ACTIVE');
  }

  getSelectedGoal() {
    return this.goalOptions.find(g => g.value === this.planRequest.goalType);
  }

  goToLiveSession(programId: number): void {
    this.router.navigate(['/programs', programId, 'live']);
  }
}
