import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RankingService } from '../../core/services/ranking.service';
import { Ranking } from '../../core/models/ranking.model';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-rankings',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule, MatButtonModule, MatIconModule,
    MatTabsModule, MatProgressSpinnerModule
  ],
  templateUrl: './rankings.component.html',
  styleUrl: './rankings.component.scss'
})
export class RankingsComponent implements OnInit {
  weeklyRankings: Ranking[] = [];
  monthlyRankings: Ranking[] = [];
  allTimeRankings: Ranking[] = [];
  loading = true;
  currentUserId = this.authService.getCurrentUser()?.userId;

  constructor(
    private rankingService: RankingService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.rankingService.getGlobal('weekly').subscribe(r => { this.weeklyRankings = r; this.checkDone(); });
    this.rankingService.getGlobal('monthly').subscribe(r => { this.monthlyRankings = r; this.checkDone(); });
    this.rankingService.getGlobal('alltime').subscribe(r => { this.allTimeRankings = r; this.checkDone(); });
  }

  private count = 0;
  private checkDone(): void {
    if (++this.count >= 3) this.loading = false;
  }

  getMedalEmoji(rank: number): string {
    if (rank === 1) return '🥇';
    if (rank === 2) return '🥈';
    if (rank === 3) return '🥉';
    return rank.toString();
  }

  isCurrentUser(userId: number): boolean {
    return this.currentUserId === userId;
  }
}
