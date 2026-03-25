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
  pendingInviteCount?: number;
  isPremium?: boolean;
  stripePaymentUrl?: string;
  sponsors?: Sponsor[];
}

export interface Sponsor {
  id?: number;
  logoUrl: string;
  linkUrl?: string;
  name?: string;
}

export interface DriveFolderDto {
  id: string;
  name: string;
  imageCount: number;
}

export interface InviteDto {
  id: number;
  communityId: number;
  communityName: string;
  communityImageUrl?: string;
  invitedUserId: number;
  invitedUsername: string;
  invitedByUsername: string;
  token: string;
  status: string;
  createdAt: string;
  expiresAt: string;
}

export interface CommunityTag {
  id: number;
  name: string;
  color: string;
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
  tags?: CommunityTag[];
  lastRunDate?: string;
  messageCount30d?: number;
}

export interface CreateCommunityRequest {
  name: string;
  description: string;
  imageUrl?: string;
  coverUrl?: string;
  driveFolderId?: string;
}
