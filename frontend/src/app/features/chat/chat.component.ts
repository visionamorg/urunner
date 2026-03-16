import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ChatService } from '../../core/services/chat.service';
import { CommunityService } from '../../core/services/community.service';
import { Community } from '../../core/models/community.model';
import { Message } from '../../core/models/message.model';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss'
})
export class ChatComponent implements OnInit {
  communities: Community[] = [];
  selectedCommunity: Community | null = null;
  messages: Message[] = [];
  messageContent = '';
  currentUser = this.authService.getCurrentUser();

  constructor(
    private chatService: ChatService,
    private communityService: CommunityService,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.communityService.getAll().subscribe(c => {
      this.communities = c;
      if (c.length > 0) this.selectCommunity(c[0]);
    });
  }

  selectCommunity(community: Community): void {
    this.selectedCommunity = community;
    this.chatService.getMessages(community.id).subscribe(m => this.messages = m);
  }

  send(): void {
    if (!this.messageContent.trim() || !this.selectedCommunity) return;
    const content = this.messageContent;
    this.chatService.sendMessage({ communityId: this.selectedCommunity.id, content }).subscribe({
      next: msg => {
        this.messages.push(msg);
        this.messageContent = '';
      }
    });
  }

  isOwnMessage(msg: Message): boolean {
    return msg.senderUsername === this.currentUser?.username;
  }

  timeFormat(dateStr: string): string {
    return new Date(dateStr).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  }

  getInitials(username: string): string {
    return username.substring(0, 2).toUpperCase();
  }

  onEnter(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.send();
    }
  }
}
