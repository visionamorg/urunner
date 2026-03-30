package com.runhub.coaching.service;

import com.runhub.coaching.dto.CoachingCommentDto;
import com.runhub.coaching.dto.CreateCommentRequest;
import com.runhub.coaching.model.CoachingComment;
import com.runhub.coaching.repository.CoachingCommentRepository;
import com.runhub.coaching.repository.CoachingConnectionRepository;
import com.runhub.config.BadRequestException;
import com.runhub.config.ResourceNotFoundException;
import com.runhub.running.model.RunningActivity;
import com.runhub.running.repository.ActivityRepository;
import com.runhub.users.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoachingCommentService {

    private final CoachingCommentRepository commentRepository;
    private final CoachingConnectionRepository connectionRepository;
    private final ActivityRepository activityRepository;

    @Transactional
    public CoachingCommentDto addComment(User coach, CreateCommentRequest req) {
        RunningActivity activity = activityRepository.findById(req.getActivityId())
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found: " + req.getActivityId()));

        Long athleteId = activity.getUser().getId();
        boolean hasConnection = connectionRepository
                .findByCoachIdAndAthleteId(coach.getId(), athleteId)
                .map(c -> "ACTIVE".equals(c.getStatus()))
                .orElse(false);

        if (!hasConnection) {
            throw new BadRequestException("No active coaching connection with this athlete");
        }

        CoachingComment comment = CoachingComment.builder()
                .coach(coach)
                .activity(activity)
                .content(req.getContent())
                .rating(req.getRating())
                .lapNumber(req.getLapNumber())
                .build();

        return toDto(commentRepository.save(comment));
    }

    public List<CoachingCommentDto> getCommentsForActivity(Long activityId) {
        return commentRepository.findByActivityIdOrderByCreatedAtAsc(activityId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public CoachingCommentDto pinComment(User coach, Long commentId) {
        CoachingComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found: " + commentId));

        if (!comment.getCoach().getId().equals(coach.getId())) {
            throw new BadRequestException("Not authorized to pin this comment");
        }

        comment.setPinnedToAthleteDashboard(true);
        return toDto(commentRepository.save(comment));
    }

    private CoachingCommentDto toDto(CoachingComment c) {
        return CoachingCommentDto.builder()
                .id(c.getId())
                .coachId(c.getCoach().getId())
                .coachUsername(c.getCoach().getDisplayUsername())
                .activityId(c.getActivity().getId())
                .content(c.getContent())
                .rating(c.getRating())
                .lapNumber(c.getLapNumber())
                .pinnedToAthleteDashboard(c.getPinnedToAthleteDashboard())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
