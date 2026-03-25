import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { ToastService, Toast } from '../../../core/services/toast.service';

@Component({
  selector: 'app-toast-container',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="fixed top-4 right-4 z-[9999] flex flex-col gap-2 max-w-sm w-full pointer-events-none">
      @for (toast of toasts; track toast.id) {
        <div class="pointer-events-auto rounded-xl px-4 py-3 text-sm font-medium shadow-lg border backdrop-blur-sm animate-slide-in flex items-center gap-2"
          [class]="getClasses(toast.type)">
          <span class="material-icons text-base">{{ getIcon(toast.type) }}</span>
          <span class="flex-1">{{ toast.message }}</span>
          <button (click)="dismiss(toast.id)" class="ml-2 opacity-60 hover:opacity-100 transition-opacity">
            <span class="material-icons text-sm">close</span>
          </button>
        </div>
      }
    </div>
  `,
  styles: [`
    @keyframes slideIn {
      from { transform: translateX(100%); opacity: 0; }
      to { transform: translateX(0); opacity: 1; }
    }
    .animate-slide-in { animation: slideIn 0.25s ease-out; }
  `]
})
export class ToastContainerComponent implements OnInit, OnDestroy {
  toasts: Toast[] = [];
  private subs: Subscription[] = [];

  constructor(private toastService: ToastService) {}

  ngOnInit(): void {
    this.subs.push(
      this.toastService.toasts$.subscribe(t => this.toasts.push(t)),
      this.toastService.dismiss$.subscribe(id => this.dismiss(id))
    );
  }

  ngOnDestroy(): void {
    this.subs.forEach(s => s.unsubscribe());
  }

  dismiss(id: number): void {
    this.toasts = this.toasts.filter(t => t.id !== id);
  }

  getClasses(type: string): string {
    switch (type) {
      case 'success': return 'bg-green-500/20 border-green-500/30 text-green-400';
      case 'error': return 'bg-red-500/20 border-red-500/30 text-red-400';
      default: return 'bg-blue-500/20 border-blue-500/30 text-blue-400';
    }
  }

  getIcon(type: string): string {
    switch (type) {
      case 'success': return 'check_circle';
      case 'error': return 'error';
      default: return 'info';
    }
  }
}
