package com.runhub.feed.controller;

import com.runhub.feed.dto.AddCommentRequest;
import com.runhub.feed.dto.CommentDto;
import com.runhub.feed.dto.CreatePostRequest;
import com.runhub.feed.dto.PostDto;
import com.runhub.feed.service.FeedService;
import com.runhub.users.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    // Legacy endpoint kept for backwards compatibility
    @GetMapping("/posts")
    public ResponseEntity<Page<PostDto>> getPostsLegacy(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Principal principal) {
        return ResponseEntity.ok(feedService.getPosts(page, size, principal.getName()));
    }

    @GetMapping("/feed/posts")
    public ResponseEntity<Page<PostDto>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(feedService.getPosts(user, page));
    }

    @PostMapping("/feed/posts")
    public ResponseEntity<PostDto> createPost(
            @AuthenticationPrincipal User user,
            @RequestBody CreatePostRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(feedService.createPost(request, user));
    }

    // Legacy endpoint kept for backwards compatibility
    @PostMapping("/posts")
    public ResponseEntity<PostDto> createPostLegacy(
            Principal principal,
            @RequestBody CreatePostRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(feedService.createPost(principal.getName(), request));
    }

    @GetMapping("/feed/posts/{id}/comments")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long id) {
        return ResponseEntity.ok(feedService.getComments(id));
    }

    @PostMapping("/feed/posts/{id}/comments")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AddCommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(feedService.addComment(id, request.getContent(), user));
    }

    @PostMapping("/feed/posts/{id}/like")
    public ResponseEntity<PostDto> toggleLike(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(feedService.toggleLike(id, user));
    }

    // Legacy endpoints kept for backwards compatibility
    @GetMapping("/posts/{id}/comments")
    public ResponseEntity<List<CommentDto>> getCommentsLegacy(@PathVariable Long id) {
        return ResponseEntity.ok(feedService.getComments(id));
    }

    @PostMapping("/posts/{id}/comments")
    public ResponseEntity<CommentDto> addCommentLegacy(
            @PathVariable Long id,
            Principal principal,
            @RequestBody AddCommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(feedService.addComment(id, principal.getName(), request.getContent()));
    }

    @PostMapping("/posts/{id}/like")
    public ResponseEntity<PostDto> toggleLikeLegacy(
            @PathVariable Long id,
            Principal principal) {
        return ResponseEntity.ok(feedService.toggleLike(id, principal.getName()));
    }
}
