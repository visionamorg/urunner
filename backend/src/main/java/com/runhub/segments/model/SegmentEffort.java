package com.runhub.segments.model;

import com.runhub.running.model.RunningActivity;
import com.runhub.users.model.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "segment_efforts")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class SegmentEffort {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "segment_id", nullable = false)
    private Segment segment;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private RunningActivity activity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "elapsed_seconds", nullable = false)
    private Integer elapsedSeconds;
    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;
    @PrePersist protected void onCreate() { recordedAt = LocalDateTime.now(); }
}
