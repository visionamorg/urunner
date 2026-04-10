package com.runhub.segments.service;

import com.runhub.segments.dto.SegmentDto;
import com.runhub.segments.model.Segment;
import com.runhub.segments.model.SegmentEffort;
import com.runhub.segments.repository.SegmentEffortRepository;
import com.runhub.segments.repository.SegmentRepository;
import com.runhub.users.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SegmentService {

    private final SegmentRepository segmentRepository;
    private final SegmentEffortRepository effortRepository;

    public List<SegmentDto> getAllSegments(User currentUser) {
        return segmentRepository.findAllByOrderByDistanceKmAsc().stream()
            .map(s -> toDto(s, currentUser))
            .collect(Collectors.toList());
    }

    private SegmentDto toDto(Segment s, User currentUser) {
        List<SegmentEffort> top = effortRepository.findBySegmentIdOrderByElapsedSecondsAsc(s.getId(), PageRequest.of(0, 1));
        SegmentEffort kom = top.isEmpty() ? null : top.get(0);
        Integer myBest = null;
        if (currentUser != null) {
            myBest = effortRepository.findTopBySegmentIdAndUserIdOrderByElapsedSecondsAsc(s.getId(), currentUser.getId())
                .map(SegmentEffort::getElapsedSeconds).orElse(null);
        }
        return SegmentDto.builder()
            .id(s.getId()).name(s.getName()).description(s.getDescription())
            .distanceKm(s.getDistanceKm()).difficulty(s.getDifficulty())
            .komUsername(kom != null ? kom.getUser().getUsername() : null)
            .komElapsedSeconds(kom != null ? kom.getElapsedSeconds() : null)
            .myBestSeconds(myBest)
            .build();
    }

    public List<SegmentDto> getMyEfforts(User user) {
        return effortRepository.findByUserIdOrderByElapsedSecondsAsc(user.getId()).stream()
            .map(e -> SegmentDto.builder()
                .id(e.getSegment().getId())
                .name(e.getSegment().getName())
                .distanceKm(e.getSegment().getDistanceKm())
                .difficulty(e.getSegment().getDifficulty())
                .myBestSeconds(e.getElapsedSeconds())
                .build())
            .distinct()
            .collect(Collectors.toList());
    }

    public List<SegmentDto> getLeaderboard(Long segmentId) {
        return effortRepository.findBySegmentIdOrderByElapsedSecondsAsc(segmentId, PageRequest.of(0, 10))
            .stream().map(e -> SegmentDto.builder()
                .komUsername(e.getUser().getUsername())
                .komElapsedSeconds(e.getElapsedSeconds())
                .build())
            .collect(Collectors.toList());
    }
}
