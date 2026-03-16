import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { CommunityService } from '../../../core/services/community.service';
import { FeedService } from '../../../core/services/feed.service';
import { Community, CommunityMember } from '../../../core/models/community.model';
import { Post, Comment } from '../../../core/models/post.model';

@Component({
  selector: 'app-community-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './community-detail.component.html',
  styleUrl: './community-detail.component.scss'
})
export class CommunityDetailComponent implements OnInit {
  community: Community | null = null;
  posts: Post[] = [];
  members: CommunityMember[] = [];
  activeTab: 'feed' | 'members' | 'settings' = 'feed';
  loading = true;
  feedLoading = false;
  syncing = false;
  postContent = '';
  saving = false;

  showComments: { [postId: number]: boolean } = {};
  commentInput: { [postId: number]: string } = {};

  // Settings form
  settingsName = '';
  settingsDescription = '';
  settingsDriveFolderId = '';
  settingsSaving = false;
  settingsSuccess = false;

  private communityId!: number;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private communityService: CommunityService,
    private feedService: FeedService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.communityId = +params['id'];
      this.loadCommunity();
    });
  }

  loadCommunity(): void {
    this.loading = true;
    this.communityService.getOne(this.communityId).subscribe({
      next: (c) => {
        this.community = c;
        this.settingsName = c.name;
        this.settingsDescription = c.description;
        this.settingsDriveFolderId = c.driveFolderId || '';
        this.loading = false;
        this.loadFeed();
        this.loadMembers();
      },
      error: () => {
        this.loading = false;
        this.router.navigate(['/communities']);
      }
    });
  }

  loadFeed(): void {
    this.feedLoading = true;
    this.communityService.getFeed(this.communityId).subscribe({
      next: (page) => {
        this.posts = page.content;
        this.feedLoading = false;
      },
      error: () => { this.feedLoading = false; }
    });
  }

  loadMembers(): void {
    this.communityService.getMembers(this.communityId).subscribe({
      next: (members) => { this.members = members; },
      error: () => {}
    });
  }

  joinOrLeave(): void {
    if (!this.community) return;
    if (this.community.joined) {
      this.communityService.leave(this.communityId).subscribe({
        next: () => this.loadCommunity(),
        error: (err) => console.error(err)
      });
    } else {
      this.communityService.join(this.communityId).subscribe({
        next: () => this.loadCommunity(),
        error: (err) => console.error(err)
      });
    }
  }

  createPost(): void {
    if (!this.postContent.trim()) return;
    this.communityService.createPost(this.communityId, {
      content: this.postContent.trim(),
      postType: 'TEXT'
    }).subscribe({
      next: (post) => {
        this.posts.unshift(post);
        this.postContent = '';
      },
      error: (err) => console.error(err)
    });
  }

  toggleLike(post: Post): void {
    // Optimistic update
    const wasLiked = post.liked || post.likedByCurrentUser;
    post.liked = !wasLiked;
    post.likedByCurrentUser = !wasLiked;
    post.likesCount = wasLiked ? Math.max(0, post.likesCount - 1) : post.likesCount + 1;

    this.feedService.toggleLike(post.id).subscribe({
      next: (updated) => {
        post.likesCount = updated.likesCount;
        post.liked = updated.liked || updated.likedByCurrentUser;
        post.likedByCurrentUser = post.liked;
      },
      error: () => {
        // Revert on error
        post.liked = wasLiked;
        post.likedByCurrentUser = wasLiked;
        post.likesCount = wasLiked ? post.likesCount + 1 : Math.max(0, post.likesCount - 1);
      }
    });
  }

  toggleComments(postId: number): void {
    this.showComments[postId] = !this.showComments[postId];
    if (this.showComments[postId]) {
      // Lazy load comments
      this.feedService.getComments(postId).subscribe({
        next: (comments) => {
          const post = this.posts.find(p => p.id === postId);
          if (post) post.comments = comments;
        },
        error: () => {}
      });
    }
  }

  addComment(postId: number): void {
    const content = (this.commentInput[postId] || '').trim();
    if (!content) return;

    this.feedService.addComment(postId, content).subscribe({
      next: (comment) => {
        const post = this.posts.find(p => p.id === postId);
        if (post) {
          if (!post.comments) post.comments = [];
          post.comments.push(comment);
          post.commentsCount++;
        }
        this.commentInput[postId] = '';
      },
      error: (err) => console.error(err)
    });
  }

  syncDrive(): void {
    if (!this.community) return;
    this.syncing = true;
    this.communityService.syncDrive(this.communityId).subscribe({
      next: (post) => {
        this.posts.unshift(post);
        this.syncing = false;
        this.activeTab = 'feed';
      },
      error: (err) => {
        console.error(err);
        this.syncing = false;
        alert(err.error?.message || 'Failed to sync Drive photos');
      }
    });
  }

  updateCommunity(): void {
    if (!this.settingsName.trim()) return;
    this.settingsSaving = true;
    this.settingsSuccess = false;

    this.communityService.update(this.communityId, {
      name: this.settingsName,
      description: this.settingsDescription,
      driveFolderId: this.settingsDriveFolderId
    }).subscribe({
      next: (updated) => {
        this.community = updated;
        this.settingsSaving = false;
        this.settingsSuccess = true;
        setTimeout(() => { this.settingsSuccess = false; }, 3000);
      },
      error: (err) => {
        console.error(err);
        this.settingsSaving = false;
      }
    });
  }

  getPhotoGridClass(count: number): string {
    if (count === 1) return 'grid-cols-1';
    if (count === 2) return 'grid-cols-2';
    if (count === 3 || count >= 4) return 'grid-cols-2';
    return 'grid-cols-2';
  }

  getInitials(name: string): string {
    if (!name) return '?';
    const parts = name.split(' ');
    if (parts.length >= 2) {
      return (parts[0][0] + parts[1][0]).toUpperCase();
    }
    return name.substring(0, 2).toUpperCase();
  }

  timeAgo(dateStr: string): string {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffSeconds = Math.floor(diffMs / 1000);
    const diffMinutes = Math.floor(diffSeconds / 60);
    const diffHours = Math.floor(diffMinutes / 60);
    const diffDays = Math.floor(diffHours / 24);

    if (diffSeconds < 60) return 'just now';
    if (diffMinutes < 60) return `${diffMinutes}m ago`;
    if (diffHours < 24) return `${diffHours}h ago`;
    if (diffDays < 7) return `${diffDays}d ago`;
    return date.toLocaleDateString();
  }

  getRoleBadgeClass(role: string): string {
    switch (role?.toUpperCase()) {
      case 'ADMIN': return 'bg-orange-500/20 text-orange-400 border-orange-500/30';
      case 'MODERATOR': return 'bg-blue-500/20 text-blue-400 border-blue-500/30';
      default: return 'bg-slate-500/20 text-slate-400 border-slate-500/30';
    }
  }
}
