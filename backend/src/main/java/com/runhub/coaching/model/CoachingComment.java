package com.runhub.coaching.model;

import com.runhub.running.model.RunningActivity;
import com.runhub.users.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "coaching_comments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class CoachingComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coach_id", nullable = false)
    private User coach;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private RunningActivity activity;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    /** Optional rating 1–10 */
    private Integer rating;

    /** Which lap this comment is about (nullable = whole activity) */
    @Column(name = "lap_number")
    private Integer lapNumber;

    @Column(name = "pinned_to_athlete_dashboard", nullable = false)
    @Builder.Default
    private Boolean pinnedToAthleteDashboard = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
