package com.runhub.feed.service;

import com.runhub.communities.model.Community;
import com.runhub.communities.repository.CommunityRepository;
import com.runhub.config.ResourceNotFoundException;
import com.runhub.feed.dto.ActivityShareRequest;
import com.runhub.feed.dto.CommentDto;
import com.runhub.feed.dto.CreatePostRequest;
import com.runhub.feed.dto.PostDto;
import com.runhub.feed.mapper.FeedMapper;
import com.runhub.feed.model.Comment;
import com.runhub.feed.model.Like;
import com.runhub.feed.model.Post;
import com.runhub.feed.model.PostReaction;
import com.runhub.feed.repository.CommentRepository;
import com.runhub.feed.repository.LikeRepository;
import com.runhub.feed.repository.PostReactionRepository;
import com.runhub.feed.repository.PostRepository;
import com.runhub.notifications.service.NotificationService;
import com.runhub.running.model.RunningActivity;
import com.runhub.running.repository.ActivityRepository;
import com.runhub.users.model.User;
import com.runhub.users.repository.UserRepository;
import com.runhub.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final PostReactionRepository reactionRepository;
    private final FeedMapper feedMapper;
    private final UserService userService;
    private final CommunityRepository communityRepository;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public Page<PostDto> getPosts(int page, int size, String email) {
        User currentUser = userService.getUserEntityByEmail(email);
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findAllByDeletedFalseOrderByCreatedAtDesc(pageable)
                .map(post -> toPostDtoWithLike(post, currentUser));
    }

    public Page<PostDto> getPosts(User user, int page) {
        Pageable pageable = PageRequest.of(page, 20);
        return postRepository.findByCommunityIsNullAndDeletedFalseOrderByCreatedAtDesc(pageable)
                .map(post -> toPostDtoWithLike(post, user));
    }

    public Page<PostDto> getCommunityPosts(Long communityId, User user, int page) {
        Pageable pageable = PageRequest.of(page, 20);
        return postRepository.findCommunityFeed(communityId, pageable)
                .map(post -> toPostDtoWithLike(post, user));
    }

    @Transactional
    public PostDto createPost(String email, CreatePostRequest request) {
        User user = userService.getUserEntityByEmail(email);
        return createPost(request, user);
    }

    @Transactional
    public PostDto createPost(CreatePostRequest request, User user) {
        Community community = null;
        if (request.getCommunityId() != null) {
            community = communityRepository.findById(request.getCommunityId()).orElse(null);
        }

        String postType = request.getPostType() != null ? request.getPostType() : "TEXT";
        String photoUrlsJson = feedMapper.serializePhotoUrls(request.getPhotoUrls());

        String content = request.getContent() != null ? request.getContent() : "";

        Post post = Post.builder()
                .author(user)
                .content(content)
                .imageUrl(request.getImageUrl())
                .community(community)
                .postType(postType)
                .photoUrls(photoUrlsJson)
                .build();

        Post saved = postRepository.save(post);
        PostDto dto = feedMapper.toPostDto(saved);
        dto.setLiked(false);
        dto.setLikedByCurrentUser(false);
        return dto;
    }

    @Transactional
    public PostDto createPhotoPost(Long communityId, List<String> photoUrls, String caption, User user) {
        Community community = null;
        if (communityId != null) {
            community = communityRepository.findById(communityId).orElse(null);
        }

        String photoUrlsJson = feedMapper.serializePhotoUrls(photoUrls);
        String content = caption != null ? caption : "Photos from Google Drive";

        Post post = Post.builder()
                .author(user)
                .content(content)
                .community(community)
                .postType("PHOTO_ALBUM")
                .photoUrls(photoUrlsJson)
                .build();

        Post saved = postRepository.save(post);
        PostDto dto = feedMapper.toPostDto(saved);
        dto.setLiked(false);
        dto.setLikedByCurrentUser(false);
        return dto;
    }

    @Transactional
    public CommentDto addComment(Long postId, String email, String content) {
        User user = userService.getUserEntityByEmail(email);
        return addComment(postId, content, user);
    }

    @Transactional
    public CommentDto addComment(Long postId, String content, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        Comment comment = Comment.builder()
                .post(post)
                .author(user)
                .content(content)
                .build();

        Comment saved = commentRepository.save(comment);
        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.save(post);

        detectAndNotifyMentions(content, user, post);

        return feedMapper.toCommentDto(saved);
    }

    private void detectAndNotifyMentions(String content, User author, Post post) {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("@(\\w+)");
        java.util.regex.Matcher m = p.matcher(content);
        Set<String> seen = new HashSet<>();
        while (m.find()) {
            String uname = m.group(1);
            if (seen.contains(uname) || uname.equalsIgnoreCase(author.getUsername())) continue;
            seen.add(uname);
            userRepository.findByUsername(uname).ifPresent(target ->
                notificationService.create(
                    target,
                    "MENTION",
                    "You were mentioned",
                    author.getUsername() + " mentioned you in a comment",
                    "/feed"
                )
            );
        }
    }

    @Transactional
    public PostDto toggleLike(Long postId, String email) {
        User user = userService.getUserEntityByEmail(email);
        return toggleLike(postId, user);
    }

    @Transactional
    public PostDto toggleLike(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        likeRepository.findByPostIdAndUserId(postId, user.getId()).ifPresentOrElse(
                like -> {
                    likeRepository.delete(like);
                    post.setLikesCount(Math.max(0, post.getLikesCount() - 1));
                },
                () -> {
                    Like like = Like.builder().post(post).user(user).build();
                    likeRepository.save(like);
                    post.setLikesCount(post.getLikesCount() + 1);
                }
        );

        Post saved = postRepository.save(post);
        PostDto dto = feedMapper.toPostDto(saved);
        boolean liked = likeRepository.existsByPostIdAndUserId(postId, user.getId());
        dto.setLiked(liked);
        dto.setLikedByCurrentUser(liked);
        return dto;
    }

    public List<CommentDto> getComments(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .stream().map(feedMapper::toCommentDto).toList();
    }

    @Transactional
    public PostDto react(Long postId, String emoji, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        reactionRepository.findByPostIdAndUserId(postId, user.getId()).ifPresentOrElse(
                existing -> {
                    if (existing.getEmoji().equals(emoji)) {
                        // Same emoji — remove reaction
                        reactionRepository.delete(existing);
                    } else {
                        // Different emoji — switch reaction
                        existing.setEmoji(emoji);
                        reactionRepository.save(existing);
                    }
                },
                () -> {
                    PostReaction reaction = PostReaction.builder()
                            .post(post).user(user).emoji(emoji).build();
                    reactionRepository.save(reaction);
                }
        );

        return toPostDtoWithLike(post, user);
    }

    @Transactional
    public PostDto createActivityPost(ActivityShareRequest request, User user) {
        RunningActivity activity = activityRepository.findById(request.getActivityId())
            .orElseThrow(() -> new RuntimeException("Activity not found"));
        if (!activity.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not your activity");
        }
        Community community = null;
        if (request.getCommunityId() != null) {
            community = communityRepository.findById(request.getCommunityId()).orElse(null);
        }
        String caption = request.getCaption() != null ? request.getCaption() : activity.getTitle();
        Post post = Post.builder()
            .author(user)
            .content(caption)
            .community(community)
            .postType("ACTIVITY_SHARE")
            .activity(activity)
            .build();
        Post saved = postRepository.save(post);
        PostDto dto = feedMapper.toPostDto(saved);
        dto.setLiked(false);
        dto.setLikedByCurrentUser(false);
        return dto;
    }

    private PostDto toPostDtoWithLike(Post post, User user) {
        PostDto dto = feedMapper.toPostDto(post);
        boolean liked = likeRepository.existsByPostIdAndUserId(post.getId(), user.getId());
        dto.setLiked(liked);
        dto.setLikedByCurrentUser(liked);

        // Reactions
        Map<String, Long> reactions = new LinkedHashMap<>();
        reactionRepository.countByEmojiForPost(post.getId())
                .forEach(row -> reactions.put((String) row[0], (Long) row[1]));
        dto.setReactions(reactions);
        reactionRepository.findByPostIdAndUserId(post.getId(), user.getId())
                .ifPresent(r -> dto.setMyReaction(r.getEmoji()));

        return dto;
    }
}
