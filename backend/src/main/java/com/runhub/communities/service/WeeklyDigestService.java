package com.runhub.communities.service;

import com.runhub.communities.repository.CommunityMemberRepository;
import com.runhub.communities.repository.CommunityRepository;
import com.runhub.feed.dto.PostDto;
import com.runhub.feed.model.Post;
import com.runhub.feed.repository.PostRepository;
import com.runhub.rankings.dto.RankingDto;
import com.runhub.rankings.service.RankingService;
import com.runhub.running.repository.ActivityRepository;
import com.runhub.users.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeeklyDigestService {

    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final ActivityRepository activityRepository;
    private final RankingService rankingService;
    private final PostRepository postRepository;

    @Transactional
    public PostDto generateDigest(Long communityId, User user) {
        var community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("Community not found"));

        // Verify admin
        var membership = communityMemberRepository.findByCommunityIdAndUserId(communityId, user.getId())
                .orElseThrow(() -> new RuntimeException("Not a member"));
        if (!"ADMIN".equals(membership.getRole())) {
            throw new RuntimeException("Only admins can generate digests");
        }

        List<Long> userIds = communityMemberRepository.findByCommunityId(communityId)
                .stream().map(m -> m.getUser().getId()).toList();

        LocalDate now = LocalDate.now();
        LocalDate weekStart = now.minusDays(7);

        // Gather stats
        Double weeklyDistance = userIds.isEmpty() ? 0.0 :
                activityRepository.sumDistanceByUserIdsAndDateRange(userIds, weekStart, now);
        List<RankingDto> topRunners = rankingService.getCommunityWeeklyChallenge(communityId);
        long totalMembers = userIds.size();
        long activeMembers = topRunners.size();

        // Build digest content
        StringBuilder sb = new StringBuilder();
        sb.append("📊 **Weekly Community Digest** (").append(weekStart).append(" → ").append(now).append(")\n\n");
        sb.append("🏃 **Community Stats**\n");
        sb.append("• ").append(activeMembers).append("/").append(totalMembers).append(" members ran this week\n");
        sb.append("• Total distance: ").append(String.format("%.1f", weeklyDistance != null ? weeklyDistance : 0)).append(" km\n\n");

        if (!topRunners.isEmpty()) {
            sb.append("🏆 **Top Runners This Week**\n");
            for (int i = 0; i < Math.min(5, topRunners.size()); i++) {
                RankingDto r = topRunners.get(i);
                String medal = i == 0 ? "🥇" : i == 1 ? "🥈" : i == 2 ? "🥉" : "  " + (i + 1) + ".";
                sb.append(medal).append(" **").append(r.getUsername()).append("** — ")
                        .append(String.format("%.1f", r.getTotalDistanceKm())).append(" km (")
                        .append(r.getTotalRuns()).append(" runs)\n");
            }
        }

        sb.append("\nKeep pushing, team! 💪");

        // Create a pinned post in the feed
        Post post = Post.builder()
                .author(user)
                .community(community)
                .content(sb.toString())
                .postType("TEXT")
                .pinned(true)
                .build();
        post = postRepository.save(post);

        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setContent(post.getContent());
        dto.setPostType(post.getPostType());
        dto.setPinned(true);
        dto.setAuthorUsername(user.getUsername());
        dto.setCreatedAt(post.getCreatedAt());
        return dto;
    }
}
