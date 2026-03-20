import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private readonly STORAGE_KEY = 'runhub-theme';
  isDark = signal(this.getInitialTheme());

  constructor() {
    this.applyTheme();
  }

  toggle(): void {
    this.isDark.set(!this.isDark());
    this.applyTheme();
  }

  private getInitialTheme(): boolean {
    const stored = localStorage.getItem(this.STORAGE_KEY);
    if (stored) return stored === 'dark';
    return window.matchMedia('(prefers-color-scheme: dark)').matches;
  }

  private applyTheme(): void {
    const html = document.documentElement;
    if (this.isDark()) {
      html.classList.add('dark');
      localStorage.setItem(this.STORAGE_KEY, 'dark');
    } else {
      html.classList.remove('dark');
      localStorage.setItem(this.STORAGE_KEY, 'light');
    }
  }
}
