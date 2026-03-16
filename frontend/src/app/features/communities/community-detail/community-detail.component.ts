import { Component, OnInit, OnDestroy, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { CommunityService } from '../../../core/services/community.service';
import { FeedService } from '../../../core/services/feed.service';
import { ChatService } from '../../../core/services/chat.service';
import { AuthService } from '../../../core/services/auth.service';
import { Community, CommunityMember, InviteDto, DriveFolderDto } from '../../../core/models/community.model';
import { Post, Comment } from '../../../core/models/post.model';
import { RunEvent, CreateEventRequest, UpdateEventRequest } from '../../../core/models/event.model';
import { Message } from '../../../core/models/message.model';
import { CommunityCalendarComponent } from '../community-calendar/community-calendar.component';
import { CommunityRoomsComponent } from '../community-rooms/community-rooms.component';

@Component({
  selector: 'app-community-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, CommunityCalendarComponent, CommunityRoomsComponent],
  templateUrl: './community-detail.component.html',
  styleUrl: './community-detail.component.scss'
})
export class CommunityDetailComponent implements OnInit, OnDestroy {
  community: Community | null = null;
  posts: Post[] = [];
  members: CommunityMember[] = [];
  invites: InviteDto[] = [];
  activeTab: 'feed' | 'members' | 'invites' | 'settings' | 'events' | 'calendar' | 'chat' | 'rooms' = 'feed';

  // ── Events Tab ─────────────────────────────────────────────────────────────
  communityEvents: RunEvent[] = [];
  eventsLoading = false;
  eventsLoaded = false;
  showEventForm = false;
  editingEvent: RunEvent | null = null;
  eventForm: CreateEventRequest = { name: '', description: '', eventDate: '', location: '', distanceKm: 0, price: 0 };
  savingEvent = false;
  eventError = '';

  // ── Chat Tab ───────────────────────────────────────────────────────────────
  chatMessages: Message[] = [];
  chatInput = '';
  chatLoading = false;
  chatLoaded = false;
  sendingChat = false;
  currentUsername = '';
  loading = true;
  feedLoading = false;
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
  private touchStartX = 0;

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
  settingsSaving = false;
  settingsSuccess = false;

  // Role change state
  changingRole: { [userId: number]: boolean } = {};

  communityId!: number;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private communityService: CommunityService,
    private feedService: FeedService,
    private chatService: ChatService,
    private authService: AuthService
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
        this.loading = false;
        this.loadFeed();
        this.loadMembers();
        if (c.isAdmin) this.loadInvites();
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

  loadInvites(): void {
    this.communityService.getCommunityInvites(this.communityId).subscribe({
      next: (invites) => { this.invites = invites; },
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
      error: (err) => alert(err.error?.message || 'Failed to delete post')
    });
  }

  pinPost(post: Post): void {
    this.communityService.pinPost(this.communityId, post.id).subscribe({
      next: () => { post.pinned = !post.pinned; },
      error: (err) => alert(err.error?.message || 'Failed to pin post')
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
      error: (err) => alert(err.error?.message || 'Failed to kick member')
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
        alert(err.error?.message || 'Failed to change role');
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
      error: (err) => alert(err.error?.message || 'Failed to cancel invite')
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

  prevPhoto(): void {
    this.lightboxIndex = (this.lightboxIndex - 1 + this.lightboxPhotos.length) % this.lightboxPhotos.length;
  }

  nextPhoto(): void {
    this.lightboxIndex = (this.lightboxIndex + 1) % this.lightboxPhotos.length;
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

  loadEvents(): void {
    if (this.eventsLoaded) return;
    this.eventsLoading = true;
    this.communityService.getCommunityEvents(this.communityId).subscribe({
      next: (events) => { this.communityEvents = events; this.eventsLoading = false; this.eventsLoaded = true; },
      error: () => { this.eventsLoading = false; }
    });
  }

  openCreateEvent(): void {
    this.editingEvent = null;
    this.eventForm = { name: '', description: '', eventDate: '', location: '', distanceKm: 0, price: 0 };
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
      maxParticipants: event.maxParticipants
    };
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
      error: (err) => alert(err.error?.message || 'Failed to cancel event')
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
      error: () => { this.chatLoading = false; }
    });
  }

  sendChatMessage(): void {
    if (!this.chatInput.trim() || this.sendingChat) return;
    this.sendingChat = true;
    this.chatService.sendMessage({ communityId: this.communityId, content: this.chatInput.trim() }).subscribe({
      next: (msg) => { this.chatMessages.push(msg); this.chatInput = ''; this.sendingChat = false; },
      error: () => { this.sendingChat = false; }
    });
  }

  onChatKeyDown(e: KeyboardEvent): void {
    if (e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); this.sendChatMessage(); }
  }

  isOwnMessage(msg: Message): boolean {
    return msg.senderUsername === this.currentUsername;
  }

  getRoleBadgeClass(role: string): string {
    switch (role?.toUpperCase()) {
      case 'ADMIN': return 'bg-orange-500/20 text-orange-400 border-orange-500/30';
      case 'MODERATOR': return 'bg-blue-500/20 text-blue-400 border-blue-500/30';
      default: return 'bg-slate-500/20 text-slate-400 border-slate-500/30';
    }
  }
}
