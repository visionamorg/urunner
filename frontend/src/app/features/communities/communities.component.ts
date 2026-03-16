import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CommunityService } from '../../core/services/community.service';
import { Community } from '../../core/models/community.model';

@Component({
  selector: 'app-communities',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './communities.component.html',
  styleUrl: './communities.component.scss'
})
export class CommunitiesComponent implements OnInit {
  communities: Community[] = [];
  loading = true;
  showForm = false;
  form: FormGroup;

  constructor(
    private communityService: CommunityService,
    private fb: FormBuilder,
    private snackBar: MatSnackBar
  ) {
    this.form = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.communityService.getAll().subscribe({
      next: c => { this.communities = c; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  joinCommunity(id: number): void {
    this.communityService.join(id).subscribe({
      next: () => {
        this.snackBar.open('Joined community!', 'Close', { duration: 3000 });
        this.load();
      },
      error: (err) => {
        this.snackBar.open(err.error?.message || 'Could not join', 'Close', { duration: 3000 });
      }
    });
  }

  onCreate(): void {
    if (this.form.invalid) return;
    this.communityService.create(this.form.value).subscribe({
      next: () => {
        this.showForm = false;
        this.form.reset();
        this.snackBar.open('Community created!', 'Close', { duration: 3000 });
        this.load();
      }
    });
  }

  getInitials(name: string): string {
    return name.substring(0, 2).toUpperCase();
  }
}
