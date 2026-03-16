export interface Community {
  id: number;
  name: string;
  description: string;
  imageUrl?: string;
  coverUrl?: string;
  driveFolderId?: string;
  isPrivate?: boolean;
  creatorId: number;
  creatorUsername: string;
  memberCount: number;
  joined: boolean;
  role?: string;
  isAdmin: boolean;
  createdAt: string;
}

export interface CommunityMember {
  userId: number;
  username: string;
  firstName: string;
  lastName: string;
  profileImageUrl?: string;
  role: string;
  joinedAt: string;
  initials: string;
}

export interface CreateCommunityRequest {
  name: string;
  description: string;
  imageUrl?: string;
  coverUrl?: string;
  driveFolderId?: string;
}
