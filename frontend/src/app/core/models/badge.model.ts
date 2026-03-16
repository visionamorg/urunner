export interface Badge {
  id: number;
  name: string;
  description: string;
  iconUrl?: string;
  criteria: string;
  createdAt: string;
}

export interface UserBadge {
  badgeId: number;
  badgeName: string;
  badgeDescription: string;
  badgeIconUrl?: string;
  earnedAt: string;
}
