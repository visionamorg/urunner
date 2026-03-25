import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

export interface Toast {
  id: number;
  message: string;
  type: 'success' | 'error' | 'info';
}

@Injectable({ providedIn: 'root' })
export class ToastService {
  private counter = 0;
  toasts$ = new Subject<Toast>();
  dismiss$ = new Subject<number>();

  show(message: string, type: 'success' | 'error' | 'info' = 'info'): void {
    const id = ++this.counter;
    this.toasts$.next({ id, message, type });
    setTimeout(() => this.dismiss$.next(id), 4000);
  }

  success(message: string): void { this.show(message, 'success'); }
  error(message: string): void { this.show(message, 'error'); }
  info(message: string): void { this.show(message, 'info'); }
}
