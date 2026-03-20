import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ProgramService } from '../../core/services/program.service';
import { Program, ProgramProgress } from '../../core/models/program.model';

@Component({
  selector: 'app-programs',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './programs.component.html',
  styleUrl: './programs.component.scss'
})
export class ProgramsComponent implements OnInit {
  Math = Math;
  programs: Program[] = [];
  myProgress: ProgramProgress[] = [];
  loading = true;

  constructor(
    private programService: ProgramService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.programService.getAll().subscribe({
      next: p => { this.programs = p; this.loading = false; },
      error: () => { this.loading = false; }
    });
    this.programService.getMyProgress().subscribe({
      next: p => this.myProgress = p
    });
  }

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
}
