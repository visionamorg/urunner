export interface RunEvent {
  id: number;
  name: string;
  description: string;
  eventDate: string;
  location: string;
  distanceKm: number;
  price: number;
  maxParticipants: number;
  organizerId: number;
  organizerUsername: string;
  communityId?: number;
  communityName?: string;
  participantCount: number;
  isCancelled?: boolean;
  createdAt: string;
}

export interface CreateEventRequest {
  name: string;
  description: string;
  eventDate: string;
  location: string;
  distanceKm: number;
  price: number;
  maxParticipants?: number;
  communityId?: number;
}

export interface UpdateEventRequest {
  name?: string;
  description?: string;
  eventDate?: string;
  location?: string;
  distanceKm?: number;
  price?: number;
  maxParticipants?: number;
}
