import { Component, OnInit, OnDestroy, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { CommunityService } from '../../../core/services/community.service';
import { FeedService } from '../../../core/services/feed.service';
import { ChatService } from '../../../core/services/chat.service';
import { AuthService } from '../../../core/services/auth.service';
import { EventService } from '../../../core/services/event.service';
import { CommunityGoalService, CommunityGoal, CreateGoalRequest } from '../../../core/services/community-goal.service';
import { Community, CommunityMember, InviteDto, DriveFolderDto } from '../../../core/models/community.model';
import { Post, Comment } from '../../../core/models/post.model';
import { RunEvent, CreateEventRequest, UpdateEventRequest } from '../../../core/models/event.model';
import { Message } from '../../../core/models/message.model';
import { CommunityCalendarComponent } from '../community-calendar/community-calendar.component';
import { CommunityRoomsComponent } from '../community-rooms/community-rooms.component';
import { AvatarComponent } from '../../../shared/components/avatar/avatar.component';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-community-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, CommunityCalendarComponent, CommunityRoomsComponent, AvatarComponent],
  templateUrl: './community-detail.component.html',
  styleUrl: './community-detail.component.scss'
})
export class CommunityDetailComponent implements OnInit, OnDestroy {
  community: Community | null = null;
  posts: Post[] = [];
  members: CommunityMember[] = [];
  invites: InviteDto[] = [];
  activeTab: 'feed' | 'members' | 'invites' | 'settings' | 'events' | 'calendar' | 'chat' | 'rooms' | 'leaderboard' = 'feed';

  // ── Events Tab ─────────────────────────────────────────────────────────────
  communityEvents: RunEvent[] = [];
  eventsLoading = false;
  eventsLoaded = false;
  showEventForm = false;
  editingEvent: RunEvent | null = null;
  eventForm: CreateEventRequest = { name: '', description: '', eventDate: '', location: '', distanceKm: 0, price: 0, photoUrls: [] };
  savingEvent = false;
  eventError = '';
  eventPhotoInput = '';

  // ── Event Detail Modal ──────────────────────────────────────────────────────
  selectedEvent: RunEvent | null = null;
  eventParticipants: any[] = [];
  eventMessages: Message[] = [];
  eventChatInput = '';
  sendingEventChat = false;
  loadingEventDetail = false;
  eventRegistered: { [eventId: number]: boolean } = {};

  // ── Event Gallery ─────────────────────────────────────────────────────────
  galleryPhotos: any[] = [];
  galleryLoading = false;
  gallerySyncing = false;
  galleryUploadUrl = '';
  galleryUploading = false;
  galleryDriveFolderInput = '';
  showGalleryLinkForm = false;

  // ── Chat Tab ───────────────────────────────────────────────────────────────
  chatMessages: Message[] = [];
  chatInput = '';
  chatLoading = false;
  chatLoaded = false;
  sendingChat = false;
  chatMediaUrl = '';
  chatMediaType = 'image';
  showChatMediaInput = false;
  currentUsername = '';
  loading = true;
  feedLoading = false;
  feedPage = 0;
  feedHasMore = false;
  syncing = false;
  postContent = '';
  saving = false;

  showComments: { [postId: number]: boolean } = {};
  commentInput: { [postId: number]: string } = {};

  // Drive folder picker
  driveFolders: DriveFolderDto[] = [];
  showDrivePicker = false;
  loadingFolders = false;
  syncingFolderId: string | null = null;
  driveError = '';

  // Lightbox
  lightboxPhotos: string[] = [];
  lightboxIndex = 0;
  lightboxOpen = false;
  lightboxTransitioning = false;
  private touchStartX = 0;

  // Community Goal
  communityGoal: CommunityGoal | null = null;
  showGoalForm = false;
  goalForm: CreateGoalRequest = { title: '', targetKm: 500, startDate: '', endDate: '' };
  savingGoal = false;

  // Reactions
  readonly EMOJIS = ['❤️', '👍', '😂', '😮', '😢', '🙌'];
  showReactionPicker: { [postId: number]: boolean } = {};

  // Invite state
  inviteUsername = '';
  inviteLoading = false;
  inviteError = '';
  inviteSuccess = '';

  // Settings form
  settingsName = '';
  settingsDescription = '';
  settingsDriveFolderId = '';
  settingsCoverUrl = '';
  settingsImageUrl = '';
  settingsSaving = false;
  settingsSuccess = false;

  // Role change state
  changingRole: { [userId: number]: boolean } = {};
  membersLoading = true;

  // Leaderboard
  leaderboard: any[] = [];
  weeklyChallenge: any[] = [];
  leaderboardLoading = false;
  leaderboardMetric: 'distance' | 'time' | 'elevation' = 'distance';
  weeklyGoalKm = 50;
  generatingDigest = false;

  communityId!: number;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private communityService: CommunityService,
    private feedService: FeedService,
    private chatService: ChatService,
    private authService: AuthService,
    private eventService: EventService,
    private goalService: CommunityGoalService,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.currentUsername = this.authService.getCurrentUser()?.username || '';
    this.route.params.subscribe(params => {
      this.communityId = +params['id'];
      this.activeTab = 'feed';
      this.eventsLoaded = false;
      this.chatLoaded = false;
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
        this.settingsCoverUrl = c.coverUrl || '';
        this.settingsImageUrl = c.imageUrl || '';
        this.loading = false;
        this.loadFeed();
        this.loadMembers();
        if (c.isAdmin) this.loadInvites();
        this.loadGoal();
      },
      error: () => {
        this.loading = false;
        this.router.navigate(['/communities']);
      }
    });
  }

  loadFeed(): void {
    this.feedLoading = true;
    this.feedPage = 0;
    this.communityService.getFeed(this.communityId, 0).subscribe({
      next: (page) => {
        this.posts = page.content;
        this.feedHasMore = !page.last;
        this.feedLoading = false;
      },
      error: () => { this.feedLoading = false; this.toast.error('Failed to load feed'); }
    });
    if (!this.eventsLoaded) this.loadEvents();
  }

  loadMorePosts(): void {
    if (this.feedLoading || !this.feedHasMore) return;
    this.feedLoading = true;
    this.feedPage++;
    this.communityService.getFeed(this.communityId, this.feedPage).subscribe({
      next: (page) => {
        this.posts = [...this.posts, ...page.content];
        this.feedHasMore = !page.last;
        this.feedLoading = false;
      },
      error: () => { this.feedLoading = false; this.toast.error('Failed to load more posts'); }
    });
  }

  switchTab(tab: 'feed' | 'members' | 'invites' | 'settings' | 'events' | 'calendar' | 'chat' | 'rooms' | 'leaderboard'): void {
    this.activeTab = tab;
    if (tab === 'feed' && !this.eventsLoaded) this.loadEvents();
    if (tab === 'events' && !this.eventsLoaded) this.loadEvents();
    if (tab === 'chat' && !this.chatLoaded) this.loadChat();
    if (tab === 'leaderboard') this.loadLeaderboard();
  }

  loadMembers(): void {
    this.communityService.getMembers(this.communityId).subscribe({
      next: (members) => { this.members = members; this.membersLoading = false; },
      error: () => { this.membersLoading = false; this.toast.error('Failed to load members'); }
    });
  }

  loadInvites(): void {
    this.communityService.getCommunityInvites(this.communityId).subscribe({
      next: (invites) => { this.invites = invites; },
      error: () => { this.toast.error('Failed to load invites'); }
    });
  }

  loadGoal(): void {
    this.goalService.getGoal(this.communityId).subscribe({
      next: (g) => { this.communityGoal = g; },
      error: () => { this.communityGoal = null; }
    });
  }

  saveGoal(): void {
    if (!this.goalForm.title || !this.goalForm.targetKm) return;
    this.savingGoal = true;
    const today = new Date().toISOString().split('T')[0];
    const end = new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0];
    const req: CreateGoalRequest = {
      ...this.goalForm,
      startDate: this.goalForm.startDate || today,
      endDate: this.goalForm.endDate || end,
    };
    this.goalService.setGoal(this.communityId, req).subscribe({
      next: (g) => { this.communityGoal = g; this.showGoalForm = false; this.savingGoal = false; },
      error: () => { this.savingGoal = false; }
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
        post.liked = wasLiked;
        post.likedByCurrentUser = wasLiked;
        post.likesCount = wasLiked ? post.likesCount + 1 : Math.max(0, post.likesCount - 1);
      }
    });
  }

  toggleComments(postId: number): void {
    this.showComments[postId] = !this.showComments[postId];
    if (this.showComments[postId]) {
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

  // ── Admin: Post Controls ────────────────────────────────────────────────

  deletePost(postId: number): void {
    if (!confirm('Delete this post?')) return;
    this.communityService.deletePost(this.communityId, postId).subscribe({
      next: () => { this.posts = this.posts.filter(p => p.id !== postId); },
      error: (err) => this.toast.error(err.error?.message || 'Failed to delete post')
    });
  }

  pinPost(post: Post): void {
    this.communityService.pinPost(this.communityId, post.id).subscribe({
      next: () => { post.pinned = !post.pinned; },
      error: (err) => this.toast.error(err.error?.message || 'Failed to pin post')
    });
  }

  // ── Admin: Member Controls ──────────────────────────────────────────────

  kickMember(userId: number, username: string): void {
    if (!confirm(`Kick @${username} from this community?`)) return;
    this.communityService.kickMember(this.communityId, userId).subscribe({
      next: () => {
        this.members = this.members.filter(m => m.userId !== userId);
        if (this.community) this.community.memberCount = Math.max(0, this.community.memberCount - 1);
      },
      error: (err) => this.toast.error(err.error?.message || 'Failed to remove member')
    });
  }

  changeRole(userId: number, newRole: string): void {
    this.changingRole[userId] = true;
    this.communityService.changeMemberRole(this.communityId, userId, newRole).subscribe({
      next: () => {
        const member = this.members.find(m => m.userId === userId);
        if (member) member.role = newRole;
        this.changingRole[userId] = false;
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Failed to change role');
        this.changingRole[userId] = false;
      }
    });
  }

  // ── Invite Controls ─────────────────────────────────────────────────────

  sendInvite(): void {
    if (!this.inviteUsername.trim()) return;
    this.inviteLoading = true;
    this.inviteError = '';
    this.inviteSuccess = '';

    this.communityService.invite(this.communityId, this.inviteUsername.trim()).subscribe({
      next: (invite) => {
        this.invites.unshift(invite);
        this.inviteUsername = '';
        this.inviteSuccess = `Invite sent to @${invite.invitedUsername}`;
        this.inviteLoading = false;
        if (this.community) this.community.pendingInviteCount = (this.community.pendingInviteCount || 0) + 1;
        setTimeout(() => { this.inviteSuccess = ''; }, 4000);
      },
      error: (err) => {
        this.inviteError = err.error?.message || 'Failed to send invite';
        this.inviteLoading = false;
      }
    });
  }

  cancelInvite(inviteId: number): void {
    this.communityService.cancelInvite(this.communityId, inviteId).subscribe({
      next: () => {
        this.invites = this.invites.filter(i => i.id !== inviteId);
        if (this.community && this.community.pendingInviteCount)
          this.community.pendingInviteCount = Math.max(0, this.community.pendingInviteCount - 1);
      },
      error: (err) => this.toast.error(err.error?.message || 'Failed to cancel invite')
    });
  }

  openDrivePicker(): void {
    if (!this.community?.driveFolderId) {
      this.driveError = 'Set a Google Drive Folder ID in Settings first.';
      return;
    }
    this.driveError = '';
    this.showDrivePicker = true;
    this.loadingFolders = true;
    this.driveFolders = [];

    this.communityService.getDriveFolders(this.communityId).subscribe({
      next: (folders) => {
        this.driveFolders = folders;
        this.loadingFolders = false;
        if (folders.length === 0) {
          this.driveError = 'No event folders found inside the configured Drive folder.';
        }
      },
      error: (err) => {
        this.driveError = err.error?.message || 'Failed to load Drive folders';
        this.loadingFolders = false;
      }
    });
  }

  syncFolder(folder: DriveFolderDto): void {
    this.syncingFolderId = folder.id;
    this.driveError = '';

    this.communityService.syncDrive(this.communityId, folder.id, folder.name).subscribe({
      next: (post) => {
        this.posts.unshift(post);
        this.syncingFolderId = null;
        this.showDrivePicker = false;
        this.activeTab = 'feed';
      },
      error: (err) => {
        this.driveError = err.error?.message || 'Failed to sync photos from ' + folder.name;
        this.syncingFolderId = null;
      }
    });
  }

  closeDrivePicker(): void {
    this.showDrivePicker = false;
    this.driveError = '';
  }

  updateCommunity(): void {
    if (!this.settingsName.trim()) return;
    this.settingsSaving = true;
    this.settingsSuccess = false;

    this.communityService.update(this.communityId, {
      name: this.settingsName,
      description: this.settingsDescription,
      driveFolderId: this.settingsDriveFolderId,
      coverUrl: this.settingsCoverUrl,
      imageUrl: this.settingsImageUrl
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

  getInitials(name: string): string {
    if (!name) return '?';
    const parts = name.split(' ');
    if (parts.length >= 2) return (parts[0][0] + parts[1][0]).toUpperCase();
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

  // ── Lightbox ───────────────────────────────────────────────────────────────

  openLightbox(photos: string[], index: number): void {
    this.lightboxPhotos = photos;
    this.lightboxIndex = index;
    this.lightboxOpen = true;
    document.body.style.overflow = 'hidden';
  }

  closeLightbox(): void {
    this.lightboxOpen = false;
    document.body.style.overflow = '';
  }

  goToPhoto(index: number): void {
    if (index === this.lightboxIndex) return;
    this.lightboxTransitioning = true;
    setTimeout(() => {
      this.lightboxIndex = index;
      this.lightboxTransitioning = false;
    }, 150);
  }

  prevPhoto(): void {
    this.goToPhoto((this.lightboxIndex - 1 + this.lightboxPhotos.length) % this.lightboxPhotos.length);
  }

  nextPhoto(): void {
    this.goToPhoto((this.lightboxIndex + 1) % this.lightboxPhotos.length);
  }

  onTouchStart(e: TouchEvent): void {
    this.touchStartX = e.touches[0].clientX;
  }

  onTouchEnd(e: TouchEvent): void {
    const diff = this.touchStartX - e.changedTouches[0].clientX;
    if (Math.abs(diff) > 50) diff > 0 ? this.nextPhoto() : this.prevPhoto();
  }

  @HostListener('document:keydown', ['$event'])
  onKeyDown(e: KeyboardEvent): void {
    if (!this.lightboxOpen) return;
    if (e.key === 'ArrowRight') this.nextPhoto();
    else if (e.key === 'ArrowLeft') this.prevPhoto();
    else if (e.key === 'Escape') this.closeLightbox();
  }

  ngOnDestroy(): void {
    document.body.style.overflow = '';
  }

  // ── Reactions ──────────────────────────────────────────────────────────────

  toggleReactionPicker(postId: number, event: Event): void {
    event.stopPropagation();
    const isOpen = this.showReactionPicker[postId];
    this.showReactionPicker = {};
    if (!isOpen) this.showReactionPicker[postId] = true;
  }

  closeAllPickers(): void {
    this.showReactionPicker = {};
  }

  sendReaction(post: Post, emoji: string): void {
    this.showReactionPicker = {};
    const prev = post.myReaction;

    // Optimistic update
    if (!post.reactions) post.reactions = {};
    if (prev) {
      post.reactions[prev] = Math.max(0, (post.reactions[prev] || 1) - 1);
      if (post.reactions[prev] === 0) delete post.reactions[prev];
    }
    if (prev !== emoji) {
      post.reactions[emoji] = (post.reactions[emoji] || 0) + 1;
      post.myReaction = emoji;
    } else {
      post.myReaction = null;
    }

    this.feedService.react(post.id, emoji).subscribe({
      next: (updated) => {
        post.reactions = updated.reactions;
        post.myReaction = updated.myReaction;
      },
      error: () => {
        // Revert
        post.reactions = post.reactions;
        post.myReaction = prev;
      }
    });
  }

  reactionEntries(reactions: { [emoji: string]: number } | undefined): { emoji: string; count: number }[] {
    if (!reactions) return [];
    return Object.entries(reactions)
      .filter(([, count]) => count > 0)
      .map(([emoji, count]) => ({ emoji, count }));
  }

  // ── Events Tab Methods ──────────────────────────────────────────────────────

  // ── Leaderboard Tab Methods ─────────────────────────────────────────────────

  loadLeaderboard(): void {
    this.leaderboardLoading = true;
    this.communityService.getCommunityRankings(this.communityId, this.leaderboardMetric).subscribe({
      next: (data) => { this.leaderboard = data.slice(0, 10); this.leaderboardLoading = false; },
      error: () => { this.leaderboardLoading = false; this.toast.error('Failed to load leaderboard'); }
    });
    this.communityService.getCommunityWeeklyChallenge(this.communityId).subscribe({
      next: (data) => { this.weeklyChallenge = data; },
      error: () => {}
    });
  }

  changeLeaderboardMetric(metric: 'distance' | 'time' | 'elevation'): void {
    this.leaderboardMetric = metric;
    this.loadLeaderboard();
  }

  generateDigest(): void {
    this.generatingDigest = true;
    this.communityService.generateWeeklyDigest(this.communityId).subscribe({
      next: () => {
        this.generatingDigest = false;
        this.toast.success('Weekly digest posted to feed!');
        this.loadFeed();
      },
      error: () => { this.generatingDigest = false; this.toast.error('Failed to generate digest'); }
    });
  }

  getMetricValue(entry: any): string {
    switch (this.leaderboardMetric) {
      case 'time': return this.formatDuration(entry.totalDurationMinutes || 0);
      case 'elevation': return (entry.totalElevationMeters || 0) + ' m';
      default: return (entry.totalDistanceKm?.toFixed(1) || '0') + ' km';
    }
  }

  formatDuration(min: number): string {
    const h = Math.floor(min / 60);
    const m = min % 60;
    return h > 0 ? `${h}h ${m}m` : `${m}m`;
  }

  getWeeklyChallengeProgress(entry: any): number {
    if (!this.weeklyGoalKm) return 0;
    return Math.min(100, (entry.totalDistanceKm / this.weeklyGoalKm) * 100);
  }

  // ── Events Tab Methods ──────────────────────────────────────────────────────

  loadEvents(): void {
    if (this.eventsLoaded) return;
    this.eventsLoading = true;
    this.communityService.getCommunityEvents(this.communityId).subscribe({
      next: (events) => { this.communityEvents = events; this.eventsLoading = false; this.eventsLoaded = true; },
      error: () => { this.eventsLoading = false; this.toast.error('Failed to load events'); }
    });
  }

  openCreateEvent(): void {
    this.editingEvent = null;
    this.eventForm = { name: '', description: '', eventDate: '', location: '', distanceKm: 0, price: 0, photoUrls: [] };
    this.eventPhotoInput = '';
    this.eventError = '';
    this.showEventForm = true;
  }

  openEditEvent(event: RunEvent): void {
    this.editingEvent = event;
    const d = new Date(event.eventDate);
    const pad = (n: number) => n.toString().padStart(2, '0');
    this.eventForm = {
      name: event.name,
      description: event.description,
      eventDate: `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`,
      location: event.location,
      distanceKm: event.distanceKm,
      price: event.price,
      maxParticipants: event.maxParticipants,
      photoUrls: event.photoUrls ? [...event.photoUrls] : []
    };
    this.eventPhotoInput = '';
    this.eventError = '';
    this.showEventForm = true;
  }

  saveEvent(): void {
    if (!this.eventForm.name.trim() || !this.eventForm.location.trim()) {
      this.eventError = 'Name and location are required';
      return;
    }
    this.savingEvent = true;
    this.eventError = '';

    if (this.editingEvent) {
      this.communityService.updateCommunityEvent(this.communityId, this.editingEvent.id, this.eventForm).subscribe({
        next: (updated) => {
          const idx = this.communityEvents.findIndex(e => e.id === updated.id);
          if (idx !== -1) this.communityEvents[idx] = updated;
          this.showEventForm = false;
          this.savingEvent = false;
        },
        error: (err) => { this.eventError = err.error?.message || 'Failed to update event'; this.savingEvent = false; }
      });
    } else {
      this.communityService.createCommunityEvent(this.communityId, this.eventForm).subscribe({
        next: (event) => {
          this.communityEvents.unshift(event);
          this.showEventForm = false;
          this.savingEvent = false;
        },
        error: (err) => { this.eventError = err.error?.message || 'Failed to create event'; this.savingEvent = false; }
      });
    }
  }

  cancelEvent(event: RunEvent): void {
    if (!confirm(`Cancel event "${event.name}"?`)) return;
    this.communityService.cancelCommunityEvent(this.communityId, event.id).subscribe({
      next: () => { event.isCancelled = true; },
      error: (err) => this.toast.error(err.error?.message || 'Failed to cancel event')
    });
  }

  formatEventDate(dateStr: string): string {
    return new Date(dateStr).toLocaleDateString([], {
      weekday: 'short', year: 'numeric', month: 'short', day: 'numeric',
      hour: '2-digit', minute: '2-digit'
    });
  }

  // ── Chat Tab Methods ─────────────────────────────────────────────────────────

  loadChat(): void {
    if (this.chatLoaded) return;
    this.chatLoading = true;
    this.chatService.getMessages(this.communityId).subscribe({
      next: (msgs) => { this.chatMessages = msgs; this.chatLoading = false; this.chatLoaded = true; },
      error: () => { this.chatLoading = false; this.chatOffline = true; this.toast.error('Failed to load chat'); }
    });
  }

  chatOffline = false;

  retryChatConnection(): void {
    this.chatOffline = false;
    this.chatLoaded = false;
    this.loadChat();
  }

  onChatKeyDown(e: KeyboardEvent): void {
    if (e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); this.sendChatMessage(); }
  }

  isOwnMessage(msg: Message): boolean {
    return msg.senderUsername === this.currentUsername;
  }

  getRoleBadgeClass(role: string): string {
    switch (role?.toUpperCase()) {
      case 'ADMIN': return 'bg-primary/20 text-primary border-primary/30';
      case 'MODERATOR': return 'bg-blue-500/20 text-blue-400 border-blue-500/30';
      default: return 'bg-muted text-muted-foreground border-border';
    }
  }

  getMemberRole(username: string): string | null {
    const member = this.members.find(m => m.username === username);
    if (!member) return null;
    const role = member.role?.toUpperCase();
    if (role === 'ADMIN') return 'Admin';
    if (role === 'MODERATOR') return 'Crew';
    return null;
  }

  // ── Event Photo helpers ─────────────────────────────────────────────────────

  addEventPhoto(): void {
    const url = this.eventPhotoInput.trim();
    if (!url) return;
    if (!this.eventForm.photoUrls) this.eventForm.photoUrls = [];
    this.eventForm.photoUrls.push(url);
    this.eventPhotoInput = '';
  }

  removeEventPhoto(index: number): void {
    if (this.eventForm.photoUrls) {
      this.eventForm.photoUrls.splice(index, 1);
    }
  }

  // ── Event Detail Modal ──────────────────────────────────────────────────────

  openEventDetail(event: RunEvent): void {
    this.selectedEvent = event;
    this.loadingEventDetail = true;
    this.eventParticipants = [];
    this.eventMessages = [];
    this.galleryPhotos = [];

    this.eventService.getParticipants(event.id).subscribe({
      next: (participants) => { this.eventParticipants = participants; },
      error: () => {}
    });

    this.chatService.getMessages(undefined, event.id).subscribe({
      next: (msgs) => { this.eventMessages = msgs; this.loadingEventDetail = false; },
      error: () => { this.loadingEventDetail = false; }
    });

    this.loadGallery(event.id);
  }

  get galleryPhotoUrls(): string[] {
    return this.galleryPhotos.map(p => p.photoUrl);
  }

  loadGallery(eventId: number): void {
    this.galleryLoading = true;
    this.eventService.getGallery(eventId).subscribe({
      next: (photos) => { this.galleryPhotos = photos; this.galleryLoading = false; },
      error: () => { this.galleryLoading = false; }
    });
  }

  linkDriveToEvent(): void {
    if (!this.selectedEvent || !this.galleryDriveFolderInput.trim()) return;
    this.eventService.linkDriveFolder(this.selectedEvent.id, this.galleryDriveFolderInput.trim()).subscribe({
      next: () => {
        this.selectedEvent!.driveFolderId = this.galleryDriveFolderInput.trim();
        this.showGalleryLinkForm = false;
        this.galleryDriveFolderInput = '';
        this.toast.success('Drive folder linked');
      },
      error: () => this.toast.error('Failed to link Drive folder')
    });
  }

  syncEventGallery(): void {
    if (!this.selectedEvent) return;
    this.gallerySyncing = true;
    this.eventService.syncGallery(this.selectedEvent.id).subscribe({
      next: (res) => {
        this.gallerySyncing = false;
        this.toast.success(`${res.imported} photos synced`);
        this.loadGallery(this.selectedEvent!.id);
      },
      error: () => { this.gallerySyncing = false; this.toast.error('Failed to sync gallery'); }
    });
  }

  uploadGalleryPhoto(): void {
    if (!this.selectedEvent || !this.galleryUploadUrl.trim()) return;
    this.galleryUploading = true;
    this.eventService.addGalleryPhoto(this.selectedEvent.id, this.galleryUploadUrl.trim()).subscribe({
      next: (photo) => {
        this.galleryPhotos.unshift(photo);
        this.galleryUploadUrl = '';
        this.galleryUploading = false;
        this.toast.success('Photo added');
      },
      error: () => { this.galleryUploading = false; this.toast.error('Failed to upload photo'); }
    });
  }

  closeEventDetail(): void {
    this.selectedEvent = null;
  }

  sendEventMessage(): void {
    if (!this.selectedEvent || (!this.eventChatInput.trim() && !this.chatMediaUrl.trim()) || this.sendingEventChat) return;
    this.sendingEventChat = true;
    this.chatService.sendMessage({
      eventId: this.selectedEvent.id,
      content: this.eventChatInput.trim()
    }).subscribe({
      next: (msg) => {
        this.eventMessages.push(msg);
        this.eventChatInput = '';
        this.sendingEventChat = false;
      },
      error: () => { this.sendingEventChat = false; }
    });
  }

  onEventChatKeyDown(e: KeyboardEvent): void {
    if (e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); this.sendEventMessage(); }
  }

  registerForEvent(event: RunEvent): void {
    this.eventService.register(event.id).subscribe({
      next: () => {
        this.eventRegistered[event.id] = true;
        event.participantCount = (event.participantCount || 0) + 1;
      },
      error: (err) => this.toast.error(err.error?.message || 'Failed to register for event')
    });
  }

  // ── Chat Media ──────────────────────────────────────────────────────────────

  sendChatMessage(): void {
    const hasContent = this.chatInput.trim();
    const hasMedia = this.chatMediaUrl.trim();
    if ((!hasContent && !hasMedia) || this.sendingChat) return;
    this.sendingChat = true;
    this.chatService.sendMessage({
      communityId: this.communityId,
      content: this.chatInput.trim(),
      mediaUrl: hasMedia || undefined,
      mediaType: hasMedia ? this.chatMediaType : undefined
    }).subscribe({
      next: (msg) => {
        this.chatMessages.push(msg);
        this.chatInput = '';
        this.chatMediaUrl = '';
        this.showChatMediaInput = false;
        this.sendingChat = false;
      },
      error: () => { this.sendingChat = false; this.toast.error('Message failed to send'); }
    });
  }
}
