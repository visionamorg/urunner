package com.runhub.feed.service;

import com.runhub.communities.model.Community;
import com.runhub.communities.repository.CommunityRepository;
import com.runhub.config.ResourceNotFoundException;
import com.runhub.feed.dto.CommentDto;
import com.runhub.feed.dto.CreatePostRequest;
import com.runhub.feed.dto.PostDto;
import com.runhub.feed.mapper.FeedMapper;
import com.runhub.feed.model.Comment;
import com.runhub.feed.model.Like;
import com.runhub.feed.model.Post;
import com.runhub.feed.repository.CommentRepository;
import com.runhub.feed.repository.LikeRepository;
import com.runhub.feed.repository.PostRepository;
import com.runhub.users.model.User;
import com.runhub.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final FeedMapper feedMapper;
    private final UserService userService;
    private final CommunityRepository communityRepository;

    public Page<PostDto> getPosts(int page, int size, String email) {
        User currentUser = userService.getUserEntityByEmail(email);
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(post -> {
                    PostDto dto = feedMapper.toPostDto(post);
                    dto.setLikedByCurrentUser(likeRepository.existsByPostIdAndUserId(post.getId(), currentUser.getId()));
                    return dto;
                });
    }

    @Transactional
    public PostDto createPost(String email, CreatePostRequest request) {
        User user = userService.getUserEntityByEmail(email);
        Community community = null;
        if (request.getCommunityId() != null) {
            community = communityRepository.findById(request.getCommunityId()).orElse(null);
        }

        Post post = Post.builder()
                .author(user)
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .community(community)
                .build();

        Post saved = postRepository.save(post);
        PostDto dto = feedMapper.toPostDto(saved);
        dto.setLikedByCurrentUser(false);
        return dto;
    }

    @Transactional
    public CommentDto addComment(Long postId, String email, String content) {
        User user = userService.getUserEntityByEmail(email);
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

        return feedMapper.toCommentDto(saved);
    }

    @Transactional
    public PostDto toggleLike(Long postId, String email) {
        User user = userService.getUserEntityByEmail(email);
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
        dto.setLikedByCurrentUser(likeRepository.existsByPostIdAndUserId(postId, user.getId()));
        return dto;
    }

    public List<CommentDto> getComments(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .stream().map(feedMapper::toCommentDto).toList();
    }
}
