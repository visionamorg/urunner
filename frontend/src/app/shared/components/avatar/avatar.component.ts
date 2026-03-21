import { Component, Input, OnChanges } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-avatar',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (imageUrl && !imgError) {
      <img [src]="imageUrl" [alt]="username"
        class="w-full h-full object-cover"
        (error)="imgError = true" />
    } @else {
      {{ initials }}
    }
  `
})
export class AvatarComponent implements OnChanges {
  @Input() imageUrl?: string | null;
  @Input() username = '';

  imgError = false;
  initials = '';

  ngOnChanges(): void {
    this.imgError = false;
    this.initials = this.username.substring(0, 2).toUpperCase();
  }
}
