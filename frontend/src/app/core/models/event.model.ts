export interface GalleryPhoto {
  id: number;
  photoUrl: string;
  thumbnailUrl: string;
  uploadedByUsername?: string;
  bibNumber?: string;
  taggedUsername?: string;
  createdAt: string;
}

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
  photoUrls?: string[];
  driveFolderId?: string;
  galleryCount?: number;
  routeGpxUrl?: string;
  elevationGainMeters?: number;
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
  photoUrls?: string[];
}

export interface UpdateEventRequest {
  name?: string;
  description?: string;
  eventDate?: string;
  location?: string;
  distanceKm?: number;
  price?: number;
  maxParticipants?: number;
  photoUrls?: string[];
}
