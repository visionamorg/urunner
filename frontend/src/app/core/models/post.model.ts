export interface Post {
  id: number;
  authorId: number;
  authorUsername: string;
  authorProfileImageUrl?: string;
  authorInitials: string;
  communityId?: number;
  postType: 'TEXT' | 'PHOTO_ALBUM';
  content: string;
  imageUrl?: string;
  photoUrls: string[];
  likesCount: number;
  commentsCount: number;
  liked: boolean;
  likedByCurrentUser: boolean;
  pinned: boolean;
  deleted?: boolean;
  createdAt: string;
  reactions?: { [emoji: string]: number };
  myReaction?: string | null;
  comments?: Comment[];
}

export interface Comment {
  id: number;
  postId: number;
  authorId: number;
  authorUsername: string;
  authorInitials: string;
  content: string;
  createdAt: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  last: boolean;
}
