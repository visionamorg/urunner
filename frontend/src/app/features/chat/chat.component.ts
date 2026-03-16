import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatDividerModule } from '@angular/material/divider';
import { ChatService } from '../../core/services/chat.service';
import { CommunityService } from '../../core/services/community.service';
import { Community } from '../../core/models/community.model';
import { Message } from '../../core/models/message.model';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule,
    MatCardModule, MatButtonModule, MatIconModule,
    MatFormFieldModule, MatInputModule, MatListModule, MatDividerModule
  ],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss'
})
export class ChatComponent implements OnInit {
  communities: Community[] = [];
  selectedCommunity: Community | null = null;
  messages: Message[] = [];
  form: FormGroup;
  currentUser = this.authService.getCurrentUser();

  constructor(
    private chatService: ChatService,
    private communityService: CommunityService,
    private authService: AuthService,
    private fb: FormBuilder
  ) {
    this.form = this.fb.group({ content: ['', Validators.required] });
  }

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
    if (this.form.invalid || !this.selectedCommunity) return;
    const content = this.form.value.content;
    this.chatService.sendMessage({ communityId: this.selectedCommunity.id, content }).subscribe({
      next: msg => {
        this.messages.push(msg);
        this.form.reset();
      }
    });
  }

  isOwnMessage(msg: Message): boolean {
    return msg.senderUsername === this.currentUser?.username;
  }

  timeFormat(dateStr: string): string {
    return new Date(dateStr).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  }
}
