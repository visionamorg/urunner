export interface Message {
  id: number;
  senderId: number;
  senderUsername: string;
  communityId?: number;
  eventId?: number;
  roomId?: number;
  content: string;
  sentAt: string;
}

export interface SendMessageRequest {
  communityId?: number;
  eventId?: number;
  roomId?: number;
  content: string;
}
