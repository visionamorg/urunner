import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RankingService } from '../../core/services/ranking.service';
import { Ranking } from '../../core/models/ranking.model';
import { AuthService } from '../../core/services/auth.service';
import { AvatarComponent } from '../../shared/components/avatar/avatar.component';

@Component({
  selector: 'app-rankings',
  standalone: true,
  imports: [CommonModule, AvatarComponent],
  templateUrl: './rankings.component.html',
  styleUrl: './rankings.component.scss'
})
export class RankingsComponent implements OnInit {
  weeklyRankings: Ranking[] = [];
  monthlyRankings: Ranking[] = [];
  allTimeRankings: Ranking[] = [];
  loading = true;
  activeTab: 'weekly' | 'monthly' | 'alltime' = 'weekly';
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

  setTab(tab: 'weekly' | 'monthly' | 'alltime'): void {
    this.activeTab = tab;
  }

  get currentRankings(): Ranking[] {
    if (this.activeTab === 'weekly') return this.weeklyRankings;
    if (this.activeTab === 'monthly') return this.monthlyRankings;
    return this.allTimeRankings;
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

  getInitials(username: string): string {
    return username.substring(0, 2).toUpperCase();
  }

  getRankStyle(rank: number): string {
    if (rank === 1) return 'bg-gradient-to-r from-yellow-500/20 to-transparent border-yellow-500/30';
    if (rank === 2) return 'bg-gradient-to-r from-slate-400/10 to-transparent border-slate-400/20';
    if (rank === 3) return 'bg-gradient-to-r from-amber-700/20 to-transparent border-amber-700/30';
    return 'border-transparent hover:border-border';
  }
}
