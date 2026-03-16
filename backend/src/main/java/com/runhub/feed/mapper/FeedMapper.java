package com.runhub.feed.mapper;

import com.runhub.feed.dto.CommentDto;
import com.runhub.feed.dto.PostDto;
import com.runhub.feed.model.Comment;
import com.runhub.feed.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FeedMapper {

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "author.displayUsername", target = "authorUsername")
    @Mapping(source = "author.profileImageUrl", target = "authorProfileImageUrl")
    @Mapping(source = "community.id", target = "communityId")
    @Mapping(target = "likedByCurrentUser", ignore = true)
    PostDto toPostDto(Post post);

    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "author.displayUsername", target = "authorUsername")
    CommentDto toCommentDto(Comment comment);
}
