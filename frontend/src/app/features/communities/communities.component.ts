import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CommunityService } from '../../core/services/community.service';
import { Community } from '../../core/models/community.model';
import { HttpClient } from '@angular/common/http';
import { Subject, debounceTime, distinctUntilChanged } from 'rxjs';

@Component({
  selector: 'app-communities',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './communities.component.html',
  styleUrl: './communities.component.scss'
})
export class CommunitiesComponent implements OnInit {
  communities: Community[] = [];
  allCommunities: Community[] = [];
  loading = true;
  showForm = false;
  form: FormGroup;
  searchQuery = '';
  selectedCategory = '';
  categories = ['TRAIL', 'ROAD', 'URBAN', 'MARATHON', 'CASUAL'];
  private searchSubject = new Subject<string>();

  constructor(
    private communityService: CommunityService,
    private fb: FormBuilder,
    private router: Router,
    private http: HttpClient
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
    this.searchSubject.pipe(debounceTime(300), distinctUntilChanged()).subscribe(() => this.filter());
  }

  load(): void {
    this.communityService.getAll().subscribe({
      next: c => { this.communities = c; this.allCommunities = c; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  onSearchInput(): void {
    this.searchSubject.next(this.searchQuery);
  }

  filter(): void {
    this.communities = this.allCommunities.filter(c => {
      const matchesSearch = !this.searchQuery ||
        c.name.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        (c.description?.toLowerCase().includes(this.searchQuery.toLowerCase()));
      const matchesCategory = !this.selectedCategory ||
        (c as any).category === this.selectedCategory;
      return matchesSearch && matchesCategory;
    });
  }

  selectCategory(cat: string): void {
    this.selectedCategory = this.selectedCategory === cat ? '' : cat;
    this.filter();
  }

  requestJoin(id: number, event: Event): void {
    event.stopPropagation();
    this.http.post(`/api/communities/${id}/request-join`, {}).subscribe({
      next: () => alert('Join request sent!'),
      error: (err) => console.error('Could not request join', err)
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
