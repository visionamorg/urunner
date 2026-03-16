import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RoomDto, RoomMemberDto, CreateRoomRequest } from '../../../core/models/room.model';
import { Message } from '../../../core/models/message.model';
import { CommunityMember } from '../../../core/models/community.model';
import { CommunityService } from '../../../core/services/community.service';
import { ChatService } from '../../../core/services/chat.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-community-rooms',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './community-rooms.component.html',
  styleUrl: './community-rooms.component.scss'
})
export class CommunityRoomsComponent implements OnInit {
  @Input() communityId!: number;
  @Input() isAdmin = false;
  @Input() members: CommunityMember[] = [];

  rooms: RoomDto[] = [];
  selectedRoom: RoomDto | null = null;
  roomMessages: Message[] = [];
  roomMembers: RoomMemberDto[] = [];
  messageInput = '';
  loadingRooms = false;
  loadingMessages = false;
  sendingMessage = false;

  // Create room form
  showCreateForm = false;
  newRoom: CreateRoomRequest = { name: '', description: '', isPrivate: true };
  creating = false;
  createError = '';

  // Member manager
  showMemberManager = false;
  addMemberUserId: number | null = null;
  memberError = '';

  currentUsername = '';

  constructor(
    private communityService: CommunityService,
    private chatService: ChatService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.currentUsername = this.authService.getCurrentUser()?.username || '';
    this.loadRooms();
  }

  loadRooms(): void {
    this.loadingRooms = true;
    this.communityService.getRooms(this.communityId).subscribe({
      next: (rooms) => { this.rooms = rooms; this.loadingRooms = false; },
      error: () => { this.loadingRooms = false; }
    });
  }

  selectRoom(room: RoomDto): void {
    this.selectedRoom = room;
    this.roomMessages = [];
    this.roomMembers = [];
    this.showMemberManager = false;
    this.loadMessages(room.id);
    this.loadRoomMembers(room.id);
  }

  loadMessages(roomId: number): void {
    this.loadingMessages = true;
    this.chatService.getMessages(undefined, undefined, roomId).subscribe({
      next: (msgs) => { this.roomMessages = msgs; this.loadingMessages = false; },
      error: () => { this.loadingMessages = false; }
    });
  }

  loadRoomMembers(roomId: number): void {
    this.communityService.getRoomMembers(this.communityId, roomId).subscribe({
      next: (members) => { this.roomMembers = members; },
      error: () => {}
    });
  }

  sendMessage(): void {
    if (!this.messageInput.trim() || !this.selectedRoom || this.sendingMessage) return;
    this.sendingMessage = true;

    this.chatService.sendMessage({ roomId: this.selectedRoom.id, content: this.messageInput.trim() }).subscribe({
      next: (msg) => {
        this.roomMessages.push(msg);
        this.messageInput = '';
        this.sendingMessage = false;
      },
      error: () => { this.sendingMessage = false; }
    });
  }

  onKeyDown(e: KeyboardEvent): void {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      this.sendMessage();
    }
  }

  // ── Admin: Room Management ──────────────────────────────────────────────────

  createRoom(): void {
    if (!this.newRoom.name.trim()) { this.createError = 'Name is required'; return; }
    this.creating = true;
    this.createError = '';

    this.communityService.createRoom(this.communityId, this.newRoom).subscribe({
      next: (room) => {
        this.rooms.unshift(room);
        this.newRoom = { name: '', description: '', isPrivate: true };
        this.showCreateForm = false;
        this.creating = false;
      },
      error: (err) => {
        this.createError = err.error?.message || 'Failed to create room';
        this.creating = false;
      }
    });
  }

  deleteRoom(room: RoomDto): void {
    if (!confirm(`Delete room "${room.name}"? This cannot be undone.`)) return;
    this.communityService.deleteRoom(this.communityId, room.id).subscribe({
      next: () => {
        this.rooms = this.rooms.filter(r => r.id !== room.id);
        if (this.selectedRoom?.id === room.id) {
          this.selectedRoom = null;
          this.roomMessages = [];
        }
      },
      error: (err) => alert(err.error?.message || 'Failed to delete room')
    });
  }

  addMember(): void {
    if (!this.addMemberUserId || !this.selectedRoom) return;
    this.memberError = '';
    this.communityService.addRoomMember(this.communityId, this.selectedRoom.id, this.addMemberUserId).subscribe({
      next: () => {
        this.addMemberUserId = null;
        this.loadRoomMembers(this.selectedRoom!.id);
        if (this.selectedRoom) this.selectedRoom.memberCount++;
      },
      error: (err) => { this.memberError = err.error?.message || 'Failed to add member'; }
    });
  }

  removeMember(userId: number): void {
    if (!this.selectedRoom) return;
    this.communityService.removeRoomMember(this.communityId, this.selectedRoom.id, userId).subscribe({
      next: () => {
        this.roomMembers = this.roomMembers.filter(m => m.userId !== userId);
        if (this.selectedRoom) this.selectedRoom.memberCount = Math.max(0, this.selectedRoom.memberCount - 1);
      },
      error: (err) => alert(err.error?.message || 'Failed to remove member')
    });
  }

  isOwnMessage(msg: Message): boolean {
    return msg.senderUsername === this.currentUsername;
  }

  isAlreadyRoomMember(userId: number): boolean {
    return this.roomMembers.some(rm => rm.userId === userId);
  }

  timeAgo(dateStr: string): string {
    const date = new Date(dateStr);
    const diff = Math.floor((Date.now() - date.getTime()) / 1000);
    if (diff < 60) return 'just now';
    if (diff < 3600) return `${Math.floor(diff / 60)}m ago`;
    if (diff < 86400) return `${Math.floor(diff / 3600)}h ago`;
    return date.toLocaleDateString();
  }

  getInitials(name: string): string {
    if (!name) return '?';
    return name.substring(0, 2).toUpperCase();
  }
}
