import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ActivityService } from '../../../core/services/activity.service';
import { Activity, ActivityInsight, ActivityChatMessage } from '../../../core/models/activity.model';

@Component({
  selector: 'app-activity-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './activity-detail.component.html',
  styleUrl: './activity-detail.component.scss'
})
export class ActivityDetailComponent implements OnInit {
  @ViewChild('chatContainer') chatContainer!: ElementRef;

  activity: Activity | null = null;
  insight: ActivityInsight | null = null;
  loading = true;
  analyzing = false;
  chatMessages: ActivityChatMessage[] = [];
  chatInput = '';
  chatLoading = false;
  showChat = false;
  copiedCaption = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private activityService: ActivityService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) {
      this.router.navigate(['/activities']);
      return;
    }

    this.activityService.getActivityById(id).subscribe({
      next: a => {
        this.activity = a;
        this.loading = false;
        this.loadInsight(id);
      },
      error: () => {
        this.loading = false;
        this.router.navigate(['/activities']);
      }
    });
  }

  loadInsight(id: number): void {
    this.activityService.getActivityInsight(id).subscribe({
      next: insight => this.insight = insight,
      error: () => {}
    });
  }

  analyzeActivity(): void {
    if (!this.activity || this.analyzing) return;
    this.analyzing = true;
    this.activityService.analyzeActivity(this.activity.id).subscribe({
      next: insight => {
        this.insight = insight;
        this.analyzing = false;
      },
      error: () => { this.analyzing = false; }
    });
  }

  sendChatMessage(): void {
    if (!this.activity || !this.chatInput.trim() || this.chatLoading) return;

    const userMsg = this.chatInput.trim();
    this.chatMessages.push({ role: 'user', content: userMsg });
    this.chatInput = '';
    this.chatLoading = true;

    this.scrollChatToBottom();

    const history = this.chatMessages.slice(0, -1);

    this.activityService.chatAboutActivity(this.activity.id, {
      message: userMsg,
      history
    }).subscribe({
      next: res => {
        this.chatMessages.push({ role: 'assistant', content: res.reply });
        this.chatLoading = false;
        this.scrollChatToBottom();
      },
      error: () => {
        this.chatMessages.push({ role: 'assistant', content: 'Sorry, I could not process your question right now. Please try again.' });
        this.chatLoading = false;
        this.scrollChatToBottom();
      }
    });
  }

  scrollChatToBottom(): void {
    setTimeout(() => {
      if (this.chatContainer) {
        this.chatContainer.nativeElement.scrollTop = this.chatContainer.nativeElement.scrollHeight;
      }
    }, 50);
  }

  copyCaption(): void {
    if (!this.insight?.socialCaption) return;
    navigator.clipboard.writeText(this.insight.socialCaption);
    this.copiedCaption = true;
    setTimeout(() => this.copiedCaption = false, 2000);
  }

  formatPace(pace: number): string {
    if (!pace) return '--';
    const min = Math.floor(pace);
    const sec = Math.round((pace - min) * 60);
    return `${min}:${sec.toString().padStart(2, '0')} /km`;
  }

  formatDuration(min: number): string {
    const h = Math.floor(min / 60);
    const m = min % 60;
    return h > 0 ? `${h}h ${m}m` : `${m}m`;
  }

  getIntensityColor(intensity: string): string {
    const colors: Record<string, string> = {
      'EASY': 'bg-green-500/20 text-green-400 border-green-500/30',
      'MODERATE': 'bg-blue-500/20 text-blue-400 border-blue-500/30',
      'TEMPO': 'bg-yellow-500/20 text-yellow-400 border-yellow-500/30',
      'THRESHOLD': 'bg-orange-500/20 text-orange-400 border-orange-500/30',
      'HARD': 'bg-red-500/20 text-red-400 border-red-500/30',
      'RACE': 'bg-purple-500/20 text-purple-400 border-purple-500/30'
    };
    return colors[intensity] || colors['MODERATE'];
  }

  openExportStudio(): void {
    if (this.activity) {
      this.router.navigate(['/export-studio'], { queryParams: { activityId: this.activity.id } });
    }
  }
}
