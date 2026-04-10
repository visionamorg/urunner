import { Component, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

interface Message {
  role: 'user' | 'ai';
  content: string;
  loading?: boolean;
}

@Component({
  selector: 'app-ai-coach',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="max-w-3xl mx-auto flex flex-col h-[calc(100vh-8rem)]">
      <div class="flex items-center justify-between mb-4">
        <div>
          <h1 class="text-2xl font-bold text-foreground">AI Coach</h1>
          <p class="text-sm text-muted-foreground">Powered by AI — knows your training history</p>
        </div>
        <button (click)="newConversation()" class="px-3 py-1.5 text-sm border border-border rounded-lg hover:bg-secondary text-muted-foreground transition-colors">
          New chat
        </button>
      </div>

      <!-- Messages -->
      <div #messagesContainer class="flex-1 overflow-y-auto space-y-4 mb-4 pr-1">
        <div *ngIf="messages.length === 0" class="flex flex-col items-center justify-center h-full text-center">
          <span class="material-icons text-5xl text-primary mb-3">psychology</span>
          <h2 class="text-lg font-semibold text-foreground mb-1">Your AI Running Coach</h2>
          <p class="text-muted-foreground text-sm max-w-sm">Ask me anything about your training, recovery, race prep, or workouts.</p>
        </div>

        <div *ngFor="let msg of messages"
          [class]="msg.role === 'user' ? 'flex justify-end' : 'flex justify-start'">
          <div [ngClass]="msg.role === 'user'
            ? 'bg-primary text-white rounded-2xl rounded-tr-sm px-4 py-3 max-w-[80%]'
            : 'bg-card border border-border text-foreground rounded-2xl rounded-tl-sm px-4 py-3 max-w-[80%]'">
            <div *ngIf="msg.loading" class="flex gap-1 items-center py-1">
              <span class="w-2 h-2 bg-muted-foreground rounded-full animate-bounce [animation-delay:-0.3s]"></span>
              <span class="w-2 h-2 bg-muted-foreground rounded-full animate-bounce [animation-delay:-0.15s]"></span>
              <span class="w-2 h-2 bg-muted-foreground rounded-full animate-bounce"></span>
            </div>
            <p *ngIf="!msg.loading" class="text-sm whitespace-pre-wrap">{{ msg.content }}</p>
          </div>
        </div>
      </div>

      <!-- Quick prompts -->
      <div class="flex flex-wrap gap-2 mb-3">
        <button *ngFor="let chip of chips" (click)="send(chip)"
          class="px-3 py-1.5 text-xs border border-border rounded-full text-muted-foreground hover:border-primary hover:text-primary transition-colors">
          {{ chip }}
        </button>
      </div>

      <!-- Input -->
      <div class="flex gap-3 items-end">
        <textarea
          [(ngModel)]="inputMessage"
          (keydown.enter)="$event.preventDefault(); sendMessage()"
          placeholder="Ask your coach anything..."
          rows="1"
          class="flex-1 resize-none input-field rounded-xl py-3 text-sm"></textarea>
        <button (click)="sendMessage()" [disabled]="!inputMessage.trim() || loading"
          class="btn-primary px-4 py-3 rounded-xl disabled:opacity-50">
          <span class="material-icons text-lg">send</span>
        </button>
      </div>
    </div>
  `
})
export class AiCoachComponent implements AfterViewChecked {
  @ViewChild('messagesContainer') messagesContainer!: ElementRef;

  messages: Message[] = [];
  inputMessage = '';
  loading = false;
  chips = ['Analyze my last 4 weeks', 'Suggest a workout for today', 'Am I ready to race?', 'How do I improve my 10K pace?', 'Recovery day tips'];

  constructor(private http: HttpClient) {}

  ngAfterViewChecked(): void {
    this.scrollToBottom();
  }

  send(text: string): void {
    this.inputMessage = text;
    this.sendMessage();
  }

  sendMessage(): void {
    const text = this.inputMessage.trim();
    if (!text || this.loading) return;
    this.messages.push({ role: 'user', content: text });
    this.inputMessage = '';
    this.loading = true;
    const loadingMsg: Message = { role: 'ai', content: '', loading: true };
    this.messages.push(loadingMsg);

    this.http.post<any>('/api/ai/chat', { message: text }).subscribe({
      next: res => {
        loadingMsg.loading = false;
        loadingMsg.content = res.reply || res.response || res.message || 'No response from coach.';
        this.loading = false;
      },
      error: () => {
        loadingMsg.loading = false;
        loadingMsg.content = "I'm having trouble connecting right now. Please try again.";
        this.loading = false;
      }
    });
  }

  newConversation(): void {
    this.messages = [];
    this.inputMessage = '';
  }

  private scrollToBottom(): void {
    try {
      this.messagesContainer.nativeElement.scrollTop = this.messagesContainer.nativeElement.scrollHeight;
    } catch(e) {}
  }
}
