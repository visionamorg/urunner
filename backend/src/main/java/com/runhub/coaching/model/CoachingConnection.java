package com.runhub.coaching.model;

import com.runhub.users.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "coaching_connections")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class CoachingConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coach_id", nullable = false)
    private User coach;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "athlete_id", nullable = false)
    private User athlete;

    /** BASIC or FULL */
    @Column(name = "garmin_access_level", length = 10, nullable = false)
    @Builder.Default
    private String garminAccessLevel = "BASIC";

    /** PENDING, ACTIVE, REVOKED */
    @Column(length = 10, nullable = false)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "invite_token", length = 64, unique = true)
    private String inviteToken;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
