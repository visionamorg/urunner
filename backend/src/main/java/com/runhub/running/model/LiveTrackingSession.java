package com.runhub.running.model;

import com.runhub.users.model.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "live_tracking_sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class LiveTrackingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true, length = 64)
    private String token;

    private Double latitude;
    private Double longitude;

    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "garmin_live_track_url", length = 500)
    private String garminLiveTrackUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
