package com.runhub.communities.service;

import com.runhub.communities.dto.*;
import com.runhub.communities.dto.DriveFolderDto;
import com.runhub.communities.mapper.CommunityMapper;
import com.runhub.communities.model.Community;
import com.runhub.communities.model.CommunityInvite;
import com.runhub.communities.model.CommunityMember;
import com.runhub.communities.model.CommunityMemberId;
import com.runhub.communities.model.CommunityTag;
import com.runhub.communities.model.MemberTag;
import com.runhub.communities.repository.*;
import com.runhub.notifications.service.NotificationService;
import com.runhub.config.BadRequestException;
import com.runhub.config.ResourceNotFoundException;
import com.runhub.chat.repository.MessageRepository;
import com.runhub.feed.dto.PostDto;
import com.runhub.feed.repository.PostRepository;
import com.runhub.feed.service.FeedService;
import com.runhub.running.repository.ActivityRepository;
import com.runhub.users.model.User;
import com.runhub.users.repository.UserRepository;
import com.runhub.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final CommunityInviteRepository communityInviteRepository;
    private final CommunityMapper communityMapper;
    private final UserService userService;
    private final UserRepository userRepository;
    private final GoogleDriveService googleDriveService;
    private final FeedService feedService;
    private final PostRepository postRepository;
    private final NotificationService notificationService;
    private final CommunityTagRepository tagRepository;
    private final MemberTagRepository memberTagRepository;
    private final ActivityRepository activityRepository;
    private final MessageRepository messageRepository;
    private final CommunitySponsorRepository sponsorRepository;

    // ── List / Get ──────────────────────────────────────────────────────────

    public List<CommunityDto> getAllCommunities(User user) {
        return communityRepository.findAllByOrderByMemberCountDesc()
                .stream().map(c -> toDtoWithMembership(c, user)).toList();
    }

    public List<CommunityDto> getAllCommunities() {
        return communityRepository.findAllByOrderByMemberCountDesc()
                .stream().map(communityMapper::toDto).toList();
    }

    public CommunityDto getCommunity(Long id, User user) {
        return toDtoWithMembership(findById(id), user);
    }

    public CommunityDto getCommunityById(Long id) {
        return communityMapper.toDto(findById(id));
    }

    // ── Create / Update ─────────────────────────────────────────────────────

    @Transactional
    public CommunityDto createCommunity(CreateCommunityRequest request, User user) {
        Community community = Community.builder()
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .coverUrl(request.getCoverUrl())
                .driveFolderId(request.getDriveFolderId())
                .creator(user)
                .memberCount(1)
                .build();
        community = communityRepository.save(community);

        CommunityMember member = CommunityMember.builder()
                .id(new CommunityMemberId(community.getId(), user.getId()))
                .community(community).user(user).role("ADMIN").build();
        communityMemberRepository.save(member);

        CommunityDto dto = communityMapper.toDto(community);
        dto.setJoined(true);
        dto.setRole("ADMIN");
        dto.setAdmin(true);
        return dto;
    }

    @Transactional
    public CommunityDto createCommunity(String email, CreateCommunityRequest request) {
        return createCommunity(request, userService.getUserEntityByEmail(email));
    }

    @Transactional
    public CommunityDto updateCommunity(Long id, UpdateCommunityRequest request, User user) {
        Community community = findById(id);
        requireAdmin(id, user, community);

        if (request.getName() != null && !request.getName().isBlank())
            community.setName(request.getName());
        if (request.getDescription() != null)
            community.setDescription(request.getDescription());
        if (request.getDriveFolderId() != null)
            community.setDriveFolderId(request.getDriveFolderId());
        if (request.getCoverUrl() != null)
            community.setCoverUrl(request.getCoverUrl());
        if (request.getImageUrl() != null)
            community.setImageUrl(request.getImageUrl());
        if (request.getIsPremium() != null)
            community.setIsPremium(request.getIsPremium());
        if (request.getStripePaymentUrl() != null)
            community.setStripePaymentUrl(request.getStripePaymentUrl().isBlank() ? null : request.getStripePaymentUrl());

        return toDtoWithMembership(communityRepository.save(community), user);
    }

    // ── Join / Leave ─────────────────────────────────────────────────────────

    @Transactional
    public void joinCommunity(Long communityId, User user) {
        Community community = findById(communityId);
        if (communityMemberRepository.existsByIdCommunityIdAndIdUserId(communityId, user.getId()))
            throw new BadRequestException("Already a member of this community");

        communityMemberRepository.save(CommunityMember.builder()
                .id(new CommunityMemberId(communityId, user.getId()))
                .community(community).user(user).role("MEMBER").build());
        community.setMemberCount(community.getMemberCount() + 1);
        communityRepository.save(community);
    }

    @Transactional
    public void joinCommunity(Long communityId, String email) {
        joinCommunity(communityId, userService.getUserEntityByEmail(email));
    }

    @Transactional
    public void leaveCommunity(Long communityId, User user) {
        Community community = findById(communityId);
        if (!communityMemberRepository.existsByIdCommunityIdAndIdUserId(communityId, user.getId()))
            throw new BadRequestException("You are not a member of this community");
        if (community.getCreator().getId().equals(user.getId()))
            throw new BadRequestException("Community creator cannot leave. Transfer ownership first.");

        communityMemberRepository.deleteById(new CommunityMemberId(communityId, user.getId()));
        community.setMemberCount(Math.max(0, community.getMemberCount() - 1));
        communityRepository.save(community);
    }

    // ── Admin: Member Management ─────────────────────────────────────────────

    @Transactional
    public void kickMember(Long communityId, Long targetUserId, User admin) {
        Community community = findById(communityId);
        requireAdmin(communityId, admin, community);

        if (community.getCreator().getId().equals(targetUserId))
            throw new BadRequestException("Cannot kick the community creator");
        if (!communityMemberRepository.existsByIdCommunityIdAndIdUserId(communityId, targetUserId))
            throw new BadRequestException("User is not a member of this community");

        communityMemberRepository.deleteById(new CommunityMemberId(communityId, targetUserId));
        community.setMemberCount(Math.max(0, community.getMemberCount() - 1));
        communityRepository.save(community);
    }

    @Transactional
    public void changeMemberRole(Long communityId, Long targetUserId, String newRole, User admin) {
        Community community = findById(communityId);
        requireAdmin(communityId, admin, community);

        if (!List.of("ADMIN", "MODERATOR", "MEMBER").contains(newRole.toUpperCase()))
            throw new BadRequestException("Invalid role. Must be ADMIN, MODERATOR, or MEMBER");

        CommunityMember member = communityMemberRepository
                .findByCommunityId(communityId).stream()
                .filter(m -> m.getUser().getId().equals(targetUserId))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("User is not a member"));

        member.setRole(newRole.toUpperCase());
        communityMemberRepository.save(member);
    }

    @Transactional
    public void deletePost(Long communityId, Long postId, User admin) {
        Community community = findById(communityId);
        requireAdmin(communityId, admin, community);
        postRepository.findById(postId).ifPresent(post -> {
            if (!communityId.equals(post.getCommunityId()))
                throw new BadRequestException("Post does not belong to this community");
            post.setDeleted(true);
            postRepository.save(post);
        });
    }

    @Transactional
    public void pinPost(Long communityId, Long postId, User admin) {
        Community community = findById(communityId);
        requireAdmin(communityId, admin, community);
        postRepository.findById(postId).ifPresent(post -> {
            if (!communityId.equals(post.getCommunityId()))
                throw new BadRequestException("Post does not belong to this community");
            post.setPinned(!post.isPinned());
            postRepository.save(post);
        });
    }

    // ── Invites ──────────────────────────────────────────────────────────────

    @Transactional
    public InviteDto inviteUser(Long communityId, String username, User admin) {
        Community community = findById(communityId);
        requireAdmin(communityId, admin, community);

        User target = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User not found: " + username));

        if (communityMemberRepository.existsByIdCommunityIdAndIdUserId(communityId, target.getId()))
            throw new BadRequestException("User is already a member of this community");

        if (communityInviteRepository.existsByCommunityIdAndInvitedUserIdAndStatus(communityId, target.getId(), "PENDING"))
            throw new BadRequestException("An invite is already pending for this user");

        CommunityInvite invite = CommunityInvite.builder()
                .community(community).invitedUser(target).invitedBy(admin).build();
        invite = communityInviteRepository.save(invite);

        notificationService.create(target, "INVITE",
                "Community Invite",
                admin.getDisplayUsername() + " invited you to join " + community.getName(),
                "/notifications");

        return toInviteDto(invite);
    }

    public List<InviteDto> getCommunityInvites(Long communityId, User admin) {
        Community community = findById(communityId);
        requireAdmin(communityId, admin, community);
        return communityInviteRepository.findByCommunityIdAndStatus(communityId, "PENDING")
                .stream().map(this::toInviteDto).toList();
    }

    public List<InviteDto> getMyInvites(User user) {
        return communityInviteRepository.findByInvitedUserIdAndStatus(user.getId(), "PENDING")
                .stream().map(this::toInviteDto).toList();
    }

    @Transactional
    public void respondToInvite(String token, boolean accept, User user) {
        CommunityInvite invite = communityInviteRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invite not found"));

        if (!invite.getInvitedUser().getId().equals(user.getId()))
            throw new BadRequestException("This invite is not for you");
        if (!"PENDING".equals(invite.getStatus()))
            throw new BadRequestException("Invite already responded to");
        if (invite.getExpiresAt() != null && invite.getExpiresAt().isBefore(java.time.LocalDateTime.now()))
            throw new BadRequestException("This invite has expired");

        invite.setStatus(accept ? "ACCEPTED" : "DECLINED");
        communityInviteRepository.save(invite);

        if (accept) {
            joinCommunity(invite.getCommunity().getId(), user);
        }
    }

    @Transactional
    public void cancelInvite(Long communityId, Long inviteId, User admin) {
        Community community = findById(communityId);
        requireAdmin(communityId, admin, community);
        CommunityInvite invite = communityInviteRepository.findById(inviteId)
                .orElseThrow(() -> new ResourceNotFoundException("Invite not found"));
        invite.setStatus("CANCELLED");
        communityInviteRepository.save(invite);
    }

    // ── Members List ─────────────────────────────────────────────────────────

    public List<CommunityMemberDto> getMembers(Long communityId) {
        findById(communityId);
        List<MemberTag> allTags = memberTagRepository.findByCommunityId(communityId);
        Map<Long, List<CommunityTagDto>> tagsByUser = allTags.stream()
                .collect(Collectors.groupingBy(mt -> mt.getUser().getId(),
                        Collectors.mapping(mt -> CommunityTagDto.builder()
                                .id(mt.getTag().getId())
                                .name(mt.getTag().getName())
                                .color(mt.getTag().getColor())
                                .build(), Collectors.toList())));

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        return communityMemberRepository.findByCommunityId(communityId).stream().map(m -> {
            CommunityMemberDto dto = communityMapper.toMemberDto(m);
            dto.setTags(tagsByUser.getOrDefault(m.getUser().getId(), List.of()));
            LocalDate lastRun = activityRepository.findLastActivityDateByUserId(m.getUser().getId());
            dto.setLastRunDate(lastRun != null ? lastRun.atStartOfDay() : null);
            dto.setMessageCount30d(messageRepository.countBySenderIdAndCommunityIdAndSentAtAfter(
                    m.getUser().getId(), communityId, thirtyDaysAgo));
            return dto;
        }).toList();
    }

    public List<CommunityMemberDto> getCommunityMembers(Long communityId) {
        return getMembers(communityId);
    }

    // ── Tags ─────────────────────────────────────────────────────────────────

    public List<CommunityTagDto> getCommunityTags(Long communityId) {
        return tagRepository.findByCommunityIdOrderByNameAsc(communityId).stream()
                .map(t -> CommunityTagDto.builder().id(t.getId()).name(t.getName()).color(t.getColor()).build())
                .toList();
    }

    @Transactional
    public CommunityTagDto createTag(Long communityId, String name, String color, User admin) {
        Community community = findById(communityId);
        requireAdmin(communityId, admin, community);
        CommunityTag tag = CommunityTag.builder()
                .community(community).name(name).color(color != null ? color : "#3b82f6").build();
        tag = tagRepository.save(tag);
        return CommunityTagDto.builder().id(tag.getId()).name(tag.getName()).color(tag.getColor()).build();
    }

    @Transactional
    public void deleteTag(Long communityId, Long tagId, User admin) {
        Community community = findById(communityId);
        requireAdmin(communityId, admin, community);
        tagRepository.deleteById(tagId);
    }

    @Transactional
    public void assignTag(Long communityId, Long userId, Long tagId, User admin) {
        Community community = findById(communityId);
        requireAdmin(communityId, admin, community);
        if (memberTagRepository.existsByCommunityIdAndUserIdAndTagId(communityId, userId, tagId)) return;
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        CommunityTag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found"));
        memberTagRepository.save(MemberTag.builder().community(community).user(target).tag(tag).build());
    }

    @Transactional
    public void removeTag(Long communityId, Long userId, Long tagId, User admin) {
        Community community = findById(communityId);
        requireAdmin(communityId, admin, community);
        memberTagRepository.deleteByCommunityIdAndUserIdAndTagId(communityId, userId, tagId);
    }

    @Transactional
    public void batchNotifyInactive(Long communityId, List<Long> userIds, User admin) {
        Community community = findById(communityId);
        requireAdmin(communityId, admin, community);
        for (Long uid : userIds) {
            User target = userRepository.findById(uid).orElse(null);
            if (target != null) {
                notificationService.create(target, "GENERAL",
                        "We miss you!",
                        "Hey " + target.getDisplayUsername() + ", your community \"" + community.getName() + "\" misses your runs! Come back and log one.",
                        "/communities/" + communityId);
            }
        }
    }

    @Transactional
    public void batchKickMembers(Long communityId, List<Long> userIds, User admin) {
        Community community = findById(communityId);
        requireAdmin(communityId, admin, community);
        for (Long uid : userIds) {
            kickMember(communityId, uid, admin);
        }
    }

    // ── Drive Sync ───────────────────────────────────────────────────────────

    public List<DriveFolderDto> getDriveFolders(Long communityId, User user) {
        Community community = findById(communityId);
        requireAdmin(communityId, user, community);

        if (community.getDriveFolderId() == null || community.getDriveFolderId().isBlank())
            throw new BadRequestException("No Google Drive folder configured. Set one in Settings first.");

        return googleDriveService.getSubFolders(community.getDriveFolderId());
    }

    @Transactional
    public PostDto syncDrivePhotos(Long communityId, String subFolderId, String eventName, User user) {
        Community community = findById(communityId);
        requireAdmin(communityId, user, community);

        if (community.getDriveFolderId() == null || community.getDriveFolderId().isBlank())
            throw new BadRequestException("No Google Drive folder configured. Set one in Settings first.");

        List<String> imageUrls = googleDriveService.getImagesFromFolder(subFolderId);
        if (imageUrls.isEmpty())
            throw new BadRequestException("No images found in the folder: " + eventName);

        String caption = "📸 " + eventName;
        return feedService.createPhotoPost(communityId, imageUrls, caption, user);
    }

    @Transactional
    public PostDto syncDrivePhotos(Long communityId, User user) {
        Community community = findById(communityId);
        requireAdmin(communityId, user, community);

        if (community.getDriveFolderId() == null || community.getDriveFolderId().isBlank())
            throw new BadRequestException("No Google Drive folder configured. Set one in Settings first.");

        List<String> imageUrls = googleDriveService.getImagesFromFolder(community.getDriveFolderId());
        if (imageUrls.isEmpty())
            throw new BadRequestException("No images found in the Google Drive folder");

        return feedService.createPhotoPost(communityId, imageUrls, "📸 Photos from Google Drive", user);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void requireAdmin(Long communityId, User user, Community community) {
        String role = getMemberRole(communityId, user.getId());
        boolean isCreator = community.getCreator().getId().equals(user.getId());
        if (!"ADMIN".equals(role) && !isCreator)
            throw new BadRequestException("Only community admins can perform this action");
    }

    private CommunityDto toDtoWithMembership(Community community, User user) {
        CommunityDto dto = communityMapper.toDto(community);
        if (user != null) {
            boolean joined = communityMemberRepository.existsByIdCommunityIdAndIdUserId(community.getId(), user.getId());
            dto.setJoined(joined);
            if (joined) {
                String role = getMemberRole(community.getId(), user.getId());
                dto.setRole(role);
                dto.setAdmin("ADMIN".equals(role) || community.getCreator().getId().equals(user.getId()));
            } else {
                dto.setAdmin(community.getCreator().getId().equals(user.getId()));
            }
            // Pending invite count for admin
            if (dto.isAdmin()) {
                dto.setPendingInviteCount((int) communityInviteRepository
                        .findByCommunityIdAndStatus(community.getId(), "PENDING").size());
            }
        }
        // Sponsors
        dto.setSponsors(sponsorRepository.findByCommunityId(community.getId()).stream()
                .map(s -> { SponsorDto sd = new SponsorDto(); sd.setId(s.getId()); sd.setLogoUrl(s.getLogoUrl()); sd.setLinkUrl(s.getLinkUrl()); sd.setName(s.getName()); return sd; })
                .toList());
        return dto;
    }

    // ── Sponsor Management ──────────────────────────────────────────────────

    @Transactional
    public SponsorDto addSponsor(Long communityId, SponsorDto request, User user) {
        Community community = findById(communityId);
        requireAdmin(communityId, user, community);
        if (sponsorRepository.countByCommunityId(communityId) >= 3) {
            throw new BadRequestException("Maximum 3 sponsors allowed");
        }
        var sponsor = com.runhub.communities.model.CommunitySponsor.builder()
                .community(community)
                .logoUrl(request.getLogoUrl())
                .linkUrl(request.getLinkUrl())
                .name(request.getName())
                .build();
        sponsor = sponsorRepository.save(sponsor);
        SponsorDto dto = new SponsorDto();
        dto.setId(sponsor.getId()); dto.setLogoUrl(sponsor.getLogoUrl());
        dto.setLinkUrl(sponsor.getLinkUrl()); dto.setName(sponsor.getName());
        return dto;
    }

    @Transactional
    public void removeSponsor(Long communityId, Long sponsorId, User user) {
        Community community = findById(communityId);
        requireAdmin(communityId, user, community);
        sponsorRepository.deleteById(sponsorId);
    }

    public List<SponsorDto> getSponsors(Long communityId) {
        return sponsorRepository.findByCommunityId(communityId).stream()
                .map(s -> { SponsorDto sd = new SponsorDto(); sd.setId(s.getId()); sd.setLogoUrl(s.getLogoUrl()); sd.setLinkUrl(s.getLinkUrl()); sd.setName(s.getName()); return sd; })
                .toList();
    }

    private String getMemberRole(Long communityId, Long userId) {
        return communityMemberRepository.findByCommunityId(communityId).stream()
                .filter(m -> m.getUser().getId().equals(userId))
                .map(CommunityMember::getRole).findFirst().orElse(null);
    }

    private InviteDto toInviteDto(CommunityInvite invite) {
        return InviteDto.builder()
                .id(invite.getId())
                .communityId(invite.getCommunity().getId())
                .communityName(invite.getCommunity().getName())
                .communityImageUrl(invite.getCommunity().getImageUrl())
                .invitedUserId(invite.getInvitedUser().getId())
                .invitedUsername(invite.getInvitedUser().getDisplayUsername())
                .invitedByUsername(invite.getInvitedBy().getDisplayUsername())
                .token(invite.getToken())
                .status(invite.getStatus())
                .createdAt(invite.getCreatedAt())
                .expiresAt(invite.getExpiresAt())
                .build();
    }

    private Community findById(Long id) {
        return communityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found: " + id));
    }
}
