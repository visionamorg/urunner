package com.runhub.running.model;

import com.runhub.users.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "running_activities")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class RunningActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "distance_km", nullable = false)
    private Double distanceKm;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "pace_min_per_km")
    private Double paceMinPerKm;

    @Column(name = "activity_date", nullable = false)
    private LocalDate activityDate;

    @Column(length = 300)
    private String location;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ActivitySource source = ActivitySource.MANUAL;

    @Column(name = "external_id")
    private String externalId;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    // Telemetry fields
    @Column(name = "elevation_gain_meters")
    private Integer elevationGainMeters;

    @Column(name = "avg_heart_rate")
    private Integer avgHeartRate;

    @Column(name = "max_heart_rate")
    private Integer maxHeartRate;

    @Column(name = "avg_cadence")
    private Integer avgCadence;

    @Column(name = "map_polyline", columnDefinition = "TEXT")
    private String mapPolyline;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("splitKm ASC")
    @Builder.Default
    private List<ActivitySplit> splits = new ArrayList<>();

    @PrePersist
    @PreUpdate
    public void calculatePace() {
        if (distanceKm != null && distanceKm > 0 && durationMinutes != null) {
            paceMinPerKm = durationMinutes.doubleValue() / distanceKm;
        }
    }
}
