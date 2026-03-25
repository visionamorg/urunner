import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Community, CommunityMember, CommunityTag, CreateCommunityRequest, InviteDto, DriveFolderDto, Sponsor } from '../models/community.model';
import { Post, PageResponse } from '../models/post.model';
import { RunEvent, CreateEventRequest, UpdateEventRequest } from '../models/event.model';
import { RoomDto, CreateRoomRequest, RoomMemberDto } from '../models/room.model';
import { Program, ProgramSession, ProgramProgress, EnrolleeProgress, CreateProgramRequest } from '../models/program.model';

@Injectable({ providedIn: 'root' })
export class CommunityService {
  constructor(private http: HttpClient) {}

  getAll(): Observable<Community[]> {
    return this.http.get<Community[]>('/api/communities');
  }

  getOne(id: number): Observable<Community> {
    return this.http.get<Community>(`/api/communities/${id}`);
  }

  getById(id: number): Observable<Community> {
    return this.getOne(id);
  }

  create(data: Partial<Community> | CreateCommunityRequest): Observable<Community> {
    return this.http.post<Community>('/api/communities', data);
  }

  update(id: number, data: Partial<Community>): Observable<Community> {
    return this.http.put<Community>(`/api/communities/${id}`, data);
  }

  join(id: number): Observable<any> {
    return this.http.post<any>(`/api/communities/${id}/join`, {});
  }

  leave(id: number): Observable<any> {
    return this.http.delete<any>(`/api/communities/${id}/leave`);
  }

  getMembers(id: number): Observable<CommunityMember[]> {
    return this.http.get<CommunityMember[]>(`/api/communities/${id}/members`);
  }

  getFeed(id: number, page: number = 0): Observable<PageResponse<Post>> {
    const params = new HttpParams().set('page', page);
    return this.http.get<PageResponse<Post>>(`/api/communities/${id}/feed`, { params });
  }

  createPost(id: number, data: { content: string; postType?: string; photoUrls?: string[] }): Observable<Post> {
    return this.http.post<Post>(`/api/communities/${id}/feed`, data);
  }

  getDriveFolders(id: number): Observable<DriveFolderDto[]> {
    return this.http.get<DriveFolderDto[]>(`/api/communities/${id}/drive/folders`);
  }

  syncDrive(id: number, folderId?: string, folderName?: string): Observable<Post> {
    const body = folderId ? { folderId, folderName } : {};
    return this.http.post<Post>(`/api/communities/${id}/drive/sync`, body);
  }

  // ── Admin: Member Management ──────────────────────────────────────────────

  kickMember(communityId: number, userId: number): Observable<void> {
    return this.http.delete<void>(`/api/communities/${communityId}/members/${userId}`);
  }

  changeMemberRole(communityId: number, userId: number, role: string): Observable<void> {
    return this.http.put<void>(`/api/communities/${communityId}/members/${userId}/role`, { role });
  }

  // ── Admin: Post Management ────────────────────────────────────────────────

  deletePost(communityId: number, postId: number): Observable<void> {
    return this.http.delete<void>(`/api/communities/${communityId}/posts/${postId}`);
  }

  pinPost(communityId: number, postId: number): Observable<void> {
    return this.http.post<void>(`/api/communities/${communityId}/posts/${postId}/pin`, {});
  }

  // ── Tags ──────────────────────────────────────────────────────────────────

  getTags(communityId: number): Observable<CommunityTag[]> {
    return this.http.get<CommunityTag[]>(`/api/communities/${communityId}/tags`);
  }

  createTag(communityId: number, name: string, color: string): Observable<CommunityTag> {
    return this.http.post<CommunityTag>(`/api/communities/${communityId}/tags`, { name, color });
  }

  deleteTag(communityId: number, tagId: number): Observable<void> {
    return this.http.delete<void>(`/api/communities/${communityId}/tags/${tagId}`);
  }

  assignTag(communityId: number, userId: number, tagId: number): Observable<void> {
    return this.http.post<void>(`/api/communities/${communityId}/members/${userId}/tags/${tagId}`, {});
  }

  removeTagFromMember(communityId: number, userId: number, tagId: number): Observable<void> {
    return this.http.delete<void>(`/api/communities/${communityId}/members/${userId}/tags/${tagId}`);
  }

  batchNotifyInactive(communityId: number, userIds: number[]): Observable<void> {
    return this.http.post<void>(`/api/communities/${communityId}/members/batch-notify`, { userIds });
  }

  batchKickMembers(communityId: number, userIds: number[]): Observable<void> {
    return this.http.post<void>(`/api/communities/${communityId}/members/batch-kick`, { userIds });
  }

  // ── Invites ───────────────────────────────────────────────────────────────

  invite(communityId: number, username: string): Observable<InviteDto> {
    return this.http.post<InviteDto>(`/api/communities/${communityId}/invites`, { username });
  }

  getCommunityInvites(communityId: number): Observable<InviteDto[]> {
    return this.http.get<InviteDto[]>(`/api/communities/${communityId}/invites`);
  }

  cancelInvite(communityId: number, inviteId: number): Observable<void> {
    return this.http.delete<void>(`/api/communities/${communityId}/invites/${inviteId}`);
  }

  getMyInvites(): Observable<InviteDto[]> {
    return this.http.get<InviteDto[]>('/api/communities/invites/mine');
  }

  acceptInvite(token: string): Observable<void> {
    return this.http.post<void>(`/api/communities/invites/${token}/accept`, {});
  }

  declineInvite(token: string): Observable<void> {
    return this.http.post<void>(`/api/communities/invites/${token}/decline`, {});
  }

  // ── Community Events ───────────────────────────────────────────────────────

  getCommunityEvents(communityId: number): Observable<RunEvent[]> {
    return this.http.get<RunEvent[]>(`/api/communities/${communityId}/events`);
  }

  createCommunityEvent(communityId: number, data: CreateEventRequest): Observable<RunEvent> {
    return this.http.post<RunEvent>(`/api/communities/${communityId}/events`, data);
  }

  updateCommunityEvent(communityId: number, eventId: number, data: UpdateEventRequest): Observable<RunEvent> {
    return this.http.put<RunEvent>(`/api/communities/${communityId}/events/${eventId}`, data);
  }

  cancelCommunityEvent(communityId: number, eventId: number): Observable<void> {
    return this.http.delete<void>(`/api/communities/${communityId}/events/${eventId}`);
  }

  // ── Leaderboard ─────────────────────────────────────────────────────────────

  getCommunityRankings(communityId: number, metric: string = 'distance'): Observable<any[]> {
    const params = new HttpParams().set('metric', metric);
    return this.http.get<any[]>(`/api/rankings/community/${communityId}`, { params });
  }

  getCommunityWeeklyChallenge(communityId: number): Observable<any[]> {
    return this.http.get<any[]>(`/api/rankings/community/${communityId}/weekly`);
  }

  generateWeeklyDigest(communityId: number): Observable<Post> {
    return this.http.post<Post>(`/api/communities/${communityId}/digest`, {});
  }

  // ── Rooms ──────────────────────────────────────────────────────────────────

  getRooms(communityId: number): Observable<RoomDto[]> {
    return this.http.get<RoomDto[]>(`/api/communities/${communityId}/rooms`);
  }

  createRoom(communityId: number, data: CreateRoomRequest): Observable<RoomDto> {
    return this.http.post<RoomDto>(`/api/communities/${communityId}/rooms`, data);
  }

  deleteRoom(communityId: number, roomId: number): Observable<void> {
    return this.http.delete<void>(`/api/communities/${communityId}/rooms/${roomId}`);
  }

  getRoomMembers(communityId: number, roomId: number): Observable<RoomMemberDto[]> {
    return this.http.get<RoomMemberDto[]>(`/api/communities/${communityId}/rooms/${roomId}/members`);
  }

  addRoomMember(communityId: number, roomId: number, userId: number): Observable<void> {
    return this.http.post<void>(`/api/communities/${communityId}/rooms/${roomId}/members`, { userId });
  }

  removeRoomMember(communityId: number, roomId: number, userId: number): Observable<void> {
    return this.http.delete<void>(`/api/communities/${communityId}/rooms/${roomId}/members/${userId}`);
  }

  // ── Community Programmes ──────────────────────────────────────────────────

  getCommunityPrograms(communityId: number): Observable<Program[]> {
    return this.http.get<Program[]>(`/api/communities/${communityId}/programs`);
  }

  createCommunityProgram(communityId: number, data: CreateProgramRequest): Observable<Program> {
    return this.http.post<Program>(`/api/communities/${communityId}/programs`, data);
  }

  deleteCommunityProgram(communityId: number, programId: number): Observable<void> {
    return this.http.delete<void>(`/api/communities/${communityId}/programs/${programId}`);
  }

  getProgramSessions(communityId: number, programId: number): Observable<ProgramSession[]> {
    return this.http.get<ProgramSession[]>(`/api/communities/${communityId}/programs/${programId}/sessions`);
  }

  addProgramSession(communityId: number, programId: number, session: Partial<ProgramSession>): Observable<ProgramSession> {
    return this.http.post<ProgramSession>(`/api/communities/${communityId}/programs/${programId}/sessions`, session);
  }

  enrollInProgram(communityId: number, programId: number): Observable<ProgramProgress> {
    return this.http.post<ProgramProgress>(`/api/communities/${communityId}/programs/${programId}/enroll`, {});
  }

  getProgramEnrollees(communityId: number, programId: number): Observable<EnrolleeProgress[]> {
    return this.http.get<EnrolleeProgress[]>(`/api/communities/${communityId}/programs/${programId}/enrollees`);
  }

  completeProgramSession(communityId: number, programId: number): Observable<ProgramProgress> {
    return this.http.post<ProgramProgress>(`/api/communities/${communityId}/programs/${programId}/complete-session`, {});
  }

  // Sponsors
  addSponsor(communityId: number, sponsor: Sponsor): Observable<Sponsor> {
    return this.http.post<Sponsor>(`/api/communities/${communityId}/sponsors`, sponsor);
  }

  removeSponsor(communityId: number, sponsorId: number): Observable<void> {
    return this.http.delete<void>(`/api/communities/${communityId}/sponsors/${sponsorId}`);
  }
}
