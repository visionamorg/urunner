import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ThemeService } from '../../core/services/theme.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit, OnDestroy {
  private themeService = inject(ThemeService);

  mobileMenuOpen = false;
  activeStatIndex = 0;
  private statInterval: any;
  private observerEntries: IntersectionObserver[] = [];

  stats = [
    { value: '10K+', label: 'Active Runners', icon: 'directions_run' },
    { value: '500+', label: 'Communities', icon: 'groups' },
    { value: '1M+', label: 'KM Tracked', icon: 'route' },
    { value: '200+', label: 'Events', icon: 'event' }
  ];

  features = [
    {
      icon: 'directions_run',
      title: 'Activity Tracking',
      description: 'Log your runs manually or auto-sync from Strava and Garmin. Track distance, pace, elevation, and more.',
      color: 'primary'
    },
    {
      icon: 'groups',
      title: 'Communities',
      description: 'Create or join running groups. Share photos, post updates, and stay connected with fellow runners.',
      color: 'blue'
    },
    {
      icon: 'leaderboard',
      title: 'Rankings',
      description: 'Compete on weekly, monthly, and all-time leaderboards. See where you stand among the best.',
      color: 'destructive'
    },
    {
      icon: 'event',
      title: 'Events',
      description: 'Discover and register for races, group runs, and community events near you.',
      color: 'green'
    },
    {
      icon: 'fitness_center',
      title: 'Training Programs',
      description: 'Follow structured training plans for 5K to ultra-marathons. Track your session progress.',
      color: 'purple'
    },
    {
      icon: 'chat',
      title: 'Chat & Rooms',
      description: 'Direct messages and community group chats. Stay in touch with your running crew.',
      color: 'cyan'
    },
    {
      icon: 'military_tech',
      title: 'Badges & Awards',
      description: 'Earn badges for achievements — distance milestones, streak records, event completions.',
      color: 'amber'
    },
    {
      icon: 'smart_toy',
      title: 'AI Coach',
      description: 'Get personalized training advice and race strategy from our AI-powered running coach.',
      color: 'pink'
    }
  ];

  steps = [
    { number: '01', title: 'Create Your Account', description: 'Sign up in seconds with email or connect your Strava/Garmin account.', icon: 'person_add' },
    { number: '02', title: 'Track Your Runs', description: 'Log activities, sync data, and watch your stats grow over time.', icon: 'timeline' },
    { number: '03', title: 'Join the Community', description: 'Connect with runners, join events, and climb the leaderboards.', icon: 'emoji_events' }
  ];

  get isDark() {
    return this.themeService.isDark();
  }

  toggleTheme() {
    this.themeService.toggle();
  }

  toggleMobileMenu() {
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }

  ngOnInit() {
    this.statInterval = setInterval(() => {
      this.activeStatIndex = (this.activeStatIndex + 1) % this.stats.length;
    }, 3000);
  }

  ngOnDestroy() {
    if (this.statInterval) clearInterval(this.statInterval);
    this.observerEntries.forEach(o => o.disconnect());
  }

  getFeatureColorClasses(color: string): { bg: string; border: string; text: string; glow: string } {
    const map: Record<string, { bg: string; border: string; text: string; glow: string }> = {
      primary:     { bg: 'bg-primary/15', border: 'border-primary/30', text: 'text-primary', glow: 'shadow-primary/20' },
      blue:        { bg: 'bg-blue-500/15', border: 'border-blue-500/30', text: 'text-blue-500', glow: 'shadow-blue-500/20' },
      destructive: { bg: 'bg-destructive/15', border: 'border-destructive/30', text: 'text-destructive', glow: 'shadow-destructive/20' },
      green:       { bg: 'bg-emerald-500/15', border: 'border-emerald-500/30', text: 'text-emerald-500', glow: 'shadow-emerald-500/20' },
      purple:      { bg: 'bg-purple-500/15', border: 'border-purple-500/30', text: 'text-purple-500', glow: 'shadow-purple-500/20' },
      cyan:        { bg: 'bg-cyan-500/15', border: 'border-cyan-500/30', text: 'text-cyan-500', glow: 'shadow-cyan-500/20' },
      amber:       { bg: 'bg-amber-500/15', border: 'border-amber-500/30', text: 'text-amber-500', glow: 'shadow-amber-500/20' },
      pink:        { bg: 'bg-pink-500/15', border: 'border-pink-500/30', text: 'text-pink-500', glow: 'shadow-pink-500/20' }
    };
    return map[color] || map['primary'];
  }
}
