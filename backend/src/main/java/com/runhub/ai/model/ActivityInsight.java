package com.runhub.ai.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "activity_insights")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ActivityInsight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "activity_id", nullable = false, unique = true)
    private Long activityId;

    @Column(name = "summary_text", columnDefinition = "TEXT")
    private String summaryText;

    @Column(length = 50)
    private String intensity;

    @Column(name = "next_run_suggestion", columnDefinition = "TEXT")
    private String nextRunSuggestion;

    @Column(name = "injury_risk_notes", columnDefinition = "TEXT")
    private String injuryRiskNotes;

    @Column(name = "social_caption", columnDefinition = "TEXT")
    private String socialCaption;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
