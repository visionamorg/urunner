export interface Community {
  id: number;
  name: string;
  description: string;
  imageUrl?: string;
  creatorId: number;
  creatorUsername: string;
  memberCount: number;
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
}

export interface CreateCommunityRequest {
  name: string;
  description: string;
  imageUrl?: string;
}
