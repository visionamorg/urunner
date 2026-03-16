export interface Post {
  id: number;
  authorId: number;
  authorUsername: string;
  authorProfileImageUrl?: string;
  content: string;
  imageUrl?: string;
  communityId?: number;
  likesCount: number;
  commentsCount: number;
  createdAt: string;
  likedByCurrentUser: boolean;
}

export interface Comment {
  id: number;
  postId: number;
  authorId: number;
  authorUsername: string;
  content: string;
  createdAt: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}
