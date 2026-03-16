export interface RoomDto {
  id: number;
  name: string;
  description?: string;
  communityId: number;
  createdByUsername: string;
  isPrivate: boolean;
  createdAt: string;
  memberCount: number;
}

export interface RoomMemberDto {
  userId: number;
  username: string;
  firstName: string;
  lastName: string;
  profileImageUrl?: string;
  initials?: string;
  role: string;
  joinedAt: string;
}

export interface CreateRoomRequest {
  name: string;
  description?: string;
  isPrivate: boolean;
}
