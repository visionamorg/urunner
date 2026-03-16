import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FeedService } from '../../core/services/feed.service';
import { Post } from '../../core/models/post.model';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-feed',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './feed.component.html',
  styleUrl: './feed.component.scss'
})
export class FeedComponent implements OnInit {
  posts: Post[] = [];
  loading = true;
  newPostContent = '';
  currentUserId = this.authService.getCurrentUser()?.userId;

  constructor(
    private feedService: FeedService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.feedService.getPosts(0, 30).subscribe({
      next: p => { this.posts = p.content; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  createPost(): void {
    if (!this.newPostContent.trim()) return;
    this.feedService.createPost({ content: this.newPostContent }).subscribe({
      next: (post) => {
        this.posts.unshift(post);
        this.newPostContent = '';
      }
    });
  }

  toggleLike(post: Post): void {
    this.feedService.likePost(post.id).subscribe({
      next: (updated) => {
        const idx = this.posts.findIndex(p => p.id === post.id);
        if (idx >= 0) this.posts[idx] = updated;
      }
    });
  }

  getInitials(username: string): string {
    return username.substring(0, 2).toUpperCase();
  }

  timeAgo(dateStr: string): string {
    const diff = Date.now() - new Date(dateStr).getTime();
    const min = Math.floor(diff / 60000);
    if (min < 60) return `${min}m ago`;
    const h = Math.floor(min / 60);
    if (h < 24) return `${h}h ago`;
    return `${Math.floor(h / 24)}d ago`;
  }

  isLikedByMe(post: Post): boolean {
    return post.likedByCurrentUser === true;
  }

  getCurrentUsername(): string {
    return this.authService.getCurrentUser()?.username || '';
  }
}
