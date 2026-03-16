import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
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
    private router: Router
  ) {
    this.form = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      driveFolderId: [''],
      imageUrl: [''],
      coverUrl: ['']
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

  navigateToCommunity(id: number): void {
    this.router.navigate(['/communities', id]);
  }

  join(id: number, event: Event): void {
    event.stopPropagation();
    this.communityService.join(id).subscribe({
      next: () => this.load(),
      error: (err) => console.error('Could not join community', err)
    });
  }

  leave(id: number, event: Event): void {
    event.stopPropagation();
    this.communityService.leave(id).subscribe({
      next: () => this.load(),
      error: (err) => console.error('Could not leave community', err)
    });
  }

  onCreate(): void {
    if (this.form.invalid) return;
    this.communityService.create(this.form.value).subscribe({
      next: (community) => {
        this.showForm = false;
        this.form.reset();
        this.load();
        this.router.navigate(['/communities', community.id]);
      },
      error: (err) => console.error('Could not create community', err)
    });
  }

  getInitials(name: string): string {
    return name.substring(0, 2).toUpperCase();
  }
}
